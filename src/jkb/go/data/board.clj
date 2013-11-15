(ns jkb.go.data.board)

(require 'clojure.string)
(use '(jkb.go util))
(use '(jkb.go.data board-util))

;  Board layout:
; 100 e e e ... e e 110
;  90 e x x ... x 99
;  ::             ::
;  20 e x x ... x 29
;  10 e x x ... x 19
;     e e e ... e 
;     0 1 2 ... 9
;  Board opps:
;   create group
;   merge groups
;   change group libs
;   remove group

;(defstruct Point :parent) ; also in parent: :color :rank :members
;(defstruct Board :points) ; :focus


(defn empty-board
  "Create empty board"
  [board-size]
  {:points (apply vector 
                  (for [i (range 0 (+ 1 
                                     (* (+ 1 board-size) 
                                        (+ 2 board-size))))]
                    (if (onboard? board-size i)
                      :empty
                      :edge)))})

(defn add-new-group
  "Create a new group"
  [board board-size i color]
  (assoc-in board [:points i] 
            {:parent i 
             :color color 
             :libs (count-libs board board-size i) 
             :rank 0 
             :members (list i)}))

(defn focus-on-group
  "focus on group for index (uses compression)"
  ([board index]
    (focus-on-group board index :focus))
  ([board index key]
    (let [point (get-in board [:points index])]
      (if (or (= point :empty) (= point :edge))
        (assoc board key index)
        (if (instance? java.lang.Number point)
          ; Recursively find group and reparent for compression
          (let [board (focus-on-group board point key)] ; point is a number/index
            (assoc-in board [:points index] (get board key)))
          ; Found group, so focus on it
          (assoc board key index)))))) ; index is the index of the group
  
(defn union-2-groups
  "mawage, mawage is what bwings us togethaw today"
  [board index1 index2]
  (let [board (focus-on-group (focus-on-group board index2 :group2) index1 :group1)
        group1 (:group1 board)
        group2 (:group2 board)]
    (if (or (= group2 :empty)
            (= group2 :edge)
            (= group1 group2))
      board
      (let [points (:points board)
            c1 (get-in points [group1 :color])
            c2 (get-in points [group2 :color])]
        (if (not= c1 c2)
          board
          (let [r1 (get-in points [group1 :rank])
                r2 (get-in points [group2 :rank])
                [parent child] (if (< r1 r2) [group2 group1] [group1 group2])]
            (do-merge board parent child (= r1 r2))))))))

(defn union-groups
  "mawage, mawage is what bwings us togethaw today"
  [board index1 index2 & indexes]
  (if (empty? indexes)
    (union-2-groups board index1 index2)
    (apply (partial union-groups 
                    (union-2-groups board index1 index2) 
                    index1 
                    (first indexes))
           (rest indexes))))

(defn map-index-libs
  "decriment libs of group at index"
  [op board index]
  (let [point (get-in board [:points index])]
    (if (or (= point :empty) (= point :edge))
      board
      (let [board (focus-on-group board index)]
        (update-in board [:points (:focus board) :libs] op)))))
                  
(defn map-neighbor-libs
  "decriment neighboring groups"
  [op board board-size index]
  (reduce (partial map-index-libs op)
          board 
          (neighbor-indexes board-size index)))

(defn merge-groups
  "Merge neighboring groups"
  [board board-size index]
  (apply (partial union-groups board index) 
         (neighbor-indexes board-size index)))



(defn he'll-be-dead-in-a-minute
  "You'll be stone dead."
  [my-color board-size board test-index]
  (let [board (focus-on-group board test-index)
        test-group-root (get-in board [:points (:focus board)])]
    (if (or (= test-group-root :empty) 
            (= test-group-root :edge)  
            (> (:libs test-group-root) 0)
            (= my-color (:color test-group-root)))
      board
      (let [members (:members test-group-root)
            ; remove dead stones
            board (reduce #(assoc-in %1 [:points %2] :empty) board members)
            ; inc neighbors of dead
            board (reduce #(map-neighbor-libs inc %1 board-size %2) board members)]
        ; if ko is set
        (if (:ko board)
          ; no ko
          (assoc board :ko -1)
          ; set ko to captured
          (assoc board :ko (first members)))))))
          
            
            
(defn bring-out-your-dead
  "I'm feeling better!"
  [board board-size index]
  (let [board (focus-on-group board index)
        my-color (get-in board [:points (:focus board) :color])]
    (reduce (partial he'll-be-dead-in-a-minute my-color board-size)  
            board
            (neighbor-indexes board-size index))))
  
(defn don't-do-it
  "Test of suicide"
  [board index]
  (let [board (focus-on-group board index)
        libs (get-in board [:points (:focus board) :libs])]
    (if (> libs 0)
      board
      nil)))

