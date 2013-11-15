(ns jkb.go.ai.mc-game-test
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

(def game-state (play game-state :black "A1"))
(def game-state (play game-state :black "B1"))
(def game-state (play game-state :black "C1"))

(def game-state (play game-state :white "A2"))
(def game-state (play game-state :white "B2"))
(def game-state (play game-state :white "C2"))

(def game-state (play game-state :black "A3"))
(def game-state (play game-state :black "B3"))
(def game-state (play game-state :black "C3"))

; 100 games
;"Elapsed time: 36763.611962 msecs"
;"Elapsed time: 32252.912082 msecs" without eval
;"Elapsed time: 35558.404161 msecs" without shuffle

;"Elapsed time:  7571.209926 msecs" without make-move

;"Elapsed time: 27447.463982 msecs" without decrement neighbor libs in make-move
;"Elapsed time: 22426.027485 msecs" without merge while adding libs
;"Elapsed time: 27888.015302 msecs" without remove opp dead while adding libs
;"Elapsed time: 36179.37029 msecs" without suicide test

(time (def index (mc-genmove game-state :white)))
(is (= index 14))
(def game-state (make-move game-state :white index))



;(def game-state (do-ai game-state :black))
;(def game-state (do-ai game-state :white))
;(def game-state (do-ai game-state :black))
;(def game-state (do-ai game-state :white))
;(def game-state (do-ai game-state :black))
;(def game-state (do-ai game-state :white))
;(def game-state (do-ai game-state :black))
;(def game-state (do-ai game-state :white))


;(show-board board-size (:board game-state))


