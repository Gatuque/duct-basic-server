(ns duct-basic-server.handler.core
  (:require
    [integrant.core :as ig]
    [buddy.hashers :as hashers]
    [clojure.string :as str]
    [duct-basic-server.boundary.db :as storage]))


(defmethod ig/init-key ::create-user [_ _]
  (fn [{{:keys [pass]
           :as body-params} :body-params}]
    (let [hash-pass (hashers/derive pass)
          user-info (-> body-params
                        (dissoc :pass)
                        (assoc  :password hash-pass)
                        storage/map->CreateNewUser)
          {:keys [id errors]} (storage/create-user user-info)]
       (if errors
         {:status 500
          :body {:error (format "Error creating user %s" errors)}}
         {:status 201
          :body {:id id}}))))

(defmethod ig/init-key ::get-users [_ _]
  (fn [_]
    (let [{:keys [users errors]} (storage/get-users
                                   (storage/map->GetUsers nil))]
      (if errors
        {:status 500
         :body {:error (format "Error getting users %s" errors)}}
        {:status 200
         :body {:users users}}))))


(defmethod ig/init-key ::get-user [_ _]
  (fn [{{:keys [id]} :path-params}]
    (let [{:keys [user errors]} (storage/get-user-by-id
                                  (storage/map->GetUser {:id (Integer/parseInt id)}))]
      (cond
        (and errors
             (str/includes? errors "not found")) {:status 404
                                                  :body {:error (format "User with id %s not found" id)}}
        errors {:status 500
                :body {:message (format "Error getting user %s" errors)}}
        :else {:status 200 :body {:users user}}))))

(defmethod ig/init-key ::update-user [_ _]
  (fn [{{:keys [id]} :path-params
         user-info :body-params}]
    (let [user-info-record (-> user-info
                               (assoc :id (Integer/parseInt id))
                               storage/map->UpdateUser)
          {:keys [update-count errors]} (storage/update-user user-info-record)]
      (cond
        (zero? update-count) {:status 404
                              :body {:error (format "User with id %s not found" id)}}
        (= update-count 1) {:status 200
                            :body {:message (str "User[%s] info updated successfully" id)}}
        errors {:status 500
                :body {:message (format "Error updating user %s" errors)}}
        :else {:status 500
               :body {:message (format "Error updating user %s" errors)}}))))

(defmethod ig/init-key ::delete-user [_ _]
  (fn [{{:keys [id]} :path-params}]
    (let [{:keys [update-count errors]} (storage/delete-user
                                  (storage/map->DeleteUser {:id (Integer/parseInt id)}))]
      (cond
        (zero? update-count) {:status 404
                              :body {:error (format "User with id %s not found" id)}}
        (= update-count 1) {:status 200
                            :body {:message (format "User[%s] account deleted successfully" id)}}
        errors {:status 500
                :body {:error (format "Error deleting user %s" errors)}}
        :else {:status 500
               :body {:error (format "Error deleting user %s" errors)}}))))
