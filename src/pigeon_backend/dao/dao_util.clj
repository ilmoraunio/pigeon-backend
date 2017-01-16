(ns pigeon-backend.dao.dao-util)

(defn initialize-query-data [model]
  (-> (into {} (map (fn [x] (assoc x 1 nil)) model))
      (merge {:limit nil :offset nil})
      (merge {:deleted false})
      (merge {:username nil})))
