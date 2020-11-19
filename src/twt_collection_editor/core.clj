(ns twt-collection-editor.core
  (:gen-class)
  (:require [clojure.data.json :as json]
            [twt-collection-editor.api :as api]))

(def coll-ids ["custom-1287073494606389248"
               "custom-1278372356419813377"
               "custom-1279095780536537088"
               "custom-1279471953917521920"])


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
  (loop [response-body (get-body (api/collections-entries :id coll-id :count 200))]
    (let [min-pos (min-position response-body)]
      (if (truncated? response-body)
        (recur (get-body (api/collections-entries :id coll-id :count 200 min-pos)))
        (-> (get-in response-body [:response :timeline])
            (last)
            (get-in [:tweet :id]))))))

(defn count-collection
  "Counts the total number of tweets in the collection from argument"
  [coll-id]
  (loop [response-body (get-body (api/collections-entries :id coll-id :count 200))
         acc-num 0]
    (println acc-num)                                       ;line for probing
    (let [min-pos (min-position response-body)]
      (if (truncated? response-body)
        (recur (api/collections-entries :id coll-id :count 200 :min_position min-pos) (+ acc-num (count-response response-body)))
        (+ acc-num (count-response response-body))))))

;;TODO - renew codes below according to current api/collections-entries function.
(defn -main [])

(comment

(defn post-after [coll-id tw-id relative-to]
   (api/collections-entries-add :oauth-creds creds :params {:id coll-id :tweet_id tw-id :relative_to relative-to :above false}))

 (defn curate [collection-id tweets-to-add]
   (clojure.java.shell/sh
     "curl" "--compressed" "-q"
     "https://api.twitter.com/1.1/collections/entries/curate.json"
     "-X" "POST"
     "-H" "Content-Type: application/json"
     "-H" "Accept-Encoding: gzip;q=1.0,deflate;q=0.6,identity;q=0.3"
     "-H" "Accept: */*"
     "-H" "User-Agent: twurl version: 0.9.6 platform: ruby 2.7.0 (x86_64-linux-gnu)"
     "-H" "Authorization: OAuth oauth_consumer_key=\"Ux0vpJwZ72OdgrqtDOrKYU0xs\", oauth_nonce=\"TYu6xe6rW2Ek0jSXZxfeDCQ4LNeFBCL3uFCIwjw\", oauth_signature=\"e77i4%2BbKq2c5hMWf51cBv8J7Mys%3D\", oauth_signature_method=\"HMAC-SHA1\", oauth_timestamp=\"1604826460\", oauth_token=\"348192116-UKpbWbYDgn5uB8L3OtSKxMF3pmzblYvh2R0k8aPj\", oauth_version=\"1.0\""
     "-H" "Connection: close"
     "-H" "Content-Length: 201"
     "-d"
     (json/write-str
       {:id      collection-id,
        :changes (mapv #(merge {:op "add"} %)
                       tweets-to-add)})))

 (curate "custom-1279095780536537088"
         [{:tweet_id    "390897780949925889",
           :relative_to "1240723216412205062",
           :above       false}
          {:tweet_id "390853164611555329"}]))


