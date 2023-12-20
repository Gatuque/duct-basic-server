(ns duct-basic-server.boundary.db
  (:require
    [next.jdbc.date-time]
    [next.jdbc.prepare]
    [next.jdbc.result-set]
    [integrant.core :as ig]
    [conman.core :as conman]
    [duct.database.sql]))

(def db-conn (atom nil))

(defmethod ig/init-key ::bind-db-connection [_ {:keys [db]}]
  (reset! db-conn (-> db :spec :datasource))
  db)

(defmethod ig/halt-key! ::bind-db-connection [_ db-instance]
  (conman/disconnect! db-instance))

(conman/bind-connection @db-conn "sql/users.sql")