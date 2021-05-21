(ns twt-collection-editor.auth-test
  (:require [clojure.test :refer :all]
            [twt-collection-editor.auth :refer :all]
            [clj-http.client :as http]))

;TODO- debug errors from (run-tests)
(defn get-username []
  (println "\n> Enter the screen name of current credential you're using.")
  (clojure.string/trim (read-line)))

(def user-profile
  (let [url "https://api.twitter.com/1.1/account/verify_credentials.json"]
    (http/get url
              {:query-params   (credentials :get url {})
               :cookie-policy  :standard
               :decode-cookies false})))

(deftest test-auth
  (testing "if all key & secret is provided"
    (is (every? (complement nil?) [app-consumer-key
                                 app-consumer-secret
                                 user-access-token
                                 user-access-token-secret])))

  (testing "credential validity"
    (is (= 200 (:status user-profile))))

  (testing "right account"
    (is (= (get-username) (-> user-profile
                            (:body)
                            (cheshire.core/parse-string true)
                            (:screen_name))))))
