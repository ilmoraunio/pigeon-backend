(defproject pigeon-backend "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-time "0.9.0"] ; required due to bug in lein-ring
                 [metosin/compojure-api "1.1.10" :exclusions [clj-time metosin/ring-http-response cheshire joda-time ring/ring-core com.fasterxml.jackson.dataformat/jackson-dataformat-smile ring/ring-codec commons-codec commons-io]]
                 [metosin/ring-http-response "0.6.5"]
                 [ring-server "0.4.0"]
                 [environ "1.0.1"]
                 [ragtime "0.7.1"]
                 [org.clojure/tools.nrepl "0.2.10"]
                 [org.postgresql/postgresql "9.4.1207.jre7"]
                 [webjure/jeesql "0.4.6"]
                 [ring-cors "0.1.7"]
                 [buddy/buddy-hashers "0.14.0" :exclusions [commons-codec]]
                 [buddy/buddy-auth "0.12.0" :exclusions [clj-time cheshire joda-time com.fasterxml.jackson.dataformat/jackson-dataformat-smile commons-codec]]
                 [metosin/schema-tools "0.9.0"]
                 [prismatic/schema-generators "0.1.0"]
                 [cheshire "5.3.1"]
                 [com.taoensso/sente "1.11.0"]
                 [org.immutant/web "2.1.0"]
                 [com.cognitect/transit-clj "0.8.300"]
                 [com.taoensso/sente "1.12.0"]
                 [metosin/ring-http-response "0.9.0"]]
  :plugins       [[lein-environ "0.4.0"]]
  :uberjar-name "server.jar"
  :eval-in :nrepl
  :profiles {:dev [:project/dev :profiles/dev]
             :project/dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                                          [ring-mock "0.1.5"]
                                          [midje "1.6.3"]
                                          [enlive "1.1.6"]
                                          [org.clojure/data.json "0.2.6"]]
                           :plugins [[lein-ring "0.9.6"]
                                     [lein-midje "3.1.3"]
                                     [lein-cloverage "1.0.6"]]
                           ;;when :nrepl-port is set the application starts the nREPL server on load
                           :env {:dev true
                                 :message-character-limit "150"
                                 :nrepl-port 7000}}
             :project/test {}
             :uberjar {:omit-source true
                       :aot :all}}
   :aliases {"migrate" ["run" "-m" "pigeon-backend.db.migrations/migrate"]
             "rollback" ["run" "-m" "pigeon-backend.db.migrations/rollback"]
             "migrate-data" ["run" "-m" "pigeon-backend.db.migrations/migrate-data"]
             "rollback-data" ["run" "-m" "pigeon-backend.db.migrations/rollback-data"]
             "migrate-data-extra" ["run" "-m" "pigeon-backend.db.migrations/migrate-data-extra"]
             "rollback-data-extra" ["run" "-m" "pigeon-backend.db.migrations/rollback-data-extra"]}
   :main pigeon-backend.handler
   :min-lein-version "2.5.3"
   :resource-paths ["src" "resources"])
