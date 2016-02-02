(ns pigeon-backend.handler-test
  (:require [clojure.test :refer [deftest]]
            [midje.sweet :refer :all]
            [pigeon-backend.handler :refer [coerce-to-integer ]]))

(deftest handler-test
  (facts "Port env helper fn"
    (fact "Numeric input"
      (coerce-to-integer 3000) => 3000)
    (fact "String input"
      (coerce-to-integer "3000") => 3000)))