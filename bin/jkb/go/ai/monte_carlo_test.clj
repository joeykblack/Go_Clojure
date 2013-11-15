(ns jkb.go.ai.monte-carlo-test
  (:use clojure.test
        jkb.go.core))

(use '(jkb.go.data board))
(use '(jkb.go.data board-util))
(use '(jkb.go.data.display showboard))
(use '(jkb.go util))
(use '(jkb.go.play command))
(use '(jkb.go.play moving))
(use '(jkb.go.play eval))
(use '(jkb.go.ai monte-carlo))

(defn do-ai
  [gs color]
  (make-move gs color (mc-genmove gs color)))

(def board-size 9)
(def game-state {})
(def game-state (boardsize game-state board-size))
(def game-state (clear_board game-state))

(def game-state (play game-state :black "B1"))
(def game-state (play game-state :white "A1"))

(def empties (map first 
                  (filter #(= (second %) :empty)
                          (map-indexed vector (get-in game-state [:board :points])))))

(def rnd-game-state (rnd-game game-state empties))
;(show-board board-size (:board rnd-game-state))

(def value-bad (mc-eval game-state :black 99 empties 100))
(def value-good (mc-eval game-state :black 21 empties 100))
(is (< value-bad value-good))



