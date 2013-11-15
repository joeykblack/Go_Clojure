(ns jkb.go.util)


; (chain start
;        (f1 :result x)
;        (f2 z :result))
; => (f2 z (f1 start x))
(defmacro chain
  "Chain applicative functors"
  [& funs]
  (reduce (fn [f1 f2] 
            (map (fn [atom]
                   (if (= :result atom) f1 atom)) 
                 f2)) 
          funs))

(defn coord-to-index
  "convert 1,1 to index"
  [board-size c r]
  (+ c 
    (* r 
       (+ 1 board-size))))


(defn other-player
  [color]
  (if (= color :black) :white :black))

(defn parse-color
  [c]
  (if (or (= c :black) (= c \b) (= c "b") (= c "black"))
    :black
    :white))

(defn col-chars
  [board-size]
  (take board-size (filter #(not= \I %) (map char (range 65 91)))))

(defn get-col
  [board-size vertex]
  (+ 1 (.indexOf (col-chars board-size) (nth vertex 0))))

(defn get-row
  [vertex]
  (read-string (apply str (rest vertex))))

(defn read-index
  "returns index"
  [game-state vertex]
  (let [board-size (:board-size game-state 9)] 
    (coord-to-index board-size  
                    (get-col board-size vertex)
                    (get-row vertex))))

(defn remove-nth
  [coll n]
  (into (subvec coll n) (subvec coll (inc n) (count coll))))








