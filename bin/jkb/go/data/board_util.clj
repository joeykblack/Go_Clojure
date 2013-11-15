(ns jkb.go.data.board-util)

; This should just be helper methods used in jkb.go.data.board
; they should not use any external functions

(defn onboard?
  "Is index on board"
  [board-size index]
  (and (> index (+ 1 board-size))
       (< index (Math/pow (+ 1 board-size) 2))
       (not= 0 (mod index (+ 1 board-size)))))

(defn board-indexes
  [board-size]
  (for [i (range 0 (+ 1 
                     (* (+ 1 board-size) 
                        (+ 2 board-size))))
        :when (onboard? board-size i)]
    i))

(defn neighbor-indexes
  [board-size index]
  (filter (partial onboard? board-size)
          [(- index 1)
           (+ index 1)
           (+ 1 index board-size)
           (- index board-size 1)]))

(defn get-neighbor-points
  "get neighbor ponts"
  [board board-size index]
  (map (partial nth (:points board)) 
       (neighbor-indexes board-size index)))

(defn count-libs
  "count empty spaces around index"
  [board board-size index]
  (count (filter #(= %1 :empty)
                 (get-neighbor-points board board-size index))))

(defn do-merge
  "merge groups together"
  ([board parent child do-inc]
    (let [points (:points board)
          parentPoint (nth points parent)
          childPoint (nth points child)
          parentPoint (if do-inc (update-in parentPoint [:rank] inc) parentPoint)
          parentPoint (assoc parentPoint :members 
                             (concat (:members parentPoint) 
                                     (:members childPoint)))
          parentPoint (update-in parentPoint [:libs] #(+ %1 (:libs childPoint)))
          points (assoc points parent parentPoint)
          points (assoc points child parent)]
      (assoc board :points points))))


(defn find-group
  "just find index (no compression)"
  ([board index]
    (let [point (get-in board [:points index])]
      (if (instance? java.lang.Number point)
        (find-group board point) ; point is a number/index
        index)))) ; index is the index of the group