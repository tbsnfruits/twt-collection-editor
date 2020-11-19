(ns twt-collection-editor.api
  (:require [clojure.data.json :as json]
            [clj-http.client :as client]
            [twt-collection-editor.auth :as auth]))

(defn collections-entries
  "makes GET collections/entries request"
  ;e.g.) (collections-entries :id "custom-1287073494606389248" :count 10 :max_position "418302462296924481")
  [& {:as user-params}]
  (let [url "https://api.twitter.com/1.1/collections/entries.json"]
    (client/get url
                {:query-params   (merge (auth/credentials :get url user-params) user-params)
                 :cookie-policy  :standard
                 :decode-cookies false})))

(defn collections-entries-add
  "makes POST collections/entries/add request"
  ;e.g.) (collections-entries-add :id "custom-1287073494606389248" :tweet_id "390839888012382208")
  [& {:as user-params}]
  (let [url "https://api.twitter.com/1.1/collections/entries/add.json"]
    (client/post url
                 {:query-params   (merge (auth/credentials :post url user-params) user-params)
                  :cookie-policy  :standard
                  :decode-cookies false})))


;;TODO - unfinished below
(defn collections-entries-curate
  "makes POST collections/entries/curate request"
  ;e.g.) (collections-entries-curate :id "custom-1287073494606389248"
  ;                                   :changes [{:op "add"
  ;                                              :tweet_id "390839888012382208"}
  ;                                             {:op "add"
  ;                                              :tweet_id          "390897780949925889",
  ;                                              :relative_to "1240723216412205062",
  ;                                              :above       false}])
  [& {:as body}]
  (let [url "https://api.twitter.com/1.1/collections/entries/curate.json"]
    (client/post url
                 {:query-params   (merge (auth/credentials :post url body) body)
                  :body (json/write-str body)
                  :cookie-policy  :standard
                  :content-type :application/json
                  :decode-cookies false})))

;;TODO - solve error from expression below
(collections-entries-curate :id "custom-1287073494606389248"
                            :changes [{:op "add",
                                       :tweet_id "390839888012382208"}
                                      {:op "add",
                                       :tweet_id          "390897780949925889",
                                       :relative_to "1240723216412205062",
                                       :above       false}])

