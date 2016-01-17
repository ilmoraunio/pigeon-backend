(ns pigeon-backend.coverage
  (:require [clojure.test :refer :all]
            [net.cgrand.enlive-html :as html])
  (:gen-class))

; To use: lein run -m pigeon-backend.coverage <wanted-form-coverage-percentage> <wanted-line-coverage-percentage>

(defn -main [& args]
  (let [wanted-form-coverage-percentage (Double. (first args))
        wanted-line-coverage-percentage (Double. (last args))
        td-elements (html/select 
                      (html/html-snippet (slurp "target/coverage/index.html"))
                      [:html :body :table :tr :td.with-number])
        form-coverage-sum (Double. (clojure.string/replace (apply str (:content ((comp second reverse) td-elements))) #" %" ""))
        line-coverage-sum (Double. (clojure.string/replace (apply str (:content (last td-elements))) #" %" ""))
        form-covered? (>= form-coverage-sum wanted-form-coverage-percentage)
        lines-covered? (>= line-coverage-sum wanted-line-coverage-percentage)]
    (assert (true? form-covered?) "Not enough form coverage")
    (assert (true? lines-covered?) "Not enough line coverage")
    (println "Form coverage at " form-coverage-sum "% out of target percentage " wanted-form-coverage-percentage "%")
    (println "Line coverage at " line-coverage-sum "% out of target percentage " wanted-line-coverage-percentage "%")))