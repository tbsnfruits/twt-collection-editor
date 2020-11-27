(ns twt-collection-editor.api-test
  (:require [clojure.test :refer :all]
            [twt-collection-editor.api :refer :all]))

(def test-coll-id "custom-1287073494606389248")
(def test-tweet1 "652174303283277825")
(def test-tweet2 "651780213496545280")

(deftest endpoint
  (testing "all collection/entries endpoints"
    (is (= 200 (:status (collections-entries :id test-coll-id))))
    (is (= 200 (:status (collections-entries-add :id test-coll-id
                                                 :tweet_id test-tweet1))))
    (is (= 200 (:status (collections-entries-move :id test-coll-id
                                                  :tweet_id test-tweet1
                                                  :relative_to "1286950122987778048"
                                                  :above false))))
    (is (= 200 (:status (collections-entries-remove :id test-coll-id
                                                    :tweet_id test-tweet1))))
    (is (= 200 (:status (collections-entries-curate :id test-coll-id
                                                    :changes [{:op       "add",
                                                               :tweet_id test-tweet1}
                                                              {:op       "add",
                                                               :tweet_id test-tweet2}]))))
    (is (= 200 (:status (collections-entries-curate :id test-coll-id
                                                    :changes [{:op       "remove",
                                                               :tweet_id test-tweet1}
                                                              {:op       "remove",
                                                               :tweet_id test-tweet2}]))))))
