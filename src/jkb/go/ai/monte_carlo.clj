(ns jkb.go.ai.monte-carlo)


(use '(jkb.go.data board))
(use '(jkb.go.data board-util))
(use '(jkb.go.data.display showboard))
(use '(jkb.go util))
(use '(jkb.go.play command))
(use '(jkb.go.play moving))
(use '(jkb.go.play eval))

(def NUM-GAMES 100)

(defn rnd-game
  "try to play all empty spaces in random order"
  [game-state empties]
  (reduce (fn [gs move]
            (make-move gs (other-player (:last-play gs :black)) move))
          game-state
          (shuffle empties)))

(defn mc-eval
  "num games won / num games"
  [game-state color index empties num-games]
  (let [gs (make-move game-state color index)]
    (float
      (/ (count (filter #(= color %1)
                        (repeatedly num-games
                                    #(get-winner (rnd-game gs empties)))))
         num-games))))
  

(defn mc-genmove
  "gen move using monte carlo"
  [game-state color]
  (let [empties (map first 
                     (filter #(= (second %) :empty)
                             (map-indexed vector (get-in game-state [:board :points]))))]
    (first ; get index from pair [index score]
      (first ; get highest ranking move
        (reverse ; sort desc
          (sort-by ; sort by score
            second ; key to score
            (map (fn [move] [move ; index of move
                             (mc-eval game-state ; score 
                                      color 
                                      move 
                                      (filter (fn [e] (not= e move)) empties)
                                      NUM-GAMES)]) ; num games  
                 empties)))))))
        
