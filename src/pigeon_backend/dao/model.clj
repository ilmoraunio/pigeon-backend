(ns pigeon-backend.dao.model
  (require [schema.core :as s]))

;; TODO: map kws of Model with s/optional-key and map values with s/maybe
(s/defschema QueryInput
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

(s/defschema Existing
  {:id s/Int})