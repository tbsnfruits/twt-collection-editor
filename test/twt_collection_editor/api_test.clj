(ns twt-collection-editor.api-test
  (:require [clojure.test :refer :all]
            [twt-collection-editor.api :refer :all]))

;각 api endpoint 정상작동여부 확인

(comment
  (collections-entries-curate :id "custom-1279095780536537088"
                              :changes [{:op       "add",
                                         :tweet_id "390897780949925889"}
                                        {:op       "add",
                                         :tweet_id "390853164611555329"
                                         :relative-to "390897780949925889"
                                         :above false}]))

