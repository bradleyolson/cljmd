(ns cljmd.core
  (:require [clojure.contrib.jmx :as jmx])
  (:require clojure.string))

(def block-opts
 { 
    :blockquote #"^[\ ]{0,1}\>\ "
    :ol #"^[\ ]{0,1}[0-9]*\." 
  })
(def anchor-keys {})

(defn list-to-text
  [text]
  (apply str (interpose "\r\n" text)))

(defn build-tag
  [content tag]
  (let [tag-name (if-not (nil? tag) tag "p")]
    (if-not (empty content)
      (str "<" tag-name ">" content "</" tag-name ">"))))

(defn remove-md
  [section tag opts & replacer]
  (let [replace-with (if-not (empty (first replacer)) (first replacer) "")
        lines (split-lines section)
        content (map (fn [line] 
                   (clojure.string/replace line (tag opts) replace-with)) 
                 lines)]
    (println (map (fn [line] 
                   (clojure.string/replace-re (tag opts) replace-with line))
                 lines))
      content))

(defn sectionalize
  [^String text]
  (seq (.split #"\r?\n\r?\n" text)))

; ---

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
              (do
                (build-tag (remove-md sect block-type opts) block-type) 
                )
                (build-tag sect "p"))))
        section))

(defn build-markup
  [text]
  (let [sections (section-markup (sectionalize text))]
    (println "running markup builder")
    (println (list-to-text sections))
    (println "ending build")))

(defn -main
  []
  (do
    (println "ran main...\r\n")))
