(ns cljmd.test
  (:require [cljmd.core :as core]) 
  (:require [cljmd.old :as old]))

(def test-text
"foo
#bar
###bazw#ibble\r\n# wobble
#######flub")

(def test-section "> foo\r\n> bar\r\n> baz\r\ntest2\r\n 1. #lol!\r\n2. k")
(def test-section-1 "> foo\r\n> bar\r\n> baz\r\nwibble\r\n1. hola!\r\n 2.Hello!\r\n 3. rgr")

(defn -main
  []
  (do
    ;(println (old/build-markup test-section))
    (println (core/build-markup test-section))
    ))
