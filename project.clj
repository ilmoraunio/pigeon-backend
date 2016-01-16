(defproject pigeon-backend "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clj-time "0.9.0"] ; required due to bug in lein-ring
                 [metosin/compojure-api "0.22.0"]
                 [ring-server "0.4.0"]
                 [environ "1.0.1"]]
  :ring {:handler pigeon-backend.handler/app}
  :uberjar-name "server.jar"
  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                                  [cheshire "5.3.1"]
                                  [ring-mock "0.1.5"]
                                  [midje "1.6.3"]]
                   :plugins [[lein-ring "0.9.6"]
                             [lein-midje "3.1.3"]
                             [lein-cloverage "1.0.6"]]}}
   :main pigeon-backend.handler)
