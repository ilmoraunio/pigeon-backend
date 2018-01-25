(let [params *params*
      message (:message params)
      intended-recipients (:intended-recipients params)
      opposing-recipients (:opposing-recipients params)
      dice-throw (randomize-value 6)]
  (if (= dice-throw 1)
    (send message opposing-recipients)
    (let [another-dice-throw (randomize-value 6)]
      (if (= another-dice-throw 1)
        (do (send message opposing-recipients)
            (send message intended-recipients))
        (send message intended-recipients)))))
