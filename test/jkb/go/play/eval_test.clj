(ns jkb.go.play.eval-test
  (:use clojure.test
        jkb.go.core))

(use '(jkb.go.data board))
(use '(jkb.go.data board-util))
(use '(jkb.go.data.display showboard))
(use '(jkb.go util))
(use '(jkb.go.play command))
(use '(jkb.go.play moving))
(use '(jkb.go.play eval))

(def board-size 9)
(def game-state {})
(def game-state (boardsize game-state board-size))
(def game-state (clear_board game-state))

(def game-state (play game-state :black "A1"))

(def points (get-in game-state [:board :points]))

(is (= :black (color-point points (nth points 11))))
(def points (color-points points))
(is (= (set points) #{:edge :empty :black}))

(is (= (get-new-set #{11} #{} board-size) #{12 21}))
(is (= (set (break-wave points #{12 21 11})) #{12 21}))
(is (= (wash-point :empty :black) :black-claim))
(is (= (wash-point :empty :white) :white-claim))
(is (= (wash-point :black-claim :white) :neutral))

(is (= (nth (wash-board points #{12 21} :black) 12) :black-claim))

(is (= :black (get-winner game-state)))

(def game-state (play game-state :white "A2"))
(is (= :tie (get-winner game-state)))

(def game-state (play game-state :white "B1"))
(is (= :white (get-winner game-state)))



