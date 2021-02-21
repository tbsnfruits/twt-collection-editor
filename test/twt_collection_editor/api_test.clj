(ns twt-collection-editor.api-test
  (:require [clojure.test :refer :all]
            [twt-collection-editor.api :refer :all]))

(def test-coll-id "custom-1287073494606389248")

(def test-tweet {:harry "1356626624679092225"
                 :kim   "1356626664898273281"
                 :jean  "1356626702269485057"
                 :judit "1356626731327574016"
                 :alice "1356626762256404481"
                 :jules "1356626886009393152"})


;에러가 발생햇을 때도 200 반환됨, 이에 맞춰 코드 수정 필요
(deftest endpoint
  (testing "all collection/entries endpoints"
    (is (= 200 (:status (collections-entries :id test-coll-id))))

    (is (= 200 (:status (collections-entries-add :id test-coll-id
                                                 :tweet_id (test-tweet :harry)))))
    (is (= 200 (:status (collections-entries-add :id test-coll-id
                                                 :tweet_id (test-tweet :jean)))))

    (is (= 200 (:status (collections-entries-add :id test-coll-id
                                                 :tweet_id (test-tweet :kim)
                                                 :relative_to (test-tweet :harry) ;relative tweet id
                                                 :above false))))

    (is (= 200 (:status (collections-entries-move :id test-coll-id
                                                  :tweet_id (test-tweet :jean)
                                                  :relative_to (test-tweet :kim)
                                                  :above true))))

    (is (= 200 (:status (collections-entries-remove :id test-coll-id
                                                    :tweet_id (test-tweet :harry)))))

    (is (= 200 (:status (collections-entries-curate :id test-coll-id
                                                    :changes [{:op       "add",
                                                               :tweet_id (test-tweet :judit)}
                                                              {:op       "add",
                                                               :tweet_id (test-tweet :alice)
                                                               :relative_to (test-tweet :kim)
                                                               :above true
                                                               }]))))
    (is (= 200 (:status (collections-entries-curate :id test-coll-id
                                                    :changes [{:op       "remove",
                                                               :tweet_id test-tweet1}
                                                              {:op       "remove",
                                                               :tweet_id test-tweet2}]))))))