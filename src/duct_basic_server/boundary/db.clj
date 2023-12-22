(ns duct-basic-server.boundary.db
  (:require
    [next.jdbc.date-time]
    [next.jdbc.prepare]
    [next.jdbc.result-set]
    [integrant.core :as ig]
    [conman.core :as conman]
    [taoensso.timbre :as log]
    duct.database.sql)
  (:import (java.sql SQLException)))

(def db-conn (atom nil))

(defmethod ig/init-key ::bind-db-connection [_ {:keys [db]}]
  (reset! db-conn (-> db :spec :datasource))
  db)

(defmethod ig/halt-key! ::bind-db-connection [_ db-instance]
  (conman/disconnect! db-instance))

(conman/bind-connection @db-conn "sql/users.sql")

(defmacro with-exception-logging
  "Log any exceptions thrown by the body."
  [& body]
  `(try ~@body
     (catch SQLException e#
       (log/error e# "Exception thrown in DB operation")
       {:errors ["An unexpected error occurred."]})))

(defprotocol DuctServerDB
  (create-user [this] "Create a user")
  (get-users [this] "Get all users")
  (get-user-by-id [this] "Get user by ID")
  (update-user [this] "Update a user by the ID")
  (delete-user [this] "Delete a user by the ID"))

(defrecord CreateNewUser [])
(defrecord GetUsers [])
(defrecord GetUser [])
(defrecord UpdateUser [])
(defrecord DeleteUser [])

(extend-protocol DuctServerDB
  CreateNewUser
  (create-user [user-info]
    (with-exception-logging
      (let [result (create-user! user-info)]
        (if-let [id (val (ffirst result))]
          {:id id}
          {:errors ["Failed to add user."]}))))

  GetUsers
  (get-users [_]
    (with-exception-logging
      (if-let [users (get-all-users)]
        {:users users}
        {:errors ["Failed to get users"]})))

  GetUser
  (get-user-by-id [id]
    (with-exception-logging
      (if-let [user (get-user id)]
        {:user user}
        {:errors ["User not found"]})))

  UpdateUser
  (update-user [user-data]
    (with-exception-logging
      (if-let [{update-count :next.jdbc/update-count} (update-user! user-data)]
        {:update-count update-count}
        {:errors ["User not found"]})))

  DeleteUser
  (delete-user [id]
    (with-exception-logging
      (if-let [{update-count :next.jdbc/update-count} (delete-user! id)]
        {:update-count update-count}
        {:errors ["User not found"]}))))





