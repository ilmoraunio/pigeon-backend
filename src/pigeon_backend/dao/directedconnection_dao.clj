(ns pigeon-backend.dao.directedconnection-dao
  (:require [schema.core :as s]
            [yesql.core :refer [defquery]]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.dao.psql-util :refer [execute-sql-or-handle-exception]]))

(s/defschema NewDirectedconnection {:origin s/Int
                                    :recipient s/Int
                                    :time_id s/Int
                                    :parent (s/maybe s/Int)})

(defquery sql-directedconnection-create<! "sql/directedconnection/create.sql"
  {:connection db-spec})

(defn create! [tx directedconnection] {:pre [(s/validate NewDirectedconnection directedconnection)]}
  (execute-sql-or-handle-exception
    (fn [tx map-args]
      (sql-directedconnection-create<! map-args {:connection tx})) tx directedconnection))