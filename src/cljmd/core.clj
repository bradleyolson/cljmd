(ns cljmd.core
  (:require [clojure.contrib.jmx :as jmx])
  (:use clojure.string))

(def block-opts
 { 
    :blockquote #"^[\ ]{0,1}\>\ "
    :ol #"^[\ ]{0,1}[0-9]*\."
  })
(def anchor-keys {})

(defn list-to-block
  "converts a list of strings to a text block separated by \r\n"
  [text]
  (apply str (interpose "\r\n" text)))

(defn build-tag
  "builds an html tag given [content tagtype]"
  [content tag]
  (let [tag-name (if-not (nil? tag) tag "p")]
    (if-not (empty content)
      (str "<" tag-name ">\r\n" content "\r\n</" tag-name ">"))))

(defn remove-md
  "Takes a section and removes the associated markup give [section, tag-type, options-list, and replacer*]"
  [section tag opts]
  (list-to-block 
    (map (fn [line] 
         (clojure.string/replace line (tag opts) "")) 
       (split-lines section)))) 

(defn sectionalize
  "builds base paragraphs from [text]. returns list."
  [^String text]
  (seq (.split #"\r?\n\r?\n" text)))

; --- unnecessary at the moment

(defn get-block
  [line opts]
  (let [block-types (for [opt opts] (if (not-empty (re-find (val opt) line)) (key opt)))]
      (first (keep identity block-types))))

(defn build-block
  [line prev nex tag opts]
  (let [reg (opts tag)]
    (if-not (and (nil? prev) (re-find reg prev))
      (println "foo")
      (println "bar")) 
  ;(if (not (and (nil? prev) (re-find (opts tag) prev))))
  (str "built block! " line " for " tag)))

(defn check-sections
  [lines & prev]
  (let [block-type (get-block (first lines) block-opts)
        prev-line (first prev)
        next-line (first (rest lines)) 
        curr-line (first lines)
        line (if-not (nil? block-type)
               (build-block curr-line prev-line next-line block-type block-opts)
               (first lines))]
      (cons line (if (not-empty (rest lines)) (check-sections (rest lines) curr-line)))))

(defn build-blocks
  [lines]
  (let [sections (check-sections lines)]
    (if (not= sections lines)
      (recur sections)
      sections)))

; ---

(defn section-markup
  [section]
   (map (fn [sect]
          (let [opts block-opts
                block-type (get-block sect opts)]
            (if-not (nil? block-type)
              (build-tag (remove-md sect block-type opts) (name block-type)) 
              (build-tag sect "p"))))
        section))

(defn build-markup
  [text]
  (let [sections (section-markup (sectionalize text))]
    (println "running markup builder")
    (println (list-to-block sections))
    (println "ending build")
    (list-to-block sections)))

(defn -main
  []
  (do
    (println "ran main...\r\n")))
