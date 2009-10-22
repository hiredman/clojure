(ns clojure.reader)

(defn- readI [rdr eof-is-error? eof-value recursive?]
  (letfn [(whitespace? [ch] (or (Character/isWhitespace ch) (= \, (char ch))))
          (= [x b] (.equals x b))
          (name [x] (.getName x))
          (count [x] (clojure.lang.RT/count x))
          (> [a b] (clojure.lang.Numbers/gt a b))
          (read-number [rdr initch]
                       (let [sb (StringBuilder.)]
                         (.append sb (char initch))
                         (loop [ch (.read rdr)]
                           (if (or (= (int ch) -1)
                                   (whitespace? ch)
                                   (macro? ch))
                             (do (.unread rdr ch)
                               (let [s (.toString sb)
                                     n (clojure.lang.LispReader/matchNumber s)]
                                 (if (nil? n)
                                   (throw (NumberFormatException. (str "Invalid number: " s)))
                                   n)))
                             (do (.append sb (char ch))
                               (recur (.read rdr)))))))
          (read-token [rdr initch]
                      (let [sb (StringBuilder.)]
                        (.append sb (char initch))
                        (loop [ch (.read rdr)]
                          (if (or (= (int ch) -1)
                                  (whitespace? ch)
                                  (terminating-macro? ch))
                            (do (.unread rdr ch)
                              (.toString sb))
                            (do (.append sb (char ch))
                              (recur (.read rdr)))))))
          (readunicodechar-pushback-rdr [rdr initch base length exact]
                                        (let [uc (Character/digit initch base)]
                                          (when (= -1 uc)
                                            (throw (IllegalArgumentException. (str "Invalid digit: " initch))))
                                          (loop [i 1 uc uc]
                                            (if (< i length)
                                              (let [ch (.read rdr)]
                                                (if (or (= ch -1)
                                                        (whitespace? ch)
                                                        (macro? ch))
                                                  (.unread rdr ch)
                                                  (let [d (Character/digit ch base)]
                                                    (when (= d -1)
                                                      (throw (IllegalArgumentException. (str "Invalid digit: " (char ch)))))
                                                    (recur (inc i) (* uc (+ base d))))))
                                              (if (and exact
                                                       (not= i length))
                                                (throw (IllegalArgumentException. (str "Invalid character length: " i ", shoulde be: " length)))
                                                uc)))))
          (string-reader [reader doublequote]
            (let [sb (StringBuilder.)]
              (loop [char (.read reader)]
                (if (= char \")
                  (.toString sb)
                  (if (= char -1)
                    (throw (Exception. "EOF while reading string"))
                    (if (= char \\) 
                      (let [char (.read reader)]
                        (if (or (= char \\) (= char \"))
                          (recur char)
                          (do
                            (.append sb
                                     (condp = char
                                       \t "\t"
                                       \r "\r"
                                       \n "\n"
                                       \b "\b"
                                       \f "\f"
                                       \u
                                       (let [char (.read reader)]
                                         (if (= -1 (Character/digit char 16))
                                           (throw (Exception. (str "Invalid unicode escape: \\u" char)))
                                           (readunicodechar-pushback-rdr reader char 16 4 true)))
                                       :else
                                        (if (Character/isDigit char)
                                          (let [ch (readunicodechar-pushback-rdr reader char 8 3 false)]
                                            (if (> ch 0377)
                                              (throw (Exception. "Octal escape sequence must be in range [0, 377]"))
                                              ch))
                                          (throw (Exception. (str "Unsupported escape character: " \\ char))))))
                            (recur (.read reader)))))
                     (recur (do (.append sb char) (.read reader)))))))))
          (list-reader [rdr leftparen]
                       (let [line (if (instance? clojure.lang.LineNumberingPushbackReader rdr)
                                    (.getLineNumber rdr)
                                    -1)
                             list (read-delimited-list \) rdr true)]
                         (if (.isEmpty list)
                           ()
                           (let [s (clojure.lang.PersistentList/create list)]
                             (if (not= -1 line)
                               (.withMeta s {:line line})
                               s)))))
          (get-macro [ch]
                  (condp = (char ch)
                    \" string-reader
                    \; (clojure.lang.LispReader$CommentReader.)
                    \' (clojure.lang.LispReader$WrappingReader 'clojure.core/quote)
                    \@ (clojure.lang.LispReader$WrappingReader 'clojure.core/deref)
                    \^ (clojure.lang.LispReader$WrappingReader 'clojure.core/meta)
                    \` (clojure.lang.LispReader$SyntaxQuoteReader.)
                    \( list-reader
                    (let [macs (wall-hack clojure.lang.LispReader :macros nil)
                          c (count macs)]
                      (if (and (> c (int ch)) (> (int ch) -1))
                        (aget macs (int ch))
                        nil))))
          (macro? [ch] (not (nil? (get-macro ch))))
          (terminating-macro? [ch] (clojure.lang.LispReader/isTerminatingMacro ch))
          (read-delimited-list [delim rdr recur?]
                               (clojure.lang.LispReader/readDelimitedList delim rdr recur?))
          (wall-hack [class-name field-name obj]
            (-> class-name (.getDeclaredField (name field-name))
              (doto (.setAccessible true))
                    (.get obj)))
          (read [rdr eof-is-error? eof-value recursive?]
                (try
                  (loop [ch (.read rdr)]
                    (if (whitespace? ch)
                      (recur (.read rdr))
                      (if (= (int ch) -1)
                        (if eof-is-error?
                          (throw (Exception. "EOF while reading"))
                          eof-value)
                        (if (Character/isDigit ch)
                          (let [n (read-number rdr (char ch))]
                            (if (clojure.lang.RT/suppressRead)
                              nil
                              n))
                          (let [macro-fn (get-macro ch)]
                            (if (not (nil? macro-fn))
                              (let [ret (macro-fn rdr (char ch))]
                                (if (== rdr ret)
                                  (recur (.read rdr))
                                  ret))
                              (if (or (= \+ (char ch)) (= \- (char ch)))
                                (let [ch2 (.read rdr)]
                                  (if (Character/isDigit ch2)
                                    (do (.unread rdr ch2)
                                      (let [n (read-number rdr (char ch))]
                                        (if (clojure.lang.RT/suppressRead)
                                          nil
                                          n)))
                                    (do (.unread rdr ch2)
                                      (let [token (read-token rdr (char ch))]
                                        (if (clojure.lang.RT/suppressRead)
                                          nil
                                          (clojure.lang.LispReader/interpretToken token)))))))))))))
                  (catch Exception e
                    (when (or recursive?
                              (not (instance? clojure.lang.LineNumberingPushbackReader rdr)))
                      (throw e))
                    (clojure.lang.LispReader$ReaderException. (.getLineNumber rdr) e))))]
         (.println System/err "clojure reader called.")
  (read rdr eof-is-error? eof-value recursive?)))

(in-ns 'clojure.core)
(def READER @#'clojure.reader/readI)
(in-ns 'clojure.reader)

(.println System/err (str "Reader class: " (.getName (class readI))))
