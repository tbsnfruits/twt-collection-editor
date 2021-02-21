(ns twt-collection-editor.testing
  (:require [twt-collection-editor.core :refer :all]
            [twt-collection-editor.api :as api]))

(def test-coll-id "custom-1358772597987545090")

(def default-twt "1357967060542779392")

(def test-tweet {:harry "1356626624679092225"
                 :kim   "1356626664898273281"
                 :jean  "1356626702269485057"
                 :judit "1356626731327574016"
                 :alice "1356626762256404481"
                 :jules "1356626886009393152"})

(defn reset []
  (let [tw-ids (list-tweets test-coll-id)]
    (remove-twts test-coll-id
                 tw-ids))

  (get-body (api/collections-entries-add :id test-coll-id
                                         :tweet_id default-twt)))


