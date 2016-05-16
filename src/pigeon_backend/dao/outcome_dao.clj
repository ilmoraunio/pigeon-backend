(ns pigeon-backend.dao.outcome-dao
  (:require [schema.core :as s]
            [yesql.core :refer [defquery]]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.dao.psql-util :refer [execute-sql-or-handle-exception]]))

(s/defschema NewOutcome {:property_id s/Int
                         :type String
                         :value String})

(defquery sql-outcome-create<! "sql/outcome/create.sql"
  {:connection db-spec})

(defn create! [tx outcome] {:pre [(s/validate NewOutcome outcome)]}
  (execute-sql-or-handle-exception
    (fn [tx map-args]
      (sql-outcome-create<! map-args {:connection tx})) tx outcome))