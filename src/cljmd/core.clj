(ns cljmd.core
  (:require [clojure.contrib.jmx :as jmx])
  (:use clojure.string))

; (split-lines test-text)
;(def text-to-test (into () (split-lines test-text)))

(def sectional-opts 
  { :blockquote #"^[\ ]*>\ " })

(defn build-tag
  [tag content]
  (if (not-empty content) 
    (str "<" tag ">" content "</" tag ">")))

(defn remove-md
  [text pattern]
  (clojure.string/replace text pattern ""))

(defn build-section
  [lines tag]
  (let [tag-str tag
        tag-key (jmx/maybe-keywordize tag-str)]
    (if (re-matcher (sectional-opts tag-key) (first lines))
        (if (not-empty (rest lines)) (conj (build-section (rest lines) tag-str) (remove-md (first lines) (sectional-opts tag-key)))))))

(defn section 
  [lines]
  ;(map (fn [line] (re-matches #"^>" line)) lines)
  (do
    (println "Start Line:")
    (println (first lines))
    (println (re-matches #"^>" (first lines)))
    ;(println (first lines))
    (println "End Line\r\n")
    (if (not-empty (rest lines)) (recur (rest lines)))))

(defn build-markup
  [text]
  (let [lines (split-lines text)
        sections (build-section lines "blockquote")]
    (do
      (println (build-tag "blockquote" (apply str (interpose "\r\n" (build-section lines "blockquote")))))
      lines  
      )
    ))

(defn line-to-markup
  [line]
  (let [amount (count (re-find #"^[\#]{0,6}" line))
        text (clojure.string/replace line #"^[\#\ ]*" "")]
    (if (> amount 0)
      (build-tag (str "h" amount) text)
      text)))

(defn markdown-from-text
  [& text]
  (do
    (map (fn [x] (line-to-markup x))
      text) 
    (apply str (interpose "\r\n" text))))

(defn -main
  []
  (do
    (println "ran main...\r\n")))


