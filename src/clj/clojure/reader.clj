(ns clojure.reader)
;;; TODO
;;; Move ReaderException somewhere so LispReader can be safely deleted
;;; ?

(doseq [sym '(read = name namespace with-meta map? push-thread-bindings pop-thread-bindings count list)]
  (ns-unmap *ns* sym))

;java stuff
(defmacro to-string [x] `(.toString ~x))

(defmacro regex [x] `(java.util.regex.Pattern/compile ~x))

(defmacro name [x] `(.getName ~x))

(defmacro namespace [x] `(.getNamespace ~x))

(defmacro with-meta [o m] `(.withMeta ~o ~m))

(defmacro val-at [a b] `(.valAt ~a ~b))

(defmacro array-list [] `(java.util.ArrayList.))
(defmacro add [a b] `(.add ~a ~b))

(defmacro string-builder [] `(StringBuilder.))
(defmacro append [a b] `(.append ~a ~b))

(defmacro dot-read [rdr] `(.read ~rdr))

(defmacro dot-unread [rdr ch] `(.unread ~rdr ~ch))

;clojure.lang.*
(defmacro class-for-name [name] `(clojure.lang.RT/classForName ~name))

(defmacro = [a b] `(clojure.lang.Util/equiv ~a ~b))

(defmacro invoke-constructor [class args] `(clojure.lang.Reflector/invokeConstructor ~class ~args))

(defmacro invoke-static [class name args] `(clojure.lang.Reflector/invokeStaticMethod ~class ~name ~args))

(defmacro namespace-for [kw] `(clojure.lang.Compiler/namespaceFor ~kw))

(defmacro static-member-name? [x] `(clojure.lang.Compiler/namesStaticMember ~x))

(defmacro map? [o] `(~'instance? clojure.lang.IPersistentMap ~o))

(defmacro special-form? [form] `(clojure.lang.Compiler/isSpecial ~form))

(defmacro suppressed-read? [] '(clojure.lang.RT/suppressRead))

(defmacro numbers-reduce [n] `(clojure.lang.Numbers/reduce ~n))

(defmacro numbers-divide [n d] `(clojure.lang.Numbers/divide ~n ~d))

(defmacro current-ns [] `(deref clojure.lang.RT/CURRENT_NS))

(defmacro resolve-symbol [sym] `(clojure.lang.Compiler/resolveSymbol ~sym))

(defmacro keyword-intern<2> [ns kw] `(clojure.lang.Keyword/intern ~ns ~kw))

(defmacro keyword-intern<1> [kw] `(clojure.lang.Keyword/intern ~kw))

(defmacro symbol-intern [s] `(clojure.lang.Symbol/intern ~s))

(defmacro push-thread-bindings [map] `(clojure.lang.Var/pushThreadBindings ~map))

(defmacro pop-thread-bindings [] `(clojure.lang.Var/popThreadBindings))

(defmacro reader-exception [ln msg] `(clojure.lang.RT$ReaderException. ~ln ~msg))

(defmacro create-var [x] `(clojure.lang.Var/create ~x))

(defmacro next-id [] `(clojure.lang.RT/nextID))

(defmacro count [x] `(clojure.lang.RT/count ~x))

(defmacro list [& foo] `(clojure.lang.RT/list ~@foo))

(defmacro arg-env [] `(clojure.lang.LispReader/ARG_ENV))

;reader stuff
(defmacro get-class [x] `(.getClass ~x))

(defmacro println-to-error [string] `(.println System/err ~string))

(defmacro eof-guard [ch e]
  `(when (= -1 (int ~ch))
     (throw ~e)))

(defmacro p [thing]
  `(when *trace-reader* (println-to-error (to-string ~thing))))

(defmacro p! [thing]
  `(println-to-error (to-string ~thing)))

(defmacro pre-post-p [pre post & body]
  `(do
     (p ~pre)
     (let [x# (do ~@body)]
       (p ~post)
       x#)))

(defmacro safe-to-array [x]
  `(pre-post-p "enter safe-to-array" "exit safe-to-array"
     (let [x# ~x]
      (if x#
        (.toArray x#)
        (.toArray [])))))

(defmacro type-info [x]
  `(let [x# ~x
         [a# b#] (if x# [(to-string x#) (to-string (get-class x#))] ["null" "null"])]
     (p (~'format "%s : %s" a# b#))
     x#))

(defn read [rdr eof-is-error? eof-value recursive?]
  ;(.alterRoot (clojure.lang.Var/find 'clojure.core/*trace-reader*) (fn [& _] true) nil)
  (let [intPat (regex "([-+]?)(?:(0)|([1-9][0-9]*)|0[xX]([0-9A-Fa-f]+)|0([0-7]+)|([1-9][0-9]?)[rR]([0-9A-Za-z]+)|0[0-9]+)")
        floatPat (regex "([-+]?[0-9]+(\\.[0-9]*)?([eE][-+]?[0-9]+)?)(M)?")
        ratioPat (regex "([-+]?[0-9]+)/([0-9]+)")
        symbolPat (regex "[:]?([\\D&&[^/]].*/)?([\\D&&[^/]][^/]*)")
        GENSYM_ENV (create-var nil)]
    (letfn [;Boots;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
            (eq [a b] (= a b))
            (> [a b] (clojure.lang.Numbers/gt a b))
            (not [x] (if x false true))
            (inc [x] (clojure.lang.Numbers/inc x))
            (* [x y] (. clojure.lang.Numbers (multiply x y)))
            (+ [x y] (. clojure.lang.Numbers (add x y)))
            (nil? [x] (if (= x nil) true false))
            (format [fmt & args] (String/format fmt (.toArray args)))
            (instance? [c x] (.isInstance c x))
            (symbol? [o] (instance? clojure.lang.Symbol o))
            (keyword? [o] (instance? clojure.lang.Keyword o))
            (string? [o] (instance? String o))
            (character? [ch] (instance? Character ch))
            ;/Boots;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
            ;Wrappers;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
            (wall-hack [what & args]
              (p "enter wall-hack")
              (letfn [(wall-hack-field [class-name field-name obj]
                        (-> class-name (.getDeclaredField (name field-name))
                          (doto (.setAccessible true)) (.get obj)))
                      (wall-hack-method [class-name method-name types obj & args]
                        (-> class-name (.getDeclaredMethod (name method-name)
                                                           (into-array Class types))
                          (doto (.setAccessible true))
                          (.invoke obj (into-array Object args))))]
                     (condp eq what
                       :method
                        (apply wall-hack-method args)
                       :field
                        (apply wall-hack-field args)
                       :else
                        (throw (IllegalArgumentException. "boo"))))),
            (arg-reader [rdr pct]
               (p "ARG-READER")
               ((clojure.lang.LispReader$ArgReader.) rdr (char pct)))
            (syntax-quote-reader [a b]
              (p "SYNTAX-QUOTE-READER")
              ((clojure.lang.LispReader$SyntaxQuoteReader.) a b))
            ;/Wrappers;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
            (fn-reader [rdr lparen]
              (p "FN-READER")
              (letfn [(rest-args1 [higharg args argsyms]
                        (if (> higharg 0)
                          (loop [i 1 args args]
                            (if (or (> higharg i)
                                    (= higharg i))
                               (let [sym (val-at argsyms i)
                                     sym (if (nil? sym) (garg i) sym)]
                                (recur (inc i) (conj args sym)))
                              args))
                          args))
                      (rest-args2 [args argsyms]
                        (let [restsym (val-at argsyms -1)]
                          (if (not (nil? restsym))
                            (conj (conj args '&) restsym)
                            args)))
                      (rest-args [rargs args argsyms]
                        (let [higharg (.getKey (.first rargs))]
                          (rest-args2 (rest-args1 higharg args argsyms) argsyms)))]
                (if (not (nil? (deref clojure.lang.LispReader/ARG_ENV)))
                  (throw (IllegalStateException.  "Nested #()s are not allowed"))
                  (try
                    (push-thread-bindings {(arg-env) clojure.lang.PersistentTreeMap/EMPTY})
                    (.unread rdr (int \())
                    (let [form (read rdr true nil true)
                          argsyms (deref (arg-env))
                          rargs (.rseq argsyms)
                          args (if (not (nil? rargs)) (rest-args rargs [] argsyms) [])]
                      (list 'fn* args form))
                    (finally (pop-thread-bindings))))))
            (read-delimited-list [delim rdr recur?]
              (p (format "READ-DELIMITED-LIST %s %s %s" (to-string delim) (to-string rdr) (to-string recur?)))
              (let [a (array-list)]
                (loop [ch (dot-read rdr)]
                  (if (whitespace? ch)
                    (recur (dot-read rdr))
                    (do (eof-guard ch (Exception. "EOF while reading"))
                      (if (= delim (char ch))
                          a
                        (let [macro-fn (get-macro ch)]
                          (if (not (nil? macro-fn))
                            (let [mret (macro-fn rdr (char ch))]
                              (when (not (identical? rdr mret))
                                (add a mret))
                              (recur (dot-read rdr)))
                            (do (dot-unread rdr ch)
                              (let [o (read rdr true nil recur?)]
                                (when (not (identical? rdr o))
                                  (add a o))
                                (recur (dot-read rdr))))))))))))
            (eval-reader [rdr eq]
              (p "eval-reader")
              (when (not *read-eval*)
                (throw (Exception. "eval-reader not allowed when *read-eval* is false.")))
              (let [o (read rdr true nil true)]
                (if (symbol? o)
                  (class-for-name o)
                  (if (list? o)
                    (let [fs (first o)]
                      (if (= 'var fs)
                         (clojure.lang.RT/var (namespace (second o)) (name (second o)))
                        (if (.endsWith (name fs) ".")
                          (invoke-constructor
                            (class-for-name
                              (.substring (name fs)
                                          0
                                          (+ (.length (name fs)) -1)))
                            (safe-to-array (next o)))
                          (if (static-member-name? fs)
                            (invoke-static
                              (namespace fs)
                              (name fs)
                              (safe-to-array (next o)))
                            (let [v (clojure.lang.Compiler/maybeResolveIn
                                      (current-ns)
                                      fs)]
                              (if (var? v)
                                (.applyTo v (next o))
                                (throw (Exception. (format "Can't resolve %s" (to-string fs))))))))))
                    (throw (IllegalArgumentException. "Unsupported #= form"))))))
            (regex-reader [rdr doublequote]
              (p "REGEX-READER")
              (let [sb (string-builder)]
                (loop [ch (dot-read rdr)]
                  (if (not (= (char ch) \"))
                    (if (= (int ch) -1)
                      (throw (Exception. "EOF while reading regex"))
                      (do (append sb (char ch))
                        (when (= (char ch) \\)
                          (let [ch (dot-read rdr)]
                            (when (= (int ch) -1)
                              (throw (Exception. "EOF while reading regex")))
                            (append sb (char ch))))
                        (recur (dot-read rdr))))
                    (regex (to-string sb))))))
            (get-dispatch-macro [ch]
              (condp eq (char ch)
                \^ meta-reader 
                \' var-reader
                \" regex-reader
                \( fn-reader
                \{ set-reader
                \= eval-reader
                \! coment-reader
                \< unreadable-reader
                \_ discard-reader
                nil))
            (meta-reader [rdr caret]
              (p "enter meta-reader")
              (let [line (if (instance? clojure.lang.LineNumberingPushbackReader rdr)
                           (.getLineNumber rdr)
                           -1)
                    meta (read rdr true nil true)]
                (p (format "meta-reader %s" (to-string meta)))
                (letfn [(meta-tag [meta line] (meta-map {:tag meta} line))
                        (meta-map [meta line]
                          (let [o (read rdr true nil true)]
                            (if (instance? clojure.lang.IMeta o)
                              (if (and (not (= -1 line)) (instance? clojure.lang.ISeq o))
                                (line-meta-data o meta line)
                                (add-meta o meta))
                              (throw (IllegalArgumentException. "Metadata can only be applied to IMetas")))))
                        (line-meta-data [obj meta line]
                          (add-meta obj (assoc meta :line line)))
                        (add-meta [obj meta]
                          (if (instance? clojure.lang.IReference obj)
                            (do (reset-meta! obj meta) obj)
                            (with-meta obj meta)))]
                  (if (or (symbol? meta)
                          (keyword? meta)
                          (string? meta))
                    (meta-tag meta line)
                    (if (not (map? meta))
                      (throw (IllegalArgumentException. "Metadata must be a Symbol, Keyword, or Map"))
                      (meta-map meta line))))))
            (unquote? [form]
              (and (instance? clojure.lang.ISeq form)
                   (= (first form) 'clojure.core/unquote)))
            (unquote-splicing? [form]
              (and (instance? clojure.lang.ISeq form)
                   (= (first form) 'clojure.core/unquote-splicing)))
            (syntax-quote-reader- [rdr backquote]
              (p "SYNTAX-QUOTE-READER")
              (letfn [(syntax-quote [form]
                        (letfn [(gs-symbol-name [sym]
                                  (symbol
                                    (format "%s__%i__auto__"
                                            (.substring (name sym)
                                                        0
                                                        (- (.length sym) 1))
                                            (next-id)))),
                                (sreturn [sym] (list 'quote sym))
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
              (condp eq (char ch)
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
              (p "CHARACTER-READER")
              (let [ch (dot-read rdr)]
                (eof-guard ch (Exception. "EOF while reading character"))
                (let [token (read-token rdr ch)]
                  (if (= 1 (.length token))
                    (Character/valueOf (.charAt token 0))
                    (condp eq token
                      "newline" \newline
                      "space" \space
                      "tab" \tab
                      "backspace" \backspace
                      "formfeed" \formfeed
                      "return" \return
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
                      (let [ch (dot-read rdr)]
                        (if (or (= -1 (int ch))
                                (whitespace? ch)
                                (macro? ch))
                          (do (dot-unread rdr ch)
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
              ;(p "var-reader")
              (list 'var (read rdr true nil true))),
            (unmatched-delimited-reader [rdr rightdelim]
              (throw
                (Exception.
                  (format "Unmatched delimiter: %c" (char rightdelim))))),
            (whitespace? [ch] (or (Character/isWhitespace ch) (= \, (char ch)))),
            (dispatch-reader [rdr hash]
              (p "DISPATCH-READER")
              (let [ch (dot-read rdr)]
              (p (format "read ch %c previous ch %c" (char ch) (char hash)))
                (if (= (int ch) -1)
                  (throw (Exception. "EOF while reading character"))
                  (let [fn (get-dispatch-macro ch)]
                    (if (nil? fn)
                      (throw (Exception. (format "No dispatch macro for: %c" (char ch))))
                      (fn rdr ch)))))),
            (unquote-reader [rdr comma]
              (let [ch (dot-read rdr)]
                (if (= (int ch) -1)
                  (throw (Exception. "EOF while reading character"))
                  (if (= (char ch) \@)
                    (let [o (read rdr true nil true)]
                      (list 'clojure.core/unquote-splicing o))
                    (do (dot-unread rdr ch)
                      (list 'clojure.core/unquote (read rdr true nil true))))))),
            (wrapping-reader [sym]
              (fn [rdr quo]
                (let [o (read rdr true nil true)]
                  (list sym o))))
            (discard-reader [rdr underscore]
              (read rdr true nil true)
              rdr)
            (coment-reader [rdr semicolon]
              (p "COMENT-READER")
              (loop [ch (dot-read rdr)]
                (if (and (not (= -1 (int ch)))
                         (not (= (char ch) \newline))
                         (not (= (char ch) \return)))
                  (recur (dot-read rdr))
                  rdr)))
            (string-reader [rdr doublequote]
              (p "STRING-READER")
              (let [sb (string-builder)]
                (letfn [(read-escaped-character-into [sb rdr]
                          (let [ch (dot-read rdr)]
                            (if (or (= \\ (char ch))
                                    (= \" (char ch)))
                              (append sb (char ch))
                              (append sb
                                (condp eq (char ch)
                                  \t \tab \r \return \n \newline
                                  \b \backspace \f \formfeed
                                  \u
                                    (if (= -1 (Character/digit (char ch) 16))
                                      (throw (Exception. (format "Invalid unicode escape: \\u%c" (char ch))))
                                      (read-unicode-char<pushback> rdr ch 16 4 true))
                                  (if (Character/isDigit (char ch))
                                    (let [ch (read-unicode-char<pushback> rdr ch 8 3 false)]
                                      (if (> ch 0377)
                                        (throw (Exception. "Octal escape sequence must be in range [0, 377]"))
                                        ch))
                                    (throw (Exception. (format "Unsupported escape character: %c%c" \\ (char ch))))))))))]
                  (loop [ch (dot-read rdr)]
                    (eof-guard ch (Exception. "EOF while reading string"))
                    (if (= \" (char ch))
                      (to-string sb)
                      (if (= \\ (char ch))
                        (do (read-escaped-character-into sb rdr)
                          (recur (dot-read rdr)))
                        (do (append sb (char ch))
                          (recur (dot-read rdr))))))))),
            (garg [n]
              (symbol (str (if (= -1 n) "rest" (str "p" n)) "__" (next-id)))),
            (terminating-macro? [ch]
              (and (not (= (char ch) \#))
                   (not (nil? (get-macro ch)))))
            (interpret-token [token]
              (p (format "INTERPRET-TOKEN %s" token))
              (condp eq token
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
              (p "READ-TOKEN")
              (let [sb (string-builder)]
                (append sb (char ch))
                (loop [ch (dot-read rdr)]
                  (if (or (= (int ch) -1)
                          (whitespace? ch)
                          (terminating-macro? ch))
                    (do (dot-unread rdr ch)
                      (to-string sb))
                    (do (append sb (char ch))
                      (recur (dot-read rdr))))))),
            (read-number [rdr initch]
              (p "READ-NUMBER")
              (let [sb (string-builder)]
                (append sb (char initch))
                (loop [ch (dot-read rdr)]
                  (if (or (= (int ch) -1)
                          (whitespace? ch)
                          (macro? ch))
                    (do (dot-unread rdr ch)
                      (let [s (to-string sb)
                            n (match-number s)]
                        (if (nil? n)
                          (throw (NumberFormatException. (format "Invalid number: %s" s)))
                          n)))
                    (do (append sb (char ch))
                      (recur (dot-read rdr)))))))
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
              (p "READ")
              (try
                (loop [ch (dot-read rdr)]
                  (if (whitespace? ch)
                    (recur (dot-read rdr))
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
                                  (recur (dot-read rdr))
                                  ret)))
                            (if (or (= (char ch) \+)
                                    (= (char ch) \-))
                              (let [ch2 (dot-read rdr)]
                                (if (Character/isDigit ch2)
                                  (do (dot-unread rdr ch2)
                                    (let [n (read-number rdr ch)]
                                      (if (suppressed-read?)
                                        nil
                                        n)))
                                  (do (dot-unread rdr ch2)
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
      (.println *out* (str "reader.name=" (.getName (class read)))))))
