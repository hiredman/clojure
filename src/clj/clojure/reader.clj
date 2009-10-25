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
        symbolPat (regex "[:]?([\\D&&[^/]].*/)?([\\D&&[^/]][^/]*)")
        GENSYM_ENV (clojure.lang.Var/create nil)
        ARG_ENV (clojure.lang.Var/create nil)]
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
            (format [fmt & args] (String/format fmt (.toArray args)))
            ;(symbol? [o] (instance? clojure.lang.Symbol o))
            ;(keyword? [o] (instance? clojure.lang.Keyword o))
            ;(map? [o] (instance? clojure.lang.IPersistentMap o))
            ;(string? [o] (instance? String o))
            ;(namespace [o] (.getNamespace o))
            ;(with-meta [o m] (.withMeta o m))
            ;/Boots;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
            ;Wrappers;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
            (wall-hack [what & args]
              (letfn [(wall-hack-field [class-name field-name obj]
                        (-> class-name (.getDeclaredField (name field-name))
                          (doto (.setAccessible true)) (.get obj)))
                      (wall-hack-method [class-name method-name types obj & args]
                        (-> class-name (.getDeclaredMethod (name method-name)
                                                           (into-array Class types))
                          (doto (.setAccessible true))
                          (.invoke obj (into-array Object args))))]
                     (condp = what
                       :method
                        (apply wall-hack-method args)
                       :field
                        (apply wall-hack-field args)
                       :else
                        (throw (IllegalArgumentException. "boo"))))),
            (next-id [] (clojure.lang.RT/nextID)),
            (suppressed-read? [] (clojure.lang.RT/suppressRead)),
            (numbers-reduce [n] (clojure.lang.Numbers/reduce n)),
            (numbers-divide [n d] (clojure.lang.Numbers/divide n d)),
            (array-list [] (java.util.ArrayList.)),
            (string-builder [] (StringBuilder.)),
            (append [sb thing] (.append sb thing)),
            (namespace-for [kw]
              (clojure.lang.Compiler/namespaceFor #^clojure.lang.Symbol kw)),
            (resolve-symbol [sym]
              (clojure.lang.Compiler/resolveSymbol sym)),
            (current-ns [] (deref clojure.lang.RT/CURRENT_NS)),
            (keyword-intern<2> [ns kw] (clojure.lang.Keyword/intern ns kw)),
            (keyword-intern<1> [kw] (clojure.lang.Keyword/intern kw)),
            (symbol-intern [s] (clojure.lang.Symbol/intern s)),
            (register-arg [n]
              (let [argsyms (deref ARG_ENV)]
                (if (nil? argsyms)
                  (throw (IllegalArgumentException. "arg literal not in #()"))
                  (let [ret (val-at argsyms n)]
                    (if (nil? ret)
                      (let [ret (garg n)]
                        (.set ARG_ENV (assoc argsyms n ret))
                        ret)
                      ret))))),
            (push-thread-bindings [map]
              (clojure.lang.Var/pushThreadBindings map)),
            (pop-thread-bindings [] (clojure.lang.Var/popThreadBindings)),
            (special-form? [form] (clojure.lang.Compiler/isSpecial form)),
            (val-at [a b] (.valAt a b)),
            (to-string [x] (.toString x)),
            ;
            (reader-exception [ln msg]
              (clojure.lang.LispReader$ReaderException. ln msg)),
            (regex-reader [rdr doublequote]
              ((clojure.lang.LispReader$RegexReader.) rdr (char doublequote)))
            (eval-reader [] (clojure.lang.LispReader$EvalReader.))
            (read-delimited-list [delim rdr recur?]
              (clojure.lang.LispReader/readDelimitedList delim rdr recur?))
            ;/Wrappers;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
            (get-dispatch-macro [ch]
              (condp = (char ch)
                \^ meta-reader 
                \' var-reader
                \" regex-reader
                \( fn-reader
                \{ set-reader
                \= (eval-reader)
                \! coment-reader
                \< unreadable-reader
                \_ discard-reader
                nil))
            (fn-reader [rdr lparen]
              (letfn [(hiarg [i args hiarg argsyms]
                        (if (> (inc hiarg) i)
                          (let [sym (val-at argsyms i)]
                            (recur (inc i)
                                   (conj args
                                         (if (nil? sym)
                                           (garg i)
                                           sym))
                                   hiarg
                                   args))
                          args))]
                (when (not (nil? (deref ARG_ENV)))
                  (throw (IllegalArgumentException. "Nested #()s are not allowed")))
                (try
                  (push-thread-bindings {ARG_ENV {}})
                  (.unread rdr (first "("))
                  (let [form (read rdr true nil true)
                        argsyms (deref ARG_ENV)
                        rargs (rseq argsyms)
                        args (hiarg 1 [] (key (first rargs)) argsyms)
                        restsym (val-at argsyms -1)]
                    (list 'fn*
                          (if (not (nil? restsym))
                            (conj args '& restsym)
                            args)
                          form))
                  (finally
                    (pop-thread-bindings))))),
            (meta-reader [rdr caret]
              (let [line (if (instance? clojure.lang.LineNumberingPushbackReader rdr)
                           (.getLineNumber rdr)
                           -1)
                    meta (let [meta (read rdr true nil true)]
                           (if (or (symbol? meta)
                                   (keyword? meta)
                                   (string? meta))
                             {:tag meta}
                             (if (not (map? meta))
                               (throw (IllegalArgumentException. "Metadata must be  Symbol, Keyword, String, or Map"))
                               meta)))
                    o (read rdr true nil true)]
                (let [meta (if (and (instance? clojure.lang.ISeq o) (not (= -1 line)))
                             (assoc meta :line line))]
                  (if (instance? clojure.lang.IMeta o)
                    (if (instance? clojure.lang.IReference o)
                      (reset-meta! o meta)
                      (with-meta o meta))
                    (throw (IllegalArgumentException. "Metadata can only be applied to IMetas"))))))
            (character? [ch] (instance? Character ch))
            (p [x] (.println System/err (to-string x)))
            (unquote? [form]
              (and (instance? clojure.lang.ISeq form)
                   (= (first form) 'clojure.core/unquote)))
            (unquote-splicing? [form]
              (and (instance? clojure.lang.ISeq form)
                   (= (first form) 'clojure.core/unquote-splicing)))
            (syntax-quote-reader [rdr backquote]
              (letfn [(syntax-quote [form]
                        (letfn [(gs-symbol-name [sym]
                                  (symbol
                                    (format "%s__%i__auto__"
                                            (.substring (name sym)
                                                        0
                                                        (- (.length sym) 1))
                                            (next-id)))),
                                (sreturn [sym] (list 'clojure.core/quote sym))
                                (symbol-stuff []
                                  (if (and (nil? (namespace form))
                                           (.endsWith (name form) "#"))
                                    (let [gmap (deref GENSYM_ENV)]
                                      (if (nil? gmap)
                                        (throw (IllegalStateException. "Gensym literal not in syntax-quote"))
                                        (let [gs (get gmap form (gs-symbol-name form))]
                                          (.set GENSYM_ENV (assoc gmap form gs))
                                          (sreturn gs))))
                                    (if (and (nil? (namespace form))
                                             (.endsWith (name form) "."))
                                      (let [csym (resolve-symbol (symbol (.substring (name form) 0 (- (.length (name form)) 1))))]
                                        (sreturn (symbol (.concat (name csym) "."))))
                                      (if (and (nil? (namespace form))
                                               (.startsWith (name form) "."))
                                        (sreturn form)
                                        (let [maybe-class (when (not (nil? (namespace form)))
                                                           (.getMapping (current-ns) (symbol (namespace form))))]
                                          (if (instance? Class maybe-class)
                                            (sreturn (symbol (.getName maybe-class) (name form)))
                                            (sreturn (resolve-symbol form)))))))),
                                (Finstance? [thing class] (instance? class thing))
                                (flatten-map [m]
                                  (reduce (fn [v [ke va]] (conj v ke va)) [] m))
                                (seq-expand-list [xs]
                                 (seq
                                   (loop [[item & items] xs ret []]
                                     (if (and (nil? item)
                                              (empty? items))
                                       ret
                                       (cond
                                         (unquote? item)
                                          (recur items (conj ret (list 'clojure.core/list (second item))))
                                         (unquote-splicing? item)
                                          (recur items (conj ret (second item)))
                                         :else
                                          (recur items (conj ret (list 'clojure.core/list (syntax-quote item))))))))),
                                (seq-or-list [form]
                                  (let [s (seq form)]
                                    (if (nil? s)
                                      (cons 'clojure.core/list nil)
                                      (list 'clojure.core/seq
                                            (cons 'clojure.core/concat
                                                  (seq-expand-list s)))))),
                                (collection-stuff []
                                  (condp Finstance? form
                                    clojure.lang.IPersistentMap
                                      (let [keyvals (flatten-map form)]
                                        (list 'clojure.core/apply clojure.core/hash-map
                                              (list 'clojure.core/seq
                                                    (cons 'clojure.core/concat
                                                          (seq-expand-list (seq keyvals))))))
                                    clojure.lang.IPersistentVector
                                      (list 'clojure.core/apply
                                            'clojure.core/vector
                                            (list 'clojure.core/seq
                                                  (cons 'clojure.core/concat
                                                        (seq-expand-list (seq form)))))
                                    clojure.lang.IPersistentSet
                                      (list 'clojure.core/apply
                                            'clojure.core/hash-set
                                            (list 'clojure.core/seq
                                                  (cons 'clojure.core/concat
                                                        (seq-expand-list (seq form)))))
                                    clojure.lang.IPersistentList
                                      (seq-or-list form)
                                    clojure.lang.ISeq
                                      (seq-or-list form)
                                    :else
                                      (throw (UnsupportedOperationException. "Unknown Collection type")))),]
                          (let [ret (cond
                                      (special-form? form)
                                        (list 'quote form)
                                      (symbol? form)
                                        (symbol-stuff)
                                      (unquote? form)
                                        (second form)
                                      (unquote-splicing? form)
                                        (throw (IllegalStateException. "splice not in list"))
                                      (coll? form)
                                        (collection-stuff)
                                      (or (keyword? form)
                                          (number? form)
                                          (character? form)
                                          (string? form))
                                        form
                                      :else
                                        (list 'quote form))]
                            (if (and (instance? clojure.lang.IObj form)
                                     (not (nil? (meta form)))
                                     (> (count (dissoc (meta form) :line)) 0))
                              (list 'clojure.core/with-meta
                                    ret
                                    (syntax-quote (meta form)))
                              ret))))]
                (try
                  (push-thread-bindings {GENSYM_ENV {}})
                  (let [o (read rdr true nil true)]
                    (syntax-quote o))
                  (finally
                    (pop-thread-bindings))))),
            (unreadable-reader [rdr leftangle]
              (throw (Exception. "Unreadable form"))),
            (get-macro [ch]
              (condp = (char ch)
                \" string-reader
                \; coment-reader
                \' (wrapping-reader 'quote)
                \@ (wrapping-reader 'clojure.core/deref)
                \^ (wrapping-reader 'clojure.core/meta)
                \` syntax-quote-reader 
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
                nil)),
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
                          (throw (Exception. (format "Unsupported character: \\%s" token))))))))),
            (read-unicode-char<string> [token offset length base]
              (if (not (= (.length token) (+ offset length)))
                (throw (IllegalArgumentException. (format "Invalid unicode character: \\%s" token)))
                (loop [i offset uc 0]
                  (if (> offset i)
                    (let [d (Character/digit (.charAt token i) base)]
                      (eof-guard d (IllegalArgumentException. (format "Invalid digit: %c" (char d))))
                      (recur (inc i) (+ d (* uc base))))
                    uc)))),
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
                        name- (.group m 2)]
                    (if (and (not (nil? ns))
                             (or (.endsWith ns ":/")
                                 (.endsWith name- ":")
                                 (not (= -1 (.indexOf s "::" 1)))))
                      nil
                      (if (.startsWith s "::")
                        (let [ks (symbol (.substring s 2))
                              kns (if (not (nil? (namespace ks)))
                                    (namespace-for ks)
                                    (current-ns))]
                          (keyword-intern<2> (name (name kns)) (name ks)))
                        (let [keyword? (= (.charAt s 0) \:)
                              sym (symbol-intern (.substring s (if keyword? 1 0)))]
                          (if keyword?
                            (keyword-intern<1> sym)
                            sym)))))))),
            (match-number [s]
              (let [m (.matcher intPat s)]
                (if (.matches m)
                  (if (not (nil? (.group m 2)))
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
              (if (nil? (deref ARG_ENV))
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
              (let [sb (string-builder)]
                (loop [ch (.read rdr)]
                  (if (= (char ch) \")
                    (to-string sb)
                    (do
                     (append sb
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
                    (throw (Exception. (format "Invalid token: %s" token))))))),
            (read-token [rdr ch]
              (let [sb (string-builder)]
                (append sb (char ch))
                (loop [ch (.read rdr)]
                  (if (or (= (int ch) -1)
                          (whitespace? ch)
                          (terminating-macro? ch))
                    (do (.unread rdr ch)
                      (to-string sb))
                    (do (append sb (char ch))
                      (recur (.read rdr))))))),
            (read-number [rdr initch]
              (let [sb (string-builder)]
                (append sb (char initch))
                (loop [ch (.read rdr)]
                  (if (or (= (int ch) -1)
                          (whitespace? ch)
                          (macro? ch))
                    (do (.unread rdr ch)
                      (let [s (to-string sb)
                            n (match-number s)]
                        (if (nil? n)
                          (throw (NumberFormatException. (format "Invalid number: %s" s)))
                          n)))
                    (do (append sb (char ch))
                      (recur (.read rdr)))))))
            (map-reader [rdr leftparen]
              (let [x (clojure.lang.RT/map (.toArray (read-delimited-list \} rdr true)))]
                x)),
            (set-reader [rdr leftparen]
              (clojure.lang.PersistentHashSet/create (read-delimited-list \} rdr true))),
            (vector-reader [rdr leftparen]
              (clojure.lang.LazilyPersistentVector/create (read-delimited-list \] rdr true)))
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
                                (if (identical? ret rdr)
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
