(ns cljmd.core
  (:require [clojure.contrib.jmx :as jmx])
  (:use clojure.string))

(def block-opts
 { 
    :blockquote #"^[\ ]{0,1}\>\ "
    :ol #"^[\ ]{0,1}[0-9]*\." 
  })

;(def style-opts "foo")

(defn list-to-text
  [text]
  (apply str (interpose "\r\n" text)))

(defn get-block
  [line opts]
  (let [block-types (for [opt opts] (if (not-empty (re-find (val opt) line)) (key opt)))]
      (first (keep identity block-types))))

(defn build-block
  [line prev nex tag opts]
  (print line ": " prev)
  (let [reg (opts tag)]
    (if (and (not (nil? prev)) (not-empty (re-find reg prev)))
      ))
  ;(if (not (and (nil? prev) (re-find (opts tag) prev))))
  (str "built block! " line " for " tag))

(defn check-sections
  [lines & prev]
  (let [block-type (get-block (first lines) block-opts)
        prev-line (first prev)
        next-line (first (rest lines)) 
        curr-line (first lines)
        line (if-not (nil? block-type))
               (build-block curr-line prev-line next-line block-type block-opts)
               (first lines)]
      (cons line (if (not-empty (rest lines)) (check-sections (rest lines) curr-line)))))

(defn sectionalize
  [lines]
  (let [sections (check-sections lines)]
    (if (not= sections lines)
      (recur sections)
      sections)))

(defn build-markup
  [text]
  (let [lines (split-lines text)]
    (println "running markup builder")
    (println (list-to-text (sectionalize lines)))
    (sectionalize lines)
    (println "ending build")))

(defn -main
  []
  (do
    (println "ran main...\r\n")))


