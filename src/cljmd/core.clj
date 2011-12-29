(ns cljmd.core
  (:use clojure.string))

; (split-lines test-text)
;(def text-to-test (into () (split-lines test-text)))

(def sectional-opts "foo")

(defn section
  [text]
  (let [lines (str (split-lines text))
        sections (map (fn [line] (re-matches #"^>" line)) lines)]
    lines))

(defn build-tag
  [tag content]
  (if (not-empty content) 
    (str "<" tag ">" content "</" tag ">")))

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


