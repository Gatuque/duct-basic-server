(ns duct-basic-server.handler.core
  (:require
    [integrant.core :as ig]
    [taoensso.timbre :as log]
    [duct-basic-server.boundary.db :as storage]))

(defmethod ig/init-key ::create-user [_ {:keys [db]}]
  (fn [{{:keys [id first_name last_name email admin is_active, pass]
         :or {last_name "" admin false is_active false}
         :as body-params} :body-params}]
    (let [user-info {:first_name first_name :last_name last_name :email email :pass pass}]
      ;  (storage/create-user (storage/->CreateUser user-info))
      {:body
       {:status 0
        :message (format "%s %s profile created successfully" first_name last_name)}})))
