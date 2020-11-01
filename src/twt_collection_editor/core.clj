(ns twt-collection-editor.core
  (:gen-class)
  (:use [twitter.oauth]
        [twitter.callbacks]
        [twitter.callbacks.handlers]
        [twitter.api.restful]
        [twitter.api.streaming])
  (:require [clojure.data.json :as json]
            [http.async.client :as ac])
  (:import [twitter.callbacks.protocols AsyncStreamingCallback]))

(def app-consumer-key          (System/getenv "TWITTER_CONSUMER_KEY"))
(def app-consumer-secret       (System/getenv "TWITTER_CONSUMER_SECRET"))
(def user-access-token         (System/getenv "USER_ACCESS_TOKEN"))
(def user-access-token-secret  (System/getenv "USER_ACCESS_TOKEN_SECRET"))

(def creds (make-oauth-creds app-consumer-key
                             app-consumer-secret
                             user-access-token
                             user-access-token-secret))

(def coll-ids {1 "custom-1278372356419813377"
               2 "custom-1279095780536537088"
               3 "custom-1279471953917521920"})

(defn collections-entries*
  "Returns GET collections/entries"
  ([coll-id num max-pos min-pos]
   (collections-entries :oauth-creds creds :params {:id coll-id :count num :max_position max-pos :min_position min-pos}))
  ([coll-id num max-pos]
   (collections-entries :oauth-creds creds :params {:id coll-id :count num :max_position max-pos}))
  ([coll-id num]
   (collections-entries :oauth-creds creds :params {:id coll-id :count num}))
  ([coll-id]
   (collections-entries :oauth-creds creds :params {:id coll-id})))

(defn count-response
  "Counts the tweets in GET collection/entries response from argument"
  [response]
  (count (get-in response [:body :response :timeline])))

(defn min-position
  "Returns min_position of the response"
  [response]
  (get-in response [:body :response :position :min_position]))

(defn truncated?
  "Returns true if the response is truncated"
  [response]
  (get-in response [:body :response :position :was_truncated]))

(defn last-tweet
  "Returns the id of the last tweet in the argument collection"
  [coll-id]
  (loop [response (collections-entries* coll-id 200)]
      (let [min-pos (min-position response)]
        (if (truncated? response)
          (recur (collections-entries* coll-id 200 min-pos))
          (-> (get-in response [:body :response :timeline])
              (last)
              (get-in [:tweet :id]))))))

(defn count-collection
  "Counts the total number of tweets in the collection from argument"
  [coll-id]
  (loop [response (collections-entries* coll-id 200)
         acc-num 0]
    (println acc-num)  ;line for probing
    (let [min-pos (min-position response)]
      (if (truncated? response)
        (recur (collections-entries* coll-id 200 min-pos) (+ acc-num (count-response response)))
        (+ acc-num (count-response response))))))

(defn post-after [coll-id tw-id relative-to]
  (collections-entries-add :oauth-creds creds :params {:id coll-id :tweet_id tw-id :relative_to relative-to :above false}))


(defn -main [])
