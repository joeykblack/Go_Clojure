(ns jkb.go.play.moving-test
  (:use clojure.test
        jkb.go.core))

(use '(jkb.go.data board))
(use '(jkb.go.data board-util))
(use '(jkb.go.data.display showboard))
(use '(jkb.go util))
(use '(jkb.go.play command))
(use '(jkb.go.play moving))

(def board-size 9)
(def game-state {})
(def game-state (boardsize game-state board-size))
(def game-state (clear_board game-state))

(time
  (def game-state
    (reduce (fn [gs move]
              (make-move gs (other-player (:last-play gs)) move))
            game-state
            (shuffle (board-indexes board-size)))))

;(show-board board-size (:board game-state))


