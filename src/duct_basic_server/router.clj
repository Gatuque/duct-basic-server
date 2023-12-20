(ns duct-basic-server.router
  (:require [reitit.ring :as ring]
            [reitit.coercion.malli]
            [reitit.ring.malli]
            [reitit.dev.pretty :as pretty]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.parameters :as parameters]
            [duct-basic-server.middleware.web-defaults :as middleware]
            [duct-basic-server.middleware.db-middleware :as db-middleware]
    ;       [reitit.ring.middleware.dev :as dev]
    ;       [reitit.ring.spec :as spec]
    ;       [spec-tools.spell :as spell]
            [muuntaja.core :as m]
            [duct-basic-server.handler.handler :as handler]
            [malli.util :as mu]
            [taoensso.timbre :as log]
            [integrant.core :as ig]
            [clojure.walk :as walk]))

(defn middleware [env]
  (let [base [;; swagger feature
              swagger/swagger-feature
              ;; query-params & form-params
              parameters/parameters-middleware
              ;; content-negotiation
              muuntaja/format-negotiate-middleware
              ;; encoding response body
              muuntaja/format-response-middleware
              ;; decoding request body
              muuntaja/format-request-middleware
              ;; coercing response bodys
              coercion/coerce-response-middleware
              ;; coercing request parameters
              coercion/coerce-request-middleware
              ]]
    (if (= env :production)
      (conj base exception/exception-middleware)
      base)))

(defn default-route-opts [env]
  {:coercion (reitit.coercion.malli/create
               {;; set of keys to include in error messages
                :error-keys #{#_:type :coercion :in :schema :value :errors :humanized #_:transformed}
                ;; schema identity function (default: close all map schemas)
                :compile mu/closed-schema
                ;; strip-extra-keys (effects only predefined transformers)
                :strip-extra-keys true
                ;; add/set default values
                :default-values true
                ;; malli options
                :options nil})
   :muuntaja m/instance
   :exception pretty/exception
   :middleware (middleware env)})

(def default-default-handlers
  {:not-found          (ig/ref :duct.handler.static/not-found)
   :not-acceptable     (ig/ref :duct.handler.static/bad-request)
   :method-not-allowed (ig/ref :duct.handler.static/method-not-allowed)})

(defn- resolve-symbol [x]
  (if-let [var (and (symbol? x) (resolve x))]
    (var-get var)
    x))


(def swagger-json-routes
  ["/swagger.json"
   {:get {:no-doc true
          :swagger {:info {:title "duct_server_api"
                           :description "A simple crud server API using clojure duct framework"}
          :handler (swagger/create-swagger-handler)}}}])

(defmethod ig/prep-key ::reitit [_ {:keys [routes env]
                                    ::ring/keys [opts default-handlers]}]
  {:routes (walk/postwalk resolve-symbol routes)
   ::ring/opts  (merge {:data (default-route-opts env)} opts)
   ::ring/default-handlers (merge default-default-handlers default-handlers)})

(defmethod ig/init-key ::reitit [_ {:keys [routes env]
                                    ::ring/keys [opts default-handlers]}]
  (ring/ring-handler
    (ring/router (conj routes swagger-json-routes) opts)
    (ring/routes
      (swagger-ui/create-swagger-ui-handler
        {:path "/"})
      (ring/create-default-handler default-handlers))))