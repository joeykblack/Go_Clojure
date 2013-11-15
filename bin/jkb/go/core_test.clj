(ns jkb.go
  (:use clojure.test
        jkb.go.core))

(use '(jkb.go.data board))
(use '(jkb.go.data board-util))
(use '(jkb.go.data.display showboard))
(use '(jkb.go util))
(use '(jkb.go.play command))
(use '(jkb.go.play moving))

(def board-size 9)

(testing "Board opperations"
         (let [cti (partial coord-to-index board-size)
               board (chain (empty-board board-size)
                            (add-new-group :result board-size (cti 1 1) :black)
                            (add-new-group :result board-size (cti 9 9) :white)
                            (add-new-group :result board-size (cti 1 2) :black)
                            (union-groups :result (cti 1 1) (cti 1 2))
                            (add-new-group :result board-size (cti 1 3) :black)
                            (union-groups :result (cti 1 1) (cti 1 3))
                            (add-new-group :result board-size (cti 9 8) :white)
                            (union-groups :result (cti 9 8) (cti 9 9)))]
               (is (= (board-to-string board-size board)
                      "\n 9 _ _ _ _ _ _ _ _ o \n 8 _ _ _ _ _ _ _ _ o \n 7 _ _ _ _ _ _ _ _ _ \n 6 _ _ _ _ _ _ _ _ _ \n 5 _ _ _ _ _ _ _ _ _ \n 4 _ _ _ _ _ _ _ _ _ \n 3 x _ _ _ _ _ _ _ _ \n 2 x _ _ _ _ _ _ _ _ \n 1 x _ _ _ _ _ _ _ _ \n   A B C D E F G H J"
                      ))))
(testing "parse color"
         (is (= (parse-color :black) :black))
         (is (= (parse-color \b) :black))
         (is (= (parse-color "b") :black))
         (is (= (parse-color "black") :black))
         (is (= (parse-color :white) :white))
         (is (= (parse-color \w) :white))
         (is (= (parse-color "w") :white))
         (is (= (parse-color "white") :white)))

(def game-state {})
(def game-state (boardsize game-state board-size))
(def game-state (clear_board game-state))

(testing "read-index"
         (is (= 11 (read-index game-state "A1")))
         (is (= 22 (read-index game-state "B2"))))

(def game-state1 (assoc game-state :board 
                       (add-new-group (:board game-state) 
                                      (:board-size game-state) 
                                      11 
                                      :black)))

(is (= (get-in game-state1 [:board :points 11]) 
       {:parent 11, :color :black, :libs 2, :rank 0, :members '(11)})) 

(is (= false (occupied? game-state 11)))

(def color :black)
(def index 11)
(def board-size (:board-size game-state 9))
(def maybe-board (:board game-state))
(def maybe-board (add-new-group maybe-board board-size index color))
(def maybe-board (map-neighbor-libs dec maybe-board board-size index))
(def maybe-board (merge-groups maybe-board board-size index))
(def maybe-board (bring-out-your-dead maybe-board board-size index))
(def maybe-board (don't-do-it maybe-board index))
(is (not= nil maybe-board))

(def game-state (play game-state :black "A1"))
(is (= nil (:error game-state)))
(is (= 2 (get-in game-state [:board :points 11 :libs])))

(def game-state (play game-state :black "A1"))
(is (not= nil (:error game-state)))

(def game-state (play game-state :white "A2"))
(is (= 1 (get-in game-state [:board :points 11 :libs])))
(is (= 2 (get-in game-state [:board :points 21 :libs])))

(def game-state (play game-state :white "B1"))
(is (= 3 (get-in game-state [:board :points 21 :libs])))
(is (= 3 (get-in game-state [:board :points 12 :libs])))
(is (= :empty (get-in game-state [:board :points 11])))
           

(def game-state (play game-state :white "B2"))
(is (= 6 
       (get-in game-state [:board 
                           :points 
                           (:focus (focus-on-group (:board game-state) 12)) 
                           :libs])))


(def game-state (play game-state :black "A3"))
(def game-state (play game-state :black "B3"))
(is (= 4 
       (get-in game-state [:board 
                           :points 
                           (:focus (focus-on-group (:board game-state) 12)) 
                           :libs])))

; suicide
(def game-state (play game-state :black "A1"))
(is (not= nil (:error game-state)))
                           
(def game-state (play game-state :black "C2"))
(def game-state (play game-state :black "C1"))
(is (= 2
       (get-in game-state [:board 
                           :points 
                           (:focus (focus-on-group (:board game-state) 12)) 
                           :libs])))
                           
; capture group
(def game-state (play game-state :black "A1"))
(is (= nil (:error game-state)))                           
(is (= :empty (get-in game-state [:board :points 21])))                                    
(is (= :empty (get-in game-state [:board :points 22])))         
(is (= :empty (get-in game-state [:board :points 12])))
(is (= 5
       (get-in game-state [:board 
                           :points 
                           (:focus (focus-on-group (:board game-state) 31)) 
                           :libs])))
                          
; capture in top right corner
(def game-state (play game-state :white "J9"))
(def game-state (play game-state :black "H9"))
(def game-state (play game-state :black "J8"))
(is (= :empty (get-in game-state [:board :points (read-index game-state "J9")])))
(is (= 3 (get-in game-state [:board :points (read-index game-state "H9") :libs])))
(is (= 3 (get-in game-state [:board :points (read-index game-state "J8") :libs])))



; ko
(def game-state (clear_board game-state))
(def game-state (play game-state :white "A2"))
(def game-state (play game-state :white "B1"))
(def game-state (play game-state :black "B2"))
(def game-state (play game-state :black "C1"))
(def game-state (play game-state :black "A1"))
(def game-state (play game-state :white "B1")) ; ko move
(is (not= nil (:error game-state))) 











