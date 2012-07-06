(ns cljmd.core
  (:require [clojure.contrib.string :as string]))
; to text > markup

(def sectionals
  { 
    :blockquote #"^[\ ]{0,1}\>\ "
    :ol #"^[\ ]{0,1}[0-9]*\." 
    :p #"^\r\n\r\n"
  })

(defn new-link
  [reference uri & alt]
  {
    :ref reference
    :uri uri
    :alt alt
  })

(defn links
  [lines]
  (let [link-regex #"(^\s*\[.*\])\:?\s+(\S*)(?:\s*(\".*\"))?"]
    (map (fn [legend-key]
           (apply new-link (rest (re-find link-regex legend-key))))
      (filter (fn [line] (re-find link-regex line)) lines))))

(defn tag
  [content tag]
  (str "<" tag ">" content "</" tag ">"))

(defn sectionalize
  [^String text]
  {
   :p (seq (.split #"\r?\n\r?\n" text))
  })

(defn build
  [text]
  (let [legend (links (string/split-lines text))
        sections (sectionalize text)]
    (println legend)
    (println "baz")
    (println "bar")
    ;(reduce (fn [result section] (str result (tag (val section) (key section))))
    ;        "" sections)
  ;  (reduce (fn [x] (str "FOO" x)) sections))
  ))

; TESTING

(def test-text
"> > foo

> bar
> baz

1. foo
2. bar
3. baz
4. 5

#bar
###bazw#ibble\r\n# wobble

#######flub

[msn]:    http://search.msn.com/
[google]: http://google.com/ \"The Google\"
[yahoo]:  http://yahoo.com
")

(def test-section "> foo\r\n\r\n> bar\r\n> baz\r\ntest2\r\n 1. #lol!\r\n2. k")
(def test-section-1 "> foo\r\n> bar\r\n> baz\r\nwibble\r\n1. hola!\r\n 2.Hello!\r\n 3. rgr")

(println (build test-text))
