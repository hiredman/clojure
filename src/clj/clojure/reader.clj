(ns clojure.reader)
;;; TODO
;;; Move ReaderException somewhere so LispReader can be safely deleted
;;; ?
(defn- readI [rdr eof-is-error? eof-value recursive?]
  (letfn [(whitespace? [ch] (or (Character/isWhitespace ch) (= \, (char ch))))
          ;Boots;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
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
          ;/Boots;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
          ;Wrappers;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
          (read-token [rdr initch]
            (clojure.lang.LispReader/readToken rdr initch))
          (match-number [s]
            (clojure.lang.LispReader/matchNumber s))
          (get-macro [ch]
            (condp = (char ch)
              \; (clojure.lang.LispReader$CommentReader.)
              \' (clojure.lang.LispReader$WrappingReader. 'clojure.core/quote)
              \@ (clojure.lang.LispReader$WrappingReader. 'clojure.core/deref)
              \^ (clojure.lang.LispReader$WrappingReader. 'clojure.core/meta)
              \` (clojure.lang.LispReader$SyntaxQuoteReader.)
              \( list-reader
              (let [macs (wall-hack clojure.lang.LispReader :macros nil)
                    c (count macs)]
                (if (and (> c (int ch)) (> (int ch) -1))
                  (aget macs (int ch))
                  nil))))
          (terminating-macro? [ch]
            (clojure.lang.LispReader/isTerminatingMacro ch))
          (read-delimited-list [delim rdr recur?]
            (clojure.lang.LispReader/readDelimitedList delim rdr recur?))
          (suppressed-read? [] (clojure.lang.RT/suppressRead))
          (read-number [rdr ch]
            (clojure.lang.LispReader/readNumber rdr (char ch)))
          (interpret-token [token]
            (clojure.lang.LispReader/interpretToken token))
          ;/Wrappers;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
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
                    s)))))
          (macro? [ch] (not (nil? (get-macro ch))))
          (wall-hack [class-name field-name obj]
            (-> class-name (.getDeclaredField (name field-name))
              (doto (.setAccessible true))
                    (.get obj)))
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
                  (throw (clojure.lang.LispReader$ReaderException. (.getLineNumber rdr) e))))))
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
