(ns jkb.go.play.moving)

(use '(jkb.go util))
(use '(jkb.go.data board))

(defn occupied?
  "do initial validaiton on move"
  [game-state index]
  (not= :empty (get-in game-state [:board :points index])))

(defn ko?
  [game-state index]
  (= index (get-in game-state [:board :ko] -1)))

(defn clear-old
  [board]
  (assoc board :ko nil))

; make group counting libs
; decrement libs
; merge while adding libs
; remove opp dead while adding libs
; suicide test
(defn make-move
  "attempt to make move on board"
  [game-state color index]
  (if (occupied? game-state index)
    (assoc game-state :error "Occupied")
    (if (ko? game-state index)
      (assoc game-state :error "Ko")
      (let [board-size (:board-size game-state 9)
            maybe-board (clear-old (:board game-state))
            ; make group counting libs
            maybe-board (add-new-group maybe-board board-size index color)
            ; decrement neighbor libs
            maybe-board (map-neighbor-libs dec maybe-board board-size index)
            ; merge while adding libs
            maybe-board (merge-groups maybe-board board-size index)
            ; remove opp dead while adding libs
            maybe-board (bring-out-your-dead maybe-board board-size index)
            ; suicide test
            maybe-board (don't-do-it maybe-board index)
            ]
        (if maybe-board
          (assoc (assoc (assoc game-state :board maybe-board) :error nil) :last-play color)
          (assoc game-state :error "Not a valid move"))))))
  

             
             
             
             
             
             
           