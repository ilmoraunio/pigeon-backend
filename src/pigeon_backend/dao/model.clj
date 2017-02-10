(ns pigeon-backend.dao.model
  (require [schema.core :as s]))

;; TODO: map kws of Model with s/optional-key and map values with s/maybe
(def QueryInput
  {(s/optional-key :id) (s/maybe String)
   (s/optional-key :created) (s/maybe java.util.Date)
   (s/optional-key :updated) (s/maybe java.util.Date)
   (s/optional-key :version) (s/maybe s/Int)
   (s/optional-key :deleted) (s/maybe Boolean)})

(def Model
  {:id String
   :created java.util.Date
   :updated java.util.Date
   :version s/Int
   :deleted Boolean})

(def Existing
  {:id String})