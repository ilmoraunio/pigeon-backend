(ns pigeon-backend.views.frontpage
  (:require [hiccup.page :as h]
            [pigeon-backend.views.layout :as layout]))

(defn index []
  (layout/common "frontpage" "Hello world!"))