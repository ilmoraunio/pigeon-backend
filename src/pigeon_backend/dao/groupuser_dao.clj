(ns pigeon-backend.dao.groupuser-dao
  (:require [schema.core :as s]
            [yesql.core :refer [defquery]]
            [pigeon-backend.db.config :refer [db-spec]]
            [pigeon-backend.dao.psql-util :refer [execute-sql-or-handle-exception]]))

(s/defschema Newgroupuser {:roomgroup_id s/Int
                           :users_id s/Int})

(defquery sql-groupuser-create<! "sql/groupuser/create.sql"
  {:connection db-spec})

(defn create! [tx groupuser] {:pre [(s/validate Newgroupuser groupuser)]}
  (execute-sql-or-handle-exception
    (fn [tx map-args]
      (sql-groupuser-create<! map-args {:connection tx})) tx groupuser))