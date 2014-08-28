(ns transducers.core
  (:use  clojure.test)
  (:require [clojure.core.reducers :as r]
            [clojure.core.async :as async]))



(transduce (map inc) + [1 2 3])
(into [] (map inc) [1 2 3])
(sequence (map inc) [1 2 3])

(def ls (sequence (map (fn [x]
                         (println "<" x ">")
                         (inc x)))
                  [1 2 3]))
(take 1 ls)
(take 2 ls)
ls

(into []
      (comp (map inc)
            (filter (partial < 2)))
      [1 2 3])

;; Transducer a b = forall r . (r -> a -> r) -> (r -> b -> r)

(((map inc) conj) [] 1)

(((comp (map inc) (map str)) conj) [] 1)

;;

(defn map-t [f]
  #_todo)

(is (= (into [] (map-t inc) [1 2 3])
       [2 3 4]))

;; reducers

(into [] (r/map inc [1 2 3]))
(into [] ((comp (r/map inc) (r/map dec)) [1 2 3]))

(r/map inc [1 2 3])
(source into)
(source reduce)
;; (reduce + (r/map inc [1 2 3])) === (reduce (fn [ret x] (+ ret (inc x))) (+) [1 2 3])


(def r1 #_todo)

(id (= (into [] (r1 [1 2 3]))
       (["2" "3" "4"])))

;;

(def sample-data (mapv (fn [_] (rand)) (range 10000000)))

(time (->> sample-data
           (filter (partial > 0.5))
           (map dec)
           (map #(* % 2))
           (reduce +)))

(def reducer-1 (comp (r/map #(* % 2))
                     (r/map dec)
                     (r/filter (partial > 0.5))))

(time (reduce + (reducer-1 sample-data)))
(time (r/fold + (reducer-1 sample-data)))

(def transducer-1 (comp (filter (partial > 0.5))
                        (map dec)
                        (map #(* % 2))))

(time (transduce transducer-1 + sample-data))
(time (reduce (transducer-1 +) sample-data))
(time (r/fold (transducer-1 +) sample-data))

;;

(defn identity-transducer
  [reduction-function]
  (fn
    ([] (reduction-function))
    ([result] (reduction-function result))
    ([result input] (reduction-function result input))))

(source map)

(transduce identity-transducer + [])

(source transduce)

(defn duplicate [yield]
  #_todo)

(is (= (into [] duplicate [1 2 3])
       [1 1 2 2 3 3]))

(defn take-while-t [pred]
  #_todo)

(is (= (into [] (take-while-t (partial > 5)) [1 2 3 4 5 6 7 8])
       (into [] (take-while   (partial > 5)) [1 2 3 4 5 6 7 8])))

(defn i-hate-fives [yield]
  #_todo)

(is (= (into [] i-hate-fives [1 2 3])
       [1 2 3]))
(is (= (into [] i-hate-fives [1 2 5 2 3 5 1 2])
       [1 2 "aggrh" 5 2 3]))

(sequence (comp duplicate i-hate-fives) [1 2 3 4 5 6])

(defn partition-by-t [f]
  #_todo)

(is (= (into [] (partition-by-t #(quot % 10)) [1 2 3 10 11 12 13 21 22 23])
       [[1 2 3] [10 11 12 13] [21 22 23]]))

;;

(def ch (async/chan 1 (comp duplicate (map inc))))

(async/go-loop []
  (println "ch => " (async/<! ch))
  (recur))

(async/put! ch 1)
