(ns twt-collection-editor.auth
  (:require [oauth.client :as oauth]))

(def app-consumer-key (System/getenv "TWITTER_CONSUMER_KEY"))
(def app-consumer-secret (System/getenv "TWITTER_CONSUMER_SECRET"))
(def user-access-token (System/getenv "USER_ACCESS_TOKEN"))
(def user-access-token-secret (System/getenv "USER_ACCESS_TOKEN_SECRET"))

(def consumer (oauth/make-consumer app-consumer-key
                                   app-consumer-secret
                                   "https://api.twitter.com/oauth/request_token"
                                   "https://api.twitter.com/oauth/access_token"
                                   "https://api.twitter.com/oauth/authorize"
                                   :hmac-sha1))

(defn credentials
  "returns oauth parameter as a map when http method, URL, user parameters are provided"
  [http-method url user-params]
  (oauth/credentials consumer
                     user-access-token
                     user-access-token-secret
                     http-method
                     url
                     user-params))
