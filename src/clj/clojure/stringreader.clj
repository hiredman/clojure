(in-ns 'clojure.core)

(defn- wall-hack-method [class-name name- params obj & args]
  (-> class-name (.getDeclaredMethod (name name-) (into-array Class params))
    (doto (.setAccessible true))
    (.invoke obj (into-array Object args))))

(defn- readunicodechar [& foo]
  (apply wall-hack-method clojure.lang.LispReader
         :readUnicodeChar
         [java.io.PushbackReader int int int boolean] nil
         foo))

(defn STRINGREADER [#^java.io.Reader reader doublequote]
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

(defn COMMENTREADER [reader semicolon]
  (loop [ch (char (.read reader))]
    (if (and (not= -1 (int ch))
             (not= \newline ch)
             (not= \return ch))
      (recur (char (.read reader)))
      reader)))

(defn QUOTEWRAPPINGREADER [reader quo]
  (clojure.lang.RT/list quo (read reader true nil true)))
