(ns cljmd.old
  (:require [clojure.contrib.jmx :as jmx])
  (:use clojure.string))

(def sectional-opts 
  { 
    :blockquote #"^[\ ]{0,1}\>\ "
    :ol #"^[\ ]{0,1}[0-9]*\." 
  })

(defn build-tag
  [tag content]
  (if (not-empty content) 
    (str "<" tag ">" content "</" tag ">")))

(defn remove-md
  [text pattern]
  (clojure.string/replace text pattern ""))

(defn build-section
  [lines tag] 
  (if (not-empty (and (rest lines) (re-find (sectional-opts tag) (first lines))))
      (conj (build-section (rest lines) tag) (remove-md (first lines) (sectional-opts tag)))))

(defn test-blocks
  [line tag-opts]
  (for [opt tag-opts]
    (if (not-empty (re-find (val opt) line)) (key opt))))

(defn list-to-text
  [text]
  (apply str (interpose "\r\n" text)))

(defn sectionalize
  [lines]
  (let [tag (first (keep identity (test-blocks (first lines) sectional-opts)))]
    (if (keyword? tag)
      (println (build-tag (str tag) (list-to-text (build-section lines tag)))))
    (if (not-empty (rest lines)) (recur (rest lines)))))

(defn build-markup
  [text]
  (let [lines (split-lines text)]
    (println "run-1")
    (sectionalize lines)
    ;(println (build-tag "ol" (list-to-text (build-section lines "ol"))))
    (println "endrun")
    ;(build-tag "blockquote" (list-to-text (build-section lines "blockquote")))
    ))

;(defn line-to-markup
; [line]
; (let [amount (count (re-find #"^[\#]{0,6}" line))
;       text (clojure.string/replace line #"^[\#\ ]*" "")]
;   (if (> amount 0)
;     (build-tag (str "h" amount) text)
;     text)))

;(defn section 
; [lines]
; ;(map (fn [line] (re-matches #"^>" line)) lines)
; (do
;   (println "Start Line:")
;   (println (first lines))
;   (println (re-matches #"^>" (first lines)))
;   ;(println (first lines))
;   (println "End Line\r\n")
;   (if (not-empty (rest lines)) (recur (rest lines)))))

;(defn markdown-from-text
; [& text]
; (do
;   (map (fn [x] (line-to-markup x))
;     text) 
;   (apply str (interpose "\r\n" text))))

(defn -main
  []
  (do
    (println "ran main...\r\n")))


