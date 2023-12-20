(ns duct-basic-server.handler.example
  (:require [compojure.core :refer :all]
            [integrant.core :as ig]))

(defmethod ig/init-key :duct-basic-server.handler/example [_ options]
  (context "/example" []
    (GET "/" []
      {:body {:example "data"}})))
