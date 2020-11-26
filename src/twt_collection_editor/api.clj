(ns twt-collection-editor.api
  (:require [cheshire.core :refer :all]
            [clj-http.client :as http]
            [twt-collection-editor.auth :as auth]))

(def apis {:collections-entries        {:method :get
                                         :url    "https://api.twitter.com/1.1/collections/entries.json"}
            :collections-entries-add    {:method :post
                                         :url    "https://api.twitter.com/1.1/collections/entries/add.json"}
            :collections-entries-curate {:method :post
                                         :url    "https://api.twitter.com/1.1/collections/entries/curate.json"}
            :collections-entries-move   {:method :post
                                         :url    "https://api.twitter.com/1.1/collections/entries/move.json"}
            :collections-entries-remove {:method :post
                                         :url    "https://api.twitter.com/1.1/collections/entries/remove.json"}})

(defmacro make-endpoint-function
  [endpoint-name]
  (let [endpoint (endpoint-name apis)
             method (:method endpoint)
             url (:url endpoint)]
    `(defn ~(symbol endpoint-name)
       [& {:as params#}]
       (~(symbol (str "http/" (name method))) ~url
         {:query-params   (merge (auth/credentials ~method ~url params#) params#)
          :cookie-policy  :standard
          :decode-cookies false
          :debug? true}) )))

(make-endpoint-function :collections-entries)
(make-endpoint-function :collections-entries-add)
(make-endpoint-function :collections-entries-move)
(make-endpoint-function :collections-entries-remove)

(defn collections-entries-curate
  "makes POST collections/entries/curate request"
  [& {:as body}]
  (let [url "https://api.twitter.com/1.1/collections/entries/curate.json"]
    (http/post url
                 {:query-params     (auth/credentials :post url {}) ;NO user parameter or body to be passed to create signature
                  :headers          {"Content-Type"  "application/json"}
                  :body             (generate-string body)
                  :cookie-policy    :standard
                  :decode-cookies   false
                  :debug?           true
                  :debug-body       true
                  :throw-exceptions false})))