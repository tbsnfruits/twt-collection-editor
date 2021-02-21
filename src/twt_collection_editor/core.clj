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

(defn get-timeline
  "returns the timeline of response body as a list"
  [response-body]
  (->> response-body
       :response
       :timeline
       (map #(get-in % [:tweet :id]))))

;last-tweet, count-tweets는 list-tweets를 이용가능하므로 생략

(defn list-tweets
  "Return the total list of tweet ids in the collection from argument"
  [coll-id]
  (loop [response-body (get-body (api/collections-entries :id coll-id :count 200))
         acc '()]
    (let [min-pos (min-position response-body)
          timeline (get-timeline response-body)]
      (println "min-pos:" min-pos " truncated?:" (truncated? response-body)) ;for probing
      (println (last timeline))
      (if (truncated? response-body)
        (recur (get-body (api/collections-entries :id coll-id :count 200 :max_position min-pos)) (concat acc timeline))
        (concat acc timeline)))))

(defn remove-twts [coll-id tw-ids]
  (doseq [tw-ids (partition-all 100 tw-ids)]
    (let [changes (reduce (fn [acc tw-id]
                            (conj acc {:op       "remove"
                                       :tweet_id tw-id}))
                          []
                          tw-ids)]
      (println (count changes))
      (println (get-body (api/collections-entries-curate :id coll-id :changes changes))))))

(defn remove-twts-all [coll-id]
  (let [tw-ids (->> (get-body (api/collections-entries :id coll-id))
                    :response
                    :timeline
                    (map #(get-in % [:tweet :id])))]
    (remove-twts coll-ids tw-ids)))                         ;에러발생///웨

(defn migrate-after [from to]
  "from 콜렉션의 트윗들을 to 콜렉션의 하단에 추가한다. from에는 한개 이상의 트윗이 있어야함"
  (let [tw-ids (list-tweets from)
        head (last (list-tweets to))
        order (partition 2 1 (cons head tw-ids))]
    (doseq [step order
            :let [tw-id (second step)
                  relative-to (first step)]]
      (println tw-id)
      (println (get-body (api/collections-entries-add :id to :tweet_id tw-id :relative_to relative-to :above false))))))

(defn -main [])
