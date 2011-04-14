(defproject clojure-apocrypha-lein "1.3.0-apocryphalein-SNAPSHOT"
  :description ""
  :dependencies []
  :dev-dependencies [[robert/hooke "1.1.0"]]
  :java-source-path "src/jvm/"
  :source-path "src/clj/"
  :clean-non-project-classes false
  :aot [clojure.core.protocols clojure.main clojure.set clojure.xml
        clojure.zip clojure.inspector clojure.walk clojure.stacktrace
        clojure.template clojure.test clojure.test.tap clojure.test.junit
        clojure.pprint clojure.java.io clojure.repl clojure.java.browse
        clojure.java.javadoc clojure.java.shell clojure.java.browse-ui
        clojure.string clojure.data clojure.reflect])

(use '[leiningen.core :only [prepend-tasks]]
     '[leiningen.javac :only [javac]]
     '[robert.hooke :only [add-hook]]
     '[leiningen.classpath :only [get-classpath make-path]]
     '[clojure.java.io :only [file]])

(require '[leiningen.compile :as c])

(import '(org.apache.tools.ant.taskdefs Java))

(add-hook #'c/compile
          (fn [_ project & args]
            (.mkdir (file (:compile-path project)))
            (when (:java-source-path project)
              (javac project))
            (let [java (Java.)]
              (#'c/add-system-property
               java :clojure.compile.path (:compile-path project))
              (.setClasspath java (make-path
                                   (:source-path project)
                                   (:compile-path project)))
              (.setFailonerror java true)
              (.setFork java true)
              (.setDir java (file (:root project)))
              (.setClassname java "clojure.lang.Compile")
              (doseq [namespace (:aot project)]
                (.setValue (.createArg java) (name namespace)))
              (spit (str (:compile-path project)
                         "/clojure/version.properties")
                    (str "version=" (:version project)))
              (.executeJava java))))
