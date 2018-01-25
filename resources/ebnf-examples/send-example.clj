(let [params *params*]
  (send (:message params) (:intended-recipients params)))
