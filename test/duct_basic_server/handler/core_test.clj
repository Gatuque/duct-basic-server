(ns duct-basic-server.handler.core-test
  (:require [clojure.test :refer :all]
            [integrant.core :as ig]
            [ring.mock.request :as mock]))

;(deftest smoke-test
;  (testing "example page exists"
;    (let [handler  (ig/init-key :duct-basic-server.handler/example {})
;          response (handler (mock/request :get "/example"))]
;      (is (= 200 (:status response)) "response ok"))))
