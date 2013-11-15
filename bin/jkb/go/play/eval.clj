(ns jkb.go.play.eval)

(use '(jkb.go.data board))
(use '(jkb.go.data board-util))
(use '(jkb.go.data.display showboard))
(use '(jkb.go util))
(use '(jkb.go.play command))
(use '(jkb.go.play moving))
(use 'clojure.set)

; rules http://homepages.cwi.nl/~tromp/go.html


(defn color-point
  [points point]
  (if (or (= point :edge) (= point :empty))
    point
    (find-color {:points points} point)))

(defn color-points
  "replace all occupied ponts with :black or :white"
  [points]
  (map #(color-point points %1)
       points))

(defn get-new-set
  [s last-s board-size]
   (difference
     (apply union 
            (map #(set (neighbor-indexes board-size %1)) s))
     s last-s))

(defn break-wave
  [points wave-set]
  (filter #(let [p (nth points %1)] 
             (and (not= p :black) 
                  (not= p :white) 
                  (not= p :neutral)
                  (not= p :edge)))
          wave-set))

(defn wash-point
  [point color]
  (if (or (and (= color :white) (= point :black-claim))
          (and (= color :black) (= point :white-claim)))
    :neutral
    (if (= color :white) :white-claim :black-claim)))

(defn wash-board
  "assoc points in set"
  [points wave-set color]
  (let [vpoints (vec points)]
    (reduce (fn [p index] 
              (assoc p index (wash-point (nth p index) color))) 
            vpoints 
            wave-set)))

(defn wave
  "Collors all points as :black :white :neutral or :edge by expanding waves (sets of indexes)"
  [points board-size 
   black-wave-set last-black-wave-set 
   white-wave-set last-white-wave-set]
  (if (and (empty? black-wave-set) (empty? white-wave-set))
    points
    (let [new-black-set (get-new-set black-wave-set last-black-wave-set board-size)
          new-black-set (break-wave points new-black-set)
          new-white-set (get-new-set white-wave-set last-white-wave-set board-size)
          new-white-set (break-wave points new-white-set)
          new-points (wash-board points new-black-set :black)
          new-points (wash-board new-points new-white-set :white)]
      (wave new-points board-size
            new-black-set black-wave-set
            new-white-set white-wave-set))))
          
    
(defn fill-territory
  "fill empties only reached by one color"
  [points board-size]
  (let [get-index-set (fn [points color]
                        (set (filter #(not= %1 nil) 
                                     (map-indexed #(if (= %2 color) %1 nil) 
                                                  points))))]
    (wave points 
          board-size
          (get-index-set points :black) #{}
          (get-index-set points :white) #{})))

(defn get-winner 
  "determine winner"
  [game-state]
  (let [points (color-points (get-in game-state [:board :points]))
        points (fill-territory points (:board-size game-state))
        black-score (count (filter #(or (= %1 :black) (= %1 :black-claim)) points))
        white-score (count (filter #(or (= %1 :white) (= %1 :white-claim)) points))
        white-score (+ white-score (:komi game-state 0))]
    (if (> black-score white-score)
      :black
      (if (< black-score white-score)
        :white
        :tie))))

