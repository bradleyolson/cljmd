(ns cljmd.test
  (:require [cljmd.core :as core]))

(def test-text
"foo
#bar
###bazw#ibble\r\n# wobble
#######flub")

(def test-section "> foo\r\n> bar\r\n> baz\r\nwibble")

(defn -main
  []
  (do
    (println (core/section test-section))
    ;(core/section test-section)
    ))
