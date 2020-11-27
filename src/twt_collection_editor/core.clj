(ns twt-collection-editor.core
  (:gen-class)
  (:require [cheshire.core :refer :all]
            [twt-collection-editor.api :as api]))

(def coll-ids ["custom-1278372356419813377"
               "custom-1279095780536537088"
               "custom-1279471953917521920"])

(defn get-body [response]
  "returns the body of response as a map"
  (parse-string (:body response) true))

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

(defn post-after [coll-id tw-id relative-to]
  (api/collections-entries-add :id coll-id :tweet_id tw-id :relative_to relative-to :above false))

;; TODO- add migrate to, write test

(defn migrate-to [from-coll to-coll]
  )

(defn -main [])
