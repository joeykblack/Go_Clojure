(ns jkb.go.data.display.showboard)

(use '(jkb.go.data board))
(use '(jkb.go.data board-util))
(use '(jkb.go util))

(defn find-color
  "find color of point"
  [board point]
  (if (instance? java.lang.Number point)
    (get-in board [:points (find-group board point) :color]) ; point is a number/index
    (:color point)))

(defn show-point
  "displays point"
  [board point]
  (case point
    :empty "_ "
    :edge "\n"
    (if (= (find-color board point) :black) "x " "o ")))

(defn col-labels
  "a b c ..."
  [board-size]
  (apply str (interpose " " (col-chars board-size))))

(defn drop-ends
  "drop top and bottom of board"
  [board-size board]
  (drop (+ 1 board-size)
        (drop-last (+ 2 board-size)
                   (:points board))))

(defn board-to-string
  "convert the board to a string"
  [board-size board]
  (str
    (apply str 
           (map #(str "\n" (format "%2d " %1) %2)
                (range board-size 0 -1)
                (reverse 
                  (clojure.string/split-lines
                    (apply str (map (partial show-point board) (drop-ends board-size board)))))))
    "\n   " (col-labels board-size))) 
  
(defn show-board
  "print board"
  [board-size board]
  (println (board-to-string board-size board)))