(ns jkb.go.play.command)

(use '(jkb.go.data board))
(use '(jkb.go util))
(use '(jkb.go.play moving))


(def command-list #{
                   "protocol_version"
									 "name"
									 "version"
									 "known_command"
									 "list_commands"
									 "quit"
									 "boardsize"
									 "clear_board"
									 "komi"
									 "play"
									 "genmove"
                   })

(defn protocol_version
  "Protocol version"
  [game-state]
  (assoc game-state :result 2))
  
(defn myname
  "Name"
  [game-state]
  (assoc game-state :result "jkb clojure go"))

(defn version
  "Program version"
  [game-state]
  (assoc game-state :result "0.1"))

(defn known_command
  "is command known?"
  [game-state com]
  (assoc game-state :result (contains? command-list com)))
  
(defn list_commands
  "List of commands"
  [game-state]
  (assoc game-state :result (apply str (interpose "\n" command-list))))

(defn quit
  "Quit game"
  [game-state]
  game-state)

(defn clear_board
  "clear board"
  [game-state]
  (let [size (:board-size game-state)
        size (if size size 9)]
    (assoc
      (assoc game-state :board (empty-board size))
      :last-play :white)))

(defn boardsize
  "set board size"
  [game-state size]
  (clear_board (assoc game-state :board-size size)))

(defn komi
  "set komi"
  [game-state k]
  (assoc game-state :komi k))

(defn play
  "play move"
  ([game-state vertex]
    (play (other-player (:last-player game-state :white)) vertex))
  ([game-state c vertex]
    (make-move game-state (parse-color c) (read-index game-state vertex))))

(defn genmove
  "generate move"
  [game-state & [color]]
  game-state)








