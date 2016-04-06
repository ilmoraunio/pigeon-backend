(ns coveralls
  (:require [cheshire.core :as cheshire]
            [clojure.java.io :as io]
            [environ.core :refer [env]]))

; lein run -m coveralls to add relevant Coveralls.io repo token.
; Assumes repo token has been set to COVERALLS_REPO_TOKEN env variable.

(defn -main [& args]
  (let [json-as-object (cheshire/parse-string 
                         (slurp "cov/coveralls.json") 
                         true)
        repo-token     (env :coveralls-repo-token)
        json-with-repo-token (merge json-as-object {:repo_token repo-token})
        json-encoded (cheshire/encode json-with-repo-token)]
    (with-open [wrtr (io/writer "cov/coveralls.json")]
      (.write wrtr json-encoded))))