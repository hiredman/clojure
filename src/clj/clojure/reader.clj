(ns clojure.reader)
;;; TODO
;;; Move ReaderException somewhere so LispReader can be safely deleted
;;; ?

(defmacro eof-guard [ch e]
  `(when (= -1 (int ~ch))
     (throw ~e)))

(defn- readI [rdr eof-is-error? eof-value recursive?]
  (let [regex (fn [s] (java.util.regex.Pattern/compile s))
        intPat (regex "([-+]?)(?:(0)|([1-9][0-9]*)|0[xX]([0-9A-Fa-f]+)|0([0-7]+)|([1-9][0-9]?)[rR]([0-9A-Za-z]+)|0[0-9]+)")
        floatPat (regex "([-+]?[0-9]+(\\.[0-9]*)?([eE][-+]?[0-9]+)?)(M)?")
        ratioPat (regex "([-+]?[0-9]+)/([0-9]+)")
        symbolPat (regex "[:]?([\\D&&[^/]].*/)?([\\D&&[^/]][^/]*)")]
    (letfn [;Boots;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
            (= [x y] (clojure.lang.Util/equiv x y))
            (name [x] (.getName x))
            (count [x] (clojure.lang.RT/count x))
            (> [a b] (clojure.lang.Numbers/gt a b))
            (not [x] (if x false true))
            (instance? [c x] (.isInstance c x))
            (inc [x] (clojure.lang.Numbers/inc x))
            (* [x y] (. clojure.lang.Numbers (multiply x y)))
            (+ [x y] (. clojure.lang.Numbers (add x y)))
            (nil? [x] (if (= x nil) true false))
            ;/Boots;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
            ;Wrappers;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
            (wall-hack [class-name field-name obj]
              (-> class-name (.getDeclaredField (name field-name))
                (doto (.setAccessible true))
                      (.get obj))),
            (arg-env []
              (deref
                (wall-hack clojure.lang.LispReader :ARG_ENV nil))),
            (next-id [] (clojure.lang.RT/nextID)),
            (suppressed-read? [] (clojure.lang.RT/suppressRead)),
            (numbers-reduce [n]
              (clojure.lang.Numbers/reduce n)),
            (numbers-divide [n d]
              (clojure.lang.Numbers/divide n d)),
            (array-list [] (java.util.ArrayList.)),
            (namespace-for [kw]
              (clojure.lang.Compiler/namespaceFor #^clojure.lang.Symbol kw))
            (current-ns [] (deref clojure.lang.RT/CURRENT_NS))
            (keyword-intern<2> [ns kw] (clojure.lang.Keyword/intern ns kw))
            (keyword-intern<1> [kw] (clojure.lang.Keyword/intern kw))
            (symbol-intern [s] (clojure.lang.Symbol/intern s))
            (symbol [s] (clojure.lang.Symbol/create s))
            (syntax-quote-reader []
              (clojure.lang.LispReader$SyntaxQuoteReader.))
            (get-dispatch-macro [ch]
              (condp = (char ch)
                \^ (clojure.lang.LispReader$MetaReader.)
                \' var-reader
                \" (clojure.lang.LispReader$RegexReader.)
                \( (clojure.lang.LispReader$FnReader.)
                \{ (clojure.lang.LispReader$SetReader.)
                \= (clojure.lang.LispReader$EvalReader.)
                \! coment-reader
                \< (clojure.lang.LispReader$UnreadableReader.)
                \_ discard-reader
                nil))
            (read-delimited-list [delim rdr recur?]
              (clojure.lang.LispReader/readDelimitedList delim rdr recur?)),
            (register-arg [n]
              (clojure.lang.LispReader/registerArg n))
            (reader-exception [ln msg]
              (clojure.lang.LispReader$ReaderException. ln msg))
            ;/Wrappers;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
            (get-macro [ch]
              (condp = (char ch)
                \" string-reader
                \; coment-reader
                \' (wrapping-reader 'clojure.core/quote)
                \@ (wrapping-reader 'clojure.core/deref)
                \^ (wrapping-reader 'clojure.core/meta)
                \` (syntax-quote-reader) 
                \~ unquote-reader
                \( list-reader
                \) unmatched-delimited-reader
                \[ vector-reader
                \] unmatched-delimited-reader
                \{ map-reader
                \} unmatched-delimited-reader
                \\ character-reader
                \% arg-reader
                \# dispatch-reader
                nil))
            (character-reader [rdr backslash]
              (let [ch (.read rdr)]
                (eof-guard ch (Exception. "EOF while reading character"))
                (let [token (read-token rdr ch)]
                  (if (= 1 (.length token))
                    (Character/valueOf (.charAt token 0))
                    (condp = token
                      "newline" \n
                      "space" \space
                      "tab" \t
                      "backspace" \b
                      "formfeed" \f
                      "return" \r
                      (cond
                        (.startsWith token "u")
                          (let [c (read-unicode-char<string> token 1 4 16)]
                            (if (and (or (= (int c) (+ 1 (int \uD799)))
                                         (> (int c) (+ 1 (int \uD799))))
                                     (or (= (int c) (- (int \uF000) 1))
                                         (< (int c) (- (int \uF000) 1))))
                              (throw (Exception. (format "Invalid character constant \\u%s" (Integer/toString (int c) 16))))
                              c))
                        (.startsWith token "o")
                          (let [len (- (.length token) 1)]
                            (if (> len 3)
                              (throw (Exception. (format "Invalid octal escape sequence length: %i" len)))
                              (let [uc (read-unicode-char<string> token 1 len 8)]
                                (if (> uc 0377)
                                  (throw (Exception. "Octal escape sequence must be in range [0, 377]."))
                                  uc))))
                        :else
                          (throw (Exception. (format "Unsupported character: \\%s" token)))))))))
            (read-unicode-char<string> [token offset length base]
              (if (not (= (.length token) (+ offset length)))
                (throw (IllegalArgumentException. (format "Invalid unicode character: \\%s" token)))
                (loop [i offset uc 0]
                  (if (> offset i)
                    (let [d (Character/digit (.charAt token i) base)]
                      (eof-guard d (IllegalArgumentException. (format "Invalid digit: %c" (char d))))
                      (recur (inc i) (+ d (* uc base))))
                    uc))))
            (read-unicode-char<pushback> [rdr ch base length exact]
              (letfn [(break [uc i length exact]
                        (if (and exact (not (= i length)))
                          (throw
                            (IllegalArgumentException.
                              (format "Invalid character length: %i should be %i" i length)))
                          uc))]
                (let [uc (Character/digit ch base)]
                  (eof-guard uc (IllegalArgumentException. (format "Invalid digit: %c" (char ch))))
                  (loop [i 1 uc uc]
                    (if (< i length)
                      (break uc i length exact)
                      (let [ch (.read rdr)]
                        (if (or (= -1 (int ch))
                                (whitespace? ch)
                                (macro? ch))
                          (do (.unread rdr ch)
                            (break uc i length exact))
                          (let [d (Character/digit ch base)]
                            (if (= -1 d)
                              (throw
                                (IllegalArgumentException.
                                  (format "Invalid digit: %c" (char ch))))
                              (recur (inc i) (+ d (* uc base)))))))))))),
            (match-symbol [s]
              (let [m (.matcher symbolPat s)]
                (when (.matches m)
                  (let [gc (.groupCount m)
                        ns (.group m 1)
                        name (.group 2)]
                    (if (or (and (not (nil? ns))
                                 (.endsWith ns ":/"))
                            (.endsWith ns ":")
                            (not (= -1 (.indexOf ns "::" 1))))
                      nil
                      (if (.startsWith s "::")
                        (let [ks (symbol (.substring s 2))
                              kns (if (nil? (.ns ks))
                                    (namespace-for ks)
                                    (current-ns))]
                          (keyword-intern<2> (.name (.name kns)) (.name ks)))
                        (let [keyword? (= (.charAt s 0) \:)
                              sym (symbol-intern (.substring s (if keyword? 1 0)))]
                          (when keyword?
                            (keyword-intern<1> sym))))))))),
            (match-number [s]
              (let [m (.matcher intPat s)]
                (if (.matches m)
                  (if (nil? (.group m 2))
                    0
                    (let [negate (= "-" (.group m 1))
                          [radix n] (if-let [n (.group m 3)]
                                      [10 n]
                                      (if-let [n (.group m 4)]
                                        [16 n]
                                        (if-let [n (.group m 5)]
                                          [8 n]
                                          (if-let [n (.group m 7)]
                                            [(Integer/parseInt (.group m 6)) n]
                                            [10 nil]))))]
                      (when (not (nil? n))
                       (let [bn (BigInteger. n radix)]
                         (numbers-reduce
                           (if negate (.negate bn) bn))))))
                  (let [m (.matcher floatPat s)]
                    (if (.matches m)
                      (if (not (nil? (.group m 4)))
                        (BigDecimal. (.group m 1))
                        (Double/parseDouble s))
                      (let [m (.matcher ratioPat s)]
                        (when (.matches m)
                          (numbers-divide
                            (BigInteger. (.group m 1))
                            (BigInteger. (.group m 2)))))))))),
            (var-reader [rdr quo]
              (list 'var (read rdr true nil true))),
            (unmatched-delimited-reader [rdr rightdelim]
              (throw
                (Exception.
                  (format "Unmatched delimiter: %c" (char rightdelim))))),
            (whitespace? [ch] (or (Character/isWhitespace ch) (= \, (char ch)))),
            (arg-reader [rdr pct]
              (if (nil? (arg-env))
                (interpret-token (read-token rdr \%))
                (let [ch (.read rdr)]
                  (.unread rdr ch)
                  (if (or (= (int ch) -1)
                          (whitespace? ch)
                          (terminating-macro? ch))
                    (register-arg 1)
                    (let [n (read rdr true nil true)]
                      (if (= n '&)
                        (register-arg -1)
                        (if (not (number? n))
                          (throw (IllegalStateException. "arg literal must be %, %&, or %integer"))
                          (.intValue (register-arg n))))))))),
            (dispatch-reader [rdr hash]
              (let [ch (.read rdr)]
                (if (= (int ch) -1)
                  (throw (Exception. "EOF while reading character"))
                  (let [fn (get-dispatch-macro ch)]
                    (if (nil? fn)
                      (throw (Exception. (format "No dispatch macro for: %c" (char ch))))
                      (fn rdr ch)))))),
            (unquote-reader [rdr comma]
              (let [ch (.read rdr)]
                (if (= (int ch) -1)
                  (throw (Exception. "EOF while reading character"))
                  (if (= (char ch) \@)
                    (let [o (read rdr true nil true)]
                      (clojure.lang.RT/list 'clojure.core/unquote-splicing o))
                    (do (.unread rdr ch)
                      (clojure.lang.RT/list 'clojure.core/unquote (read rdr true nil true))))))),
            (wrapping-reader [sym]
              (fn [rdr quo]
                (let [o (read rdr true nil true)]
                  (clojure.lang.RT/list sym o))))
            (discard-reader [rdr underscore]
              (read rdr true nil true)
              rdr)
            (coment-reader [rdr semicolon]
              (loop [ch (.read rdr)]
                (if (and (not (= -1 (int ch)))
                         (not (= (char ch) \newline))
                         (not (= (char ch) \return)))
                  (recur (.read rdr))
                  rdr)))
            (string-reader [rdr doublequote]
              (let [sb (StringBuilder.)]
                (loop [ch (.read rdr)]
                  (if (= (char ch) \")
                    (.toString sb)
                    (do
                     (.append sb
                      (if (= (int ch) -1)
                        (throw (Exception. "EOF while reading string"))
                        (if (= \\ (char ch))
                          (let [ch (.read rdr)]
                            (if (= (int ch) -1)
                              (throw (Exception. "EOF while reading string"))
                              (if (or (= (char ch) \")
                                      (= (char ch) \\))
                                ""
                                (condp = (char ch)
                                  \t \tab
                                  \r \return
                                  \n \newline
                                  \b \backspace
                                  \f \formfeed
                                  \u (let [ch (.read rdr)]
                                       (if (= -1 (Character/digit (char ch) 16))
                                         (throw (Exception. (str "Invalid unicode escape: \\u" (char ch))))
                                         (char (read-unicode-char<pushback> rdr ch 16 4 true))))
                                  (if (Character/isDigit ch)
                                    (let [ch (read-unicode-char<pushback> rdr ch 8 3 false)]
                                      (if (> ch 0377)
                                        (throw (Exception. "Octal escape sequence must be in range [0, 377]."))
                                        (char ch)))
                                    (throw (Exception. (str "Unsupported escape character: " (char ch)))))))))
                          (char ch))))
                            (recur (.read rdr))))))),
            (garg [n]
              (symbol (str (if (= -1 n) "rest" (str "p" n)) "__" (next-id)))),
            (terminating-macro? [ch]
              (and (not (= (char ch) \#))
                   (not (nil? (get-macro ch)))))
            (interpret-token [token]
              (condp = token
                "nil" nil
                "true" true
                "false" false
                "/" (symbol "/")
                "clojure.core//" (symbol "clojure.core" "/")
                (let [ret (match-symbol token)]
                  (if (not (nil? ret))
                    ret
                    (throw (Exception. (str "Invalid token: " + token)))))))
            (read-token [rdr ch]
              (let [sb (StringBuilder.)]
                (.append sb (char ch))
                (loop [ch (.read rdr)]
                  (if (or (= (int ch) -1)
                          (whitespace? ch)
                          (terminating-macro? ch))
                    (do (.unread rdr ch)
                      (.toString sb))
                    (do (.append sb (char ch))
                      (recur (.read rdr)))))))
            (read-number [rdr initch]
              (let [sb (StringBuilder.)]
                (.append sb (char initch))
                (loop [ch (.read rdr)]
                  (if (or (= (int ch) -1)
                          (whitespace? ch)
                          (macro? ch))
                    (do (.unread rdr ch)
                      (let [s (.toString sb)
                            n (match-number s)]
                        (if (nil? n)
                          (throw (NumberFormatException. (.concat "Invalid number: " s)))
                          n)))
                    (do (.append sb (char ch))
                      (recur (.read rdr)))))))
            (map-reader [rdr leftparen]
              (let [line (if (instance? clojure.lang.LineNumberingPushbackReader rdr)
                           (.getLineNumber rdr)
                           -1)
                    list (read-delimited-list \} rdr true)]
                (if (.isEmpty list)
                  ()
                  (let [s (clojure.lang.RT/map (.toArray list))]
                    (if (not (= -1 line))
                      (.withMeta s {:line line})
                      s)))))
            (vector-reader [rdr leftparen]
              (let [line (if (instance? clojure.lang.LineNumberingPushbackReader rdr)
                           (.getLineNumber rdr)
                           -1)
                    list (read-delimited-list \] rdr true)]
                (if (.isEmpty list)
                  ()
                  (let [s (clojure.lang.LazilyPersistentVector/create list)]
                    (if (not (= -1 line))
                      (.withMeta s {:line line})
                      s)))))
            (list-reader [rdr leftparen]
              (let [line (if (instance? clojure.lang.LineNumberingPushbackReader rdr)
                           (.getLineNumber rdr)
                           -1)
                    list (read-delimited-list \) rdr true)]
                (if (.isEmpty list)
                  ()
                  (let [s (clojure.lang.PersistentList/create list)]
                    (if (not (= -1 line))
                      (.withMeta s {:line line})
                      s))))),
            (macro? [ch] (not (nil? (get-macro ch))))
            (read [rdr eof-error? eof-value recursive?]
              (try
                (loop [ch (.read rdr)]
                  (if (whitespace? ch)
                    (recur (.read rdr))
                    (if (= -1 (int ch))
                      (if eof-error?
                        (throw (Exception. "EOF while reading"))
                        eof-value)
                      (if (Character/isDigit ch)
                        (let [n (read-number rdr (char ch))]
                          (if (suppressed-read?)
                            nil
                            n))
                        (let [macro-fn (get-macro (int ch))]
                          (if (not (nil? macro-fn))
                            (let [ret (macro-fn rdr (char ch))]
                              (if (suppressed-read?)
                                nil
                                (if (= ret rdr)
                                  (recur (.read rdr))
                                  ret)))
                            (if (or (= (char ch) \+)
                                    (= (char ch) \-))
                              (let [ch2 (.read rdr)]
                                (if (Character/isDigit ch2)
                                  (do (.unread rdr ch2)
                                    (let [n (read-number rdr ch)]
                                      (if (suppressed-read?)
                                        nil
                                        n)))
                                  (do (.unread rdr ch2)
                                    (read-token-sub rdr ch))))
                              (read-token-sub rdr ch))))))))
                (catch Exception e
                  (if (or recursive?
                          (not (instance? clojure.lang.LineNumberingPushbackReader rdr)))
                    (throw e)
                    (throw (reader-exception (.getLineNumber rdr) e))))))
            (read-token-sub [rdr ch]
              (let [token (read-token rdr (char ch))]
                (if (suppressed-read?)
                  nil
                  (interpret-token token))))]
  (read rdr eof-is-error? eof-value recursive?))))

(when *compile-files*
  (with-open [file (-> "reader/reader.properties" java.io.File.
                     java.io.FileWriter. java.io.PrintWriter.)]
    (binding [*out* file]
      (.println *out* (str "reader.name=" (.getName (class readI)))))))

;;(defmacro lett [bindings & body]
;;  (let [[name value & xs] bindings]
;;    (if (not (nil? name))
;;      `((fn [~name] (lett ~(vec xs) ~@body)) ~value)
;;      `(do ~@body))))
