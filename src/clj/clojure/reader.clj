(ns clojure.reader)

(defn- readI [rdr eof-is-error? eof-value recursive?]
  (try
    (loop [ch (.read rdr)]
      (if (or (Character/isWhitespace ch) (= \, (char ch)))
        (recur (.read rdr))
        (if (= (int ch) -1)
          (if eof-is-error?
            (throw (Exception. "EOF while reading"))
            eof-value)
          (if (Character/isDigit ch)
            (let [n (clojure.lang.LispReader/readNumber rdr (char ch))]
              (if (clojure.lang.RT/suppressRead)
                nil
                n))
            (let [macro-fn (clojure.lang.LispReader/getMacro ch)]
              (if (not (nil? macro-fn))
                (let [ret (macro-fn rdr (char ch))]
                  (if (== rdr ret)
                    (recur (.read rdr))
                    ret))
                (if (or (= \+ (char ch)) (= \- (char ch)))
                  (let [ch2 (.read rdr)]
                    (if (Character/isDigit ch2)
                      (do (.unread rdr ch2)
                        (let [n (clojure.lang.LispReader/readNumber rdr (char ch))]
                          (if (clojure.lang.RT/suppressRead)
                            nil
                            n)))
                      (do (.unread rdr ch2)
                        (let [token (clojure.lang.LispReader/readToken rdr (char ch))]
                          (if (clojure.lang.RT/suppressRead)
                            nil
                            (clojure.lang.LispReader/interpretToken token)))))))))))))
    (catch Exception e
      (when (or recursive?
                (not (instance? clojure.lang.LineNumberingPushbackReader rdr)))
        (throw e))
      (clojure.lang.LispReader$ReaderException. (.getLineNumber rdr) e))))

(.println System/err (.getName (class readI)))
