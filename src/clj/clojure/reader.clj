(ns clojure.reader)

(defn- readI [rdr eof-is-error? eof-value recursive?]
  (letfn [(whitespace? [ch] (or (Character/isWhitespace ch) (= \, (char ch))))
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
          (macro? [ch] (clojure.lang.LispReader/isMacro ch))
          (get-macro [ch] (clojure.lang.LispReader/getMacro ch))
          (terminating-macro? [ch] (clojure.lang.LispReader/isTerminatingMacro ch))
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
  (read rdr eof-is-error? eof-value recursive?)))

(.println System/err (.getName (class readI)))
