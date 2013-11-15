(ns jkb.go.core)

(use '(jkb.go.data board))
(use '(jkb.go.data board-util))
(use '(jkb.go.data.display showboard))
(use '(jkb.go util))
(use '(jkb.go.play command))
(use '(jkb.go.play moving))


(let [board-size 9
      cti (partial coord-to-index board-size)]
  (chain (empty-board board-size)
         (add-new-group :result
                        board-size
                        (cti 1 1)
                        :black)
         (add-new-group :result
                        board-size
                        (cti 9 9)
                        :white)
         (add-new-group :result
                        board-size
                        (cti 1 2)
                        :black)
         (union-groups :result
                       (cti 1 1)
                       (cti 1 2))
         (add-new-group :result
                        board-size
                        (cti 1 3)
                        :black)
         (union-groups :result
                       (cti 1 1)
                       (cti 1 3))
         (add-new-group :result
                        board-size
                        (cti 9 8)
                        :white)
         (union-groups :result
                       (cti 9 8)
                       (cti 9 9))
         ;(focus-on-group :result (cti 1 2) :test))
         ;(show-board board-size :result)))
         (board-to-string board-size :result)))
  
       
       