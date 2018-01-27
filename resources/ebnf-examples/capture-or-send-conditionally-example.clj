(let [params *params*
      dice-throw (randomize-value 6)
      message (:message *params*)
      intended-recipients (:intended-recipients params)
      opposing-recipients (:opposing-recipients params)]
  (if (= dice-throw 1)
    (send message opposing-recipients)
    (send message intended-recipients)))
