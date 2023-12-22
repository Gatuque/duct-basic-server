(ns duct-basic-server.handler.test-utils
  (:require [clojure.java.io :as io]
            [duct.core :as duct]
            [integrant.core :as ig]))

(duct/load-hierarchy)

(defonce system
         (-> (duct/read-config (io/resource "duct_basic_server/config.edn"))
             (duct/prep-config [:duct.profile/test :duct.profile/local])
             (dissoc :duct.server.http/jetty)
             (ig/init)))

(defn ig-get [key]
  (get system key))
