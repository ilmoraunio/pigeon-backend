(ns pigeon-backend.dao.property-dao
  (:require [schema.core :as s]
            [yesql.core :refer [defquery]]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.dao.psql-util :refer [execute-sql-or-handle-exception]]))

(s/defschema NewProperty {:directedconnection_id s/Int
                          :name String
                          :type String
                          :value String})

(defquery sql-property-create<! "sql/property/create.sql"
  {:connection db-spec})

(defn create! [tx property] {:pre [(s/validate NewProperty property)]}
  (execute-sql-or-handle-exception
    (fn [tx map-args]
      (sql-property-create<! map-args {:connection tx})) tx property))