(ns pigeon-backend.dao.model
  (require [schema.core :as s]))

;; TODO: map kws of Model with s/optional-key and map values with s/maybe
(def QueryInput
  {(s/optional-key :id) (s/maybe String)
   (s/optional-key :created) (s/maybe java.util.Date)
   (s/optional-key :updated) (s/maybe java.util.Date)
   (s/optional-key :version) (s/maybe s/Int)
   (s/optional-key :deleted) (s/maybe Boolean)})

(def id-pattern? #".{8}-.{4}-.{4}-.{4}-.{12}")
(def id (s/pred #(re-matches id-pattern? %)))
(def Model (assoc {:created java.util.Date
                   :updated java.util.Date
                   :version s/Int
                   :deleted Boolean}
                  :id id))
(def Existing id)