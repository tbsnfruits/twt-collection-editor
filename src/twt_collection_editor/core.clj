(ns twt-collection-editor.core
  (:gen-class)
  (:require [clojure.data.json :as json]
            [clj-http.client :as client]
            [oauth.client :as oauth]))

(def app-consumer-key (System/getenv "TWITTER_CONSUMER_KEY"))
(def app-consumer-secret (System/getenv "TWITTER_CONSUMER_SECRET"))
(def user-access-token (System/getenv "USER_ACCESS_TOKEN"))
(def user-access-token-secret (System/getenv "USER_ACCESS_TOKEN_SECRET"))

(def coll-ids ["custom-1287073494606389248"
               "custom-1278372356419813377"
               "custom-1279095780536537088"
               "custom-1279471953917521920"])

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

(defn collections-entries
  "Returns GET collections/entries"
  ;e.g.) (collections-entries :id "custom-1287073494606389248" :count 10 :max_position "418302462296924481")
  [& {:as user-params}]
  (let [url "https://api.twitter.com/1.1/collections/entries.json"]
    (client/get url
                {:query-params   (merge (credentials :get url user-params) user-params)
                 :cookie-policy  :standard
                 :decode-cookies false}))
  )

(defn get-body [response]
  "returns the body of response as a map"
  (json/read-str (:body response) :key-fn keyword))

(defn count-response
  "Counts the tweets in GET collection/entries response body from argument"
  [response-body]
  (count (get-in response-body [:response :timeline])))

(defn min-position
  "Returns min_position of the response body"
  [response-body]
  (get-in response-body [:response :position :min_position]))

(defn truncated?
  "Returns true if the response is truncated"
  [response-body]
  (get-in response-body [:response :position :was_truncated]))

(defn last-tweet
  "Returns the id of the last tweet in the argument collection"
  [coll-id]
  (loop [response-body (get-body (collections-entries :id coll-id :count 200))]
    (let [min-pos (min-position response-body)]
      (if (truncated? response-body)
        (recur (get-body (collections-entries :id coll-id :count 200 min-pos)))
        (-> (get-in response-body [:response :timeline])
            (last)
            (get-in [:tweet :id]))))))

(defn count-collection
  "Counts the total number of tweets in the collection from argument"
  [coll-id]
  (loop [response-body (get-body (collections-entries :id coll-id :count 200))
         acc-num 0]
    (println acc-num)                                       ;line for probing
    (let [min-pos (min-position response-body)]
      (if (truncated? response-body)
        (recur (collections-entries :id coll-id :count 200 :min_position min-pos) (+ acc-num (count-response response-body)))
        (+ acc-num (count-response response-body))))))

;;TODO - renew codes below according to current collections-entries function.

(defn post-after [coll-id tw-id relative-to]
  (collections-entries-add :oauth-creds creds :params {:id coll-id :tweet_id tw-id :relative_to relative-to :above false}))

(comment
  (collections-entries* (coll-ids 0))

  (collections-entries-add :oauth-creds creds
                           :params {:id       (coll-ids 0)
                                    :tweet_id "652174303283277825"})

  (collections-entries-remove :oauth-creds creds
                              :params {:id       (coll-ids 0)
                                       :tweet_id "652174303283277825"})

  (collections-entries-curate :oauth-creds creds
                              :headers {:content-type "application/json"}
                              :body {:id      (coll-ids 0)
                                     :changes [{:op "add" :tweet_id "1325174161212321793"}]}))

(defn -main [])


(comment

 (defn curate [collection-id tweets-to-add]
   (clojure.java.shell/sh
     "curl" "--compressed" "-q"
     "https://api.twitter.com/1.1/collections/entries/curate.json"
     "-X" "POST"
     "-H" "Content-Type: application/json"
     "-H" "Accept-Encoding: gzip;q=1.0,deflate;q=0.6,identity;q=0.3"
     "-H" "Accept: */*"
     "-H" "User-Agent: twurl version: 0.9.6 platform: ruby 2.7.0 (x86_64-linux-gnu)"
     "-H" "Authorization: OAuth oauth_body_hash=\"vjnQaOf%2BiUOg1SgUEBTrjF6eFCw%3D\", oauth_consumer_key=\"Ux0vpJwZ72OdgrqtDOrKYU0xs\", oauth_nonce=\"TYu6xe6rW2Ek0jSXZxfeDCQ4LNeFBCL3uFCIwjw\", oauth_signature=\"e77i4%2BbKq2c5hMWf51cBv8J7Mys%3D\", oauth_signature_method=\"HMAC-SHA1\", oauth_timestamp=\"1604826460\", oauth_token=\"348192116-UKpbWbYDgn5uB8L3OtSKxMF3pmzblYvh2R0k8aPj\", oauth_version=\"1.0\""
     "-H" "Connection: close"
     "-H" "Content-Length: 201"
     "-d"
     (json/write-str
       {:id      collection-id,
        :changes (mapv #(merge {:op "add"} %)
                       tweets-to-add)})))

 (curate "custom-1279095780536537088"
         [{:id          "390897780949925889",
           :relative_to "1240723216412205062",
           :above       false}]))


