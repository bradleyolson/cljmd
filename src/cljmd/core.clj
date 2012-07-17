(ns cljmd.core
  (:require [clojure.contrib.string :as string]))

; declarations

(def sectionals
  (hash-map 
    :blockquote #"^\s?[\>].*(\r*\s?[\>].*)*"
    :ol #"^\s*[0-9]*\.+.*([\r\n]\s*[0-9]*\.+.*)*"
    :ul #"^\s*[\*\+\-].*([\r\n]\s*[\*\+\-].*)*"
    :p #"^.*(\S)"
  ))

(def removals
  (hash-map
    
  ))

(defn new-link
  [reference uri & alt]
  (hash-map 
    :ref reference
    :uri uri
    :alt (first alt)
  ))

; sectionalize

(defn re-move
  [pattern text]
  (clojure.string/replace-first text pattern ""))

(defn test-regex
  [text pair]
  (if (re-find (val pair) text)
    {(key pair) text}))

(defn section-test
  [block section-opts]
  (into {} (filter #(not (nil? %))
             (map (fn [opt]
               (test-regex block opt)
             ) section-opts))))

(defn merge-sequential-keys
  [collection & pairs]
  (cond
    (empty? pairs) collection
    (and (not-empty collection) 
         (= (key (last collection)) (key (first pairs)))) 'foo
    :else (recur (conj collection (first pairs)) (rest pairs))
    ))

(comment 
(defn find-blocks
  [text & regex]
  (if (empty? regex) text
    (if (re-find (val (first regex)) text)
      (test-regex text (first regex))
      (recur text (rest regex))
    )))
)

(defn find-blocks
  [text & regex]
  (cond
    (empty? regex) nil
    (re-find (val (first regex)) text) (test-regex text (first regex))
    :else (recur text (rest regex))))

(defn filter-regex
  [text]  
  (filter (fn [x] (not (nil? x))) 
          (map (fn [x]
            (first (apply find-blocks x sectionals))
          ) (clojure.string/split-lines text))))

(defn sectionalize
  [^String text]
  (apply merge-sequential-keys '() (filter-regex text)))

; markup specific

(defn attr
  [pair]
  (str (name (key pair)) "=\"" (val pair) "\""))

(defn attrs
  ([opts] (apply attrs "" opts))
  ([total & opts]
    (if (empty? opts) total
      (recur (str total " " (apply attr (first opts))) (rest opts)))))

(defn tag
  [content tag & opts]
  (str "<" tag (if opts (attrs opts)) ">" content "</" tag ">"))

(defn link-legend
  [^String lines]
  (let [link-regex #"(^\s*\[.*\])\:?\s+(\S*)(?:\s*(\".*\"))?"]
    (map (fn [legend-key]
           (apply new-link (rest (re-find link-regex legend-key))))
      (filter (fn [line] (re-find link-regex line)) lines))))

(defn collapse-to-markup
  [total & sections]
  (if (empty? sections) total
    (if (string? (val (first sections)))
      (recur (str total (tag (val (first sections)) (name (key (first sections)))))
             (rest sections)))))
 
(defn build-markup
  [text]
  (let [legend (link-legend (string/split-lines text))
        sections (sectionalize text)]
    (println legend)
    (println sections)
    ;(println (apply collapse-to-markup "" sections))
  ))

(def test-text
"> > foo

1. bar
2. baz
3. 5

> ba
> baz

  teststststsst

#bar
###bazw#ibble\r\n# wobble

#######flub

  foo

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

;(println (apply collapse-to-markup "" test-map))
(println (build-markup test-text))


; Sectionalize - run through each \r\n\r\n and either make it a p tag or make it another type section.
;
