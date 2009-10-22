(defn refer [x])
(ns clojure.reader)

(clojure.core/defn- readI [rdr eof-is-error? eof-value recursive?]
  (clojure.core/letfn [(whitespace? [ch] (clojure.core/or (Character/isWhitespace ch) (= \, (clojure.core/char ch))))
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
          (read-number [rdr initch]
                       (clojure.core/let [sb (StringBuilder.)]
                         (.append sb (clojure.core/char initch))
                         (clojure.core/loop [ch (.read rdr)]
                           (if (clojure.core/or (= (clojure.core/int ch) -1)
                                   (whitespace? ch)
                                   (macro? ch))
                             (do (.unread rdr ch)
                               (clojure.core/let [s (.toString sb)
                                     n (clojure.lang.LispReader/matchNumber s)]
                                 (if (nil? n)
                                   (throw (NumberFormatException. (.concat "Invalid number: " s)))
                                   n)))
                             (do (.append sb (clojure.core/char ch))
                               (recur (.read rdr)))))))
          (read-token [rdr initch]
                      (clojure.core/let [sb (StringBuilder.)]
                        (.append sb (clojure.core/char initch))
                        (clojure.core/loop [ch (.read rdr)]
                          (if (clojure.core/or (= (clojure.core/int ch) -1)
                                  (whitespace? ch)
                                  (terminating-macro? ch))
                            (do (.unread rdr ch)
                              (.toString sb))
                            (do (.append sb (clojure.core/char ch))
                              (recur (.read rdr)))))))
          (readunicodechar-pushback-rdr [rdr initch base length exact]
                                        (clojure.core/let [uc (Character/digit initch base)]
                                          (clojure.core/when (= -1 uc)
                                            (throw (IllegalArgumentException. (.concat "Invalid digit: " (.toString (Character. initch))))))
                                          (clojure.core/loop [i 1 uc uc]
                                            (if (>  length i)
                                              (clojure.core/let [ch (.read rdr)]
                                                (if (clojure.core/or (= ch -1)
                                                        (whitespace? ch)
                                                        (macro? ch))
                                                  (.unread rdr ch)
                                                  (clojure.core/let [d (Character/digit ch base)]
                                                    (clojure.core/when (= d -1)
                                                      (throw (IllegalArgumentException. (.concat "Invalid digit: " (.toString (Character. ch))))))
                                                    (recur (inc i) (* uc (+ base d))))))
                                              (if (clojure.core/and exact
                                                       (not (= i length)))
                                                (throw
                                                  (IllegalArgumentException.
                                                    (.concat (.concat (.concat "Invalid character length: " i) ", shoulde be: ") length)))
                                                uc)))))
          (string-reader [reader doublequote]
            (clojure.core/let [sb (StringBuilder.)]
              (clojure.core/loop [char (.read reader)]
                (if (= char \")
                  (.toString sb)
                  (if (= char -1)
                    (throw (Exception. "EOF while reading string"))
                    (if (= char \\) 
                      (clojure.core/let [char (.read reader)]
                        (if (clojure.core/or (= char \\) (= char \"))
                          (recur char)
                          (do
                            (.append sb
                                     (clojure.core/condp = char
                                       \t "\t"
                                       \r "\r"
                                       \n "\n"
                                       \b "\b"
                                       \f "\f"
                                       \u
                                       (clojure.core/let [char (.read reader)]
                                         (if (= -1 (Character/digit char 16))
                                           (throw (Exception. (.concat "Invalid unicode escape: \\u" char)))
                                           (readunicodechar-pushback-rdr reader char 16 4 true)))
                                       :else
                                        (if (Character/isDigit char)
                                          (clojure.core/let [ch (readunicodechar-pushback-rdr reader char 8 3 false)]
                                            (if (> ch 0377)
                                              (throw (Exception. "Octal escape sequence must be in range [0, 377]"))
                                              ch))
                                          (throw (Exception. (.concat "Unsupported escape character: " char))))))
                            (recur (.read reader)))))
                     (recur (do (.append sb char) (.read reader)))))))))
          (list-reader [rdr leftparen]
                       (clojure.core/let [line (if (instance? clojure.lang.LineNumberingPushbackReader rdr)
                                    (.getLineNumber rdr)
                                    -1)
                             list (read-delimited-list \) rdr true)]
                         (if (.isEmpty list)
                           ()
                           (clojure.core/let [s (clojure.lang.PersistentList/create list)]
                             (if (not (= -1 line))
                               (.withMeta s {:line line})
                               s)))))
          (get-macro [ch]
                  (clojure.core/condp = (clojure.core/char ch)
                    \" string-reader
                    \; (clojure.lang.LispReader$CommentReader.)
                    \' (clojure.lang.LispReader$WrappingReader 'clojure.core/quote)
                    \@ (clojure.lang.LispReader$WrappingReader 'clojure.core/deref)
                    \^ (clojure.lang.LispReader$WrappingReader 'clojure.core/meta)
                    \` (clojure.lang.LispReader$SyntaxQuoteReader.)
                    \( list-reader
                    (clojure.core/let [macs (wall-hack clojure.lang.LispReader :macros nil)
                          c (count macs)]
                      (if (clojure.core/and (> c (clojure.core/int ch)) (> (clojure.core/int ch) -1))
                        (clojure.core/aget macs (clojure.core/int ch))
                        nil))))
          (macro? [ch] (not (nil? (get-macro ch))))
          (terminating-macro? [ch] (clojure.lang.LispReader/isTerminatingMacro ch))
          (read-delimited-list [delim rdr recur?]
                               (clojure.lang.LispReader/readDelimitedList delim rdr recur?))
          (wall-hack [class-name field-name obj]
            (clojure.core/-> class-name (.getDeclaredField (name field-name))
              (clojure.core/doto (.setAccessible true))
                    (.get obj)))
          (read [rdr eof-is-error? eof-value recursive?]
                (try
                  (clojure.core/loop [ch (.read rdr)]
                    (if (whitespace? ch)
                      (recur (.read rdr))
                      (if (= (clojure.core/int ch) -1)
                        (if eof-is-error?
                          (throw (Exception. "EOF while reading"))
                          eof-value)
                        (if (Character/isDigit ch)
                          (clojure.core/let [n (clojure.lang.LispReader/readNumber rdr (clojure.core/char ch))]
                            (if (clojure.lang.RT/suppressRead)
                              nil
                              n))
                          (clojure.core/let [macro-fn (get-macro ch)]
                            (if (not (nil? macro-fn))
                              (clojure.core/let [ret (macro-fn rdr (clojure.core/char ch))]
                                (if (= rdr ret)
                                  (recur (.read rdr))
                                  ret))
                              (if (clojure.core/or (= \+ (clojure.core/char ch)) (= \- (clojure.core/char ch)))
                                (clojure.core/let [ch2 (.read rdr)]
                                  (if (Character/isDigit ch2)
                                    (do (.unread rdr ch2)
                                      (clojure.core/let [n (read-number rdr (clojure.core/char ch))]
                                        (if (clojure.lang.RT/suppressRead)
                                          nil
                                          n)))
                                    (do (.unread rdr ch2)
                                      (clojure.core/let [token (read-token rdr (clojure.core/char ch))]
                                        (if (clojure.lang.RT/suppressRead)
                                          nil
                                          (clojure.lang.LispReader/interpretToken token)))))))))))))
                  (catch Exception e
                    (clojure.core/when (clojure.core/or recursive?
                              (not (instance? clojure.lang.LineNumberingPushbackReader rdr)))
                      (throw e))
                    (clojure.lang.LispReader$ReaderException. (.getLineNumber rdr) e))))]
  (.println System/err "Clojure Reader Called.")
  (read rdr eof-is-error? eof-value recursive?)))

(.println System/err (.concat "Reader class: " (.getName (clojure.core/class readI))))
