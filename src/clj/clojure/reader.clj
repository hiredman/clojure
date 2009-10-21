(in-ns 'clojure.core)

(defn- wall-hack-method [class-name name- params obj & args]
  (-> class-name (.getDeclaredMethod (name name-) (into-array Class params))
    (doto (.setAccessible true))
    (.invoke obj (into-array Object args))))

(defn- terminating-macro? [ch]
  (clojure.lang.LispReader/isTerminatingMacro (int ch)))

(defn- macro? [ch]
  (clojure.lang.LispReader/isMacro (int ch)))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- readunicodechar [& foo]
  (apply wall-hack-method clojure.lang.LispReader
         :readUnicodeChar
         [java.io.PushbackReader int int int boolean] nil
         foo))

(defn- whitespace? [char-or-int]
  (or (Character/isWhitespace (char char-or-int)) (= char-or-int \,)))

(defmulti #^{:private true} readunicodechar (comp type first list))

(defmethod readunicodechar java.io.PushbackReader [r initch base length exact]
  (let [uc (Character/digit initch base)]
    (when (= -1 uc)
      (throw (IllegalArgumentException. (str "Invalid digit: " initch))))
    (loop [i 1 uc uc]
      (if (< i length)
        (let [ch (.read r)]
          (if (or (= ch -1)
                  (whitespace? ch)
                  (macro? ch))
            (.unread r ch)
            (let [d (Character/digit ch base)]
              (when (= d -1)
                (throw (IllegalArgumentException. (str "Invalid digit: " (char ch)))))
              (recur (inc i) (* uc (+ base d))))))
        (if (and exact
                 (not= i length))
          (throw (IllegalArgumentException. (str "Invalid character length: " i ", shoulde be: " length)))
          uc)))))

(defn- STRINGREADER [#^java.io.Reader reader doublequote]
  (let [sb (StringBuilder.)]
    (loop []
      (let [ch (char (.read reader))]
        (if (= \" ch)
          (.toString sb)
          (do
            (when (= -1 (int ch)) (throw (Exception. "EOF while reading string")))
            (.append sb
                     (if (= ch \\ )
                       (let [ch (char (.read reader))]
                         (condp = ch
                           \t "\t" \r "\r" \n "\n" \\ \\ \" \"
                           \b "\b" \f "\f"
                           \u (let [ch (char (.read reader))]
                                (when (= -1 (Character/digit ch 16))
                                  (throw (Exception. (str "Invalid unicode escape: \\u" (char ch)))))
                                (readunicodechar reader ch 16 4 true))
                           :else
                              (if (Character/isDigit ch)
                                (let [ch (readunicodechar reader ch 8 3 false)]
                                  (when (> ch 0377)
                                    (throw (Exception. "Octal escape sequence must be in range [0, 377].")))
                                  ch)
                                (throw (Exception. (str "Unsupported escape character: " ch))))))
                       ch))
            (recur)))))))

(defn- COMMENTREADER [reader semicolon]
  (loop [ch (char (.read reader))]
    (if (and (not= -1 (int ch))
             (not= \newline ch)
             (not= \return ch))
      (recur (char (.read reader)))
      reader)))

(defn- wrapping-reader [sym]
  (fn [reader quo]
    (clojure.lang.RT/list sym (read reader true nil true))))

(def #^{:private true} QUOTEWRAPPINGREADER (wrapping-reader 'quote))
(def #^{:private true} DEREFWRAPPINGREADER (wrapping-reader 'deref))
(def #^{:private true} METAWRAPPINGREADER (wrapping-reader 'meta))

;;(defn- READTOKEN [reader initch]
;;  (let [sb (StringBuilder.)]
;;    (.append sb initch)
;;    (loop [ch (int (.read reader))]
;;      (if (or (= -1 ch)
;;              (whitespace? ch)
;;              (terminating-macro? ch))
;;        (do (.unread reader ch)
;;            (.toString sb))
;;        (do (.append sb (char ch))
;;            (recur (int (.read reader))))))))

(defn- DISCARDREADER [reader underscore]
  (READER reader true nil true)
  reader)

(.println System/err "Reader Online")

