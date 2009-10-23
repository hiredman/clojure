(ns clojure.reader)
;;; TODO
;;; Move ReaderException somewhere so LispReader can be safely deleted
;;; ?
(defn- readI [rdr eof-is-error? eof-value recursive?]
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
          (nil? [x] (= x nil))
          ;/Boots;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
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
          (match-number [s]
            (clojure.lang.LispReader/matchNumber s)),
          (get-macro [ch]
            (condp = (char ch)
              \" string-reader
              \; coment-reader
              \' (wrapping-reader 'clojure.core/quote)
              \@ (wrapping-reader 'clojure.core/deref)
              \^ (wrapping-reader 'clojure.core/meta)
              \` (clojure.lang.LispReader$SyntaxQuoteReader.)
              \~ unquote-reader
              \( list-reader
              \) unmatched-delimited-reader
              \[ vector-reader
              \] unmatched-delimited-reader
              \{ (clojure.lang.LispReader$MapReader.)
              \} unmatched-delimited-reader
              \\ (clojure.lang.LispReader$CharacterReader.)
              \% arg-reader
              \# dispatch-reader
              nil))
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
          (read-number [rdr ch]
            (clojure.lang.LispReader/readNumber rdr (char ch))),
          (macth-symbol [s]
            (clojure.lang.LispReader/matchSymbol s)),
          (read-uncode-char [rdr ch base length exact]
            (clojure.lang.LispReader/readUnicodeChar rdr ch base length exact)),
          (register-arg [n]
            (clojure.lang.LispReader/registerArg n))
          (reader-exception [ln msg]
            (clojure.lang.LispReader$ReaderException. ln msg))
          ;/Wrappers;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
          (var-reader [rdr quo]
            (list 'var (read rdr true nil true)))
          (unmatched-delimited-reader [rdr rightdelim]
            (throw
              (Exception.
                (format "Unmatched delimiter: %c" (char rightdelim)))))
          (whitespace? [ch] (or (Character/isWhitespace ch) (= \, (char ch))))
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
                              (char ch)
                              (condp = (char ch)
                                \t \tab
                                \r \return
                                \n \newline
                                \b \backspace
                                \f \formfeed
                                \u (let [ch (.read rdr)]
                                     (if (= -1 (Character/digit (char ch) 16))
                                       (throw (Exception. (str "Invalid unicode escape: \\u" (char ch))))
                                       (char (read-uncode-char rdr ch 16 4 true))))
                                (if (Character/isDigit ch)
                                  (let [ch (read-uncode-char rdr ch 8 3 false)]
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
              (let [ret (macth-symbol token)]
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
  (read rdr eof-is-error? eof-value recursive?)))

(when *compile-files*
  (with-open [file (-> "reader/reader.properties" java.io.File.
                     java.io.FileWriter. java.io.PrintWriter.)]
    (binding [*out* file]
      (.println *out* (str "reader.name=" (.getName (class readI)))))))
