(ns duct-basic-server.model
  (:require [integrant.core :as ig]))

(defmethod ig/init-key ::email-model [_ _]
  (re-pattern "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$"))