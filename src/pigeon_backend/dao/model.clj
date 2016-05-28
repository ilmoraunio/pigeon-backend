(ns pigeon-backend.dao.model
  (require [schema.core :as s]))

(s/defschema Input
  {(s/optional-key :id) (s/maybe s/Int)
   (s/optional-key :created) (s/maybe java.util.Date)
   (s/optional-key :updated) (s/maybe java.util.Date)
   (s/optional-key :version) (s/maybe s/Int)
   (s/optional-key :deleted) (s/maybe Boolean)})

(s/defschema Model
  {:id s/Int
   :created java.util.Date
   :updated java.util.Date
   :version s/Int
   :deleted Boolean})