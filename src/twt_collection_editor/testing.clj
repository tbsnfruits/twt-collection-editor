(ns twt-collection-editor.testing
  (:require [twt-collection-editor.core :refer :all]
            [twt-collection-editor.api :as api]))

(def mito-coll-id (str "custom-" "1395708400877539331"))

(defn split [reg str]
  (clojure.string/split str reg))

(def twt-list (->> "resources/mito.txt"
                   slurp
                   (split #"https://twitter.com/teaba_g/status/")
                   rest))

(add-twt-list-to mito-coll-id twt-list)