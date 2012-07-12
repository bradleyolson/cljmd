(ns cljmd.core
  (:require [clojure.contrib.string :as string]))

(def sectionals
  (hash-map 
    :blockquote #"^\s?[\>].*"
    :ol #"^\s*[0-9]*\.+.*" 
    :ul #"^\s*[\*\+\-].*"
    :p #"^[\r\n\r\n|\t] "
  ))

(defn new-link
  [reference uri & alt]
  (hash-map 
    :ref reference
    :uri uri
    :alt (first alt)
  ))

(defn tag
  [content tag & opts]
  (str "<" tag ">" content "</" tag ">"))

(defn link-legend
  [^String lines]
  (let [link-regex #"(^\s*\[.*\])\:?\s+(\S*)(?:\s*(\".*\"))?"]
    (map (fn [legend-key]
           (apply new-link (rest (re-find link-regex legend-key))))
      (filter (fn [line] (re-find link-regex line)) lines))))

(defn re-move
  [pattern text]
  (clojure.string/replace-first text pattern ""))

(defn test-regex
  [text pair]
  (if (re-find (val pair) text)
    {(key pair) (re-move (val pair) text)}))

(defn section-test
  [block section-opts]
  (into {} (filter #(not (nil? %))
      (doall (map (fn [opt]
                    (test-regex block opt)
              ) section-opts)))))

(defn sectionalize
  [^String text]
  (into {} (map (fn [line]
        (section-test line sectionals)
      ) (seq (string/split #"\r?\n\r?\n" text)))))

(defn collapse
  [total & sections]
  (if (empty? sections) total
    (let [focus (first sections)]
      (if (string? (val focus))
        (recur (str total (tag (val focus) (name (key focus)))) (rest sections)))
      ))
; (reduce (fn [complete [tag-type text]]
;           (if (string? text)
;             (str complete (tag text tag-type))
;             complete
;             )
;           )
;   sections)
  
  )
 
(defn build-markup
  [text]
  (let [legend (link-legend (string/split-lines text))
        sections (sectionalize text)
        ]
    (println legend)
    (println sections)
    (println (apply collapse "" sections))
  ))

(def test-text
"> > foo

2. bar
3. baz
4. 5

> ba
> baz

#bar
###bazw#ibble\r\n# wobble

#######flub

[msn]:    http://search.msn.com/
[google]: http://google.com/ \"The Google\"
[yahoo]:  http://yahoo.com
")

(def test-map ({:blockquote "test
is a 
test"}
{:p "this is a test"}
{:p "this is a [test link][foobar]"}))

(def test-section "> foo\r\n\r\n> bar\r\n> baz\r\ntest2\r\n 1. #lol!\r\n2. k")
(def test-section-1 "> foo\r\n> bar\r\n> baz\r\nwibble\r\n1. hola!\r\n 2.Hello!\r\n 3. rgr")

(println (build-markup test-text))
;(println (apply collapse "" test-map))


; Sectionalize - run through each \r\n\r\n and either make it a p tag or make it another type section.
;
