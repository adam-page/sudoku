;;;; Sudoku Solver
;;;; Author: Adam Page
;;;; Created: Dec 30, 2018

(ns sudoku.core
  (:require [clojure.core.matrix :as m])
  (:gen-class))

(declare get-puzzle check-partial pretty-print backtracker)

(defn -main
  "I don't do a whole lot."
  []
  (let [puzzles {:easy {:puzzle (get-puzzle "sudokus/s01a.txt")
                        :solution (get-puzzle "sudokus/s01a_s.txt")}
                 :hard {:puzzle (get-puzzle "sudokus/s16.txt")
                        :solution (get-puzzle "sudokus/s16_s.txt")}}]
    (println "Easy solution result:"
             (time (= (backtracker ((get puzzles :easy) :puzzle))
                      ((get puzzles :easy) :solution))))
    (println "Hard solution result:"
             (time (= (backtracker ((get puzzles :hard) :puzzle))
                      ((get puzzles :hard) :solution))))))

(defn get-puzzle
  "Get a sudoku puzzle from a file specified by its path."
  [path]
  (if (re-find #"_s" path)
    (m/matrix
     (m/submatrix
      (m/matrix
       (partition 12
                  (map #(Integer. %) (re-seq #"\d" (slurp path))))) 0 9 0 9))
    (m/matrix (partition 9 (map #(Integer. %) (re-seq #"\d" (slurp path)))))))

(defn check-line
  "Checks a sudoku line for the presence of 1-9, ignoring 0s."
  [line]
  (loop [num (first line) line (rest line) seen #{}]
    (if (nil? num)
      true
      (if (not= num 0)
        (if (contains? seen num)
          false
          (recur (first line) (rest line) (conj seen num)))
        (recur (first line) (rest line) seen)))))

(defn check
  "Checks a sudoku solution. If full-check? is true, does not allow for 0s
  (which normally serve as unfilled spaces)."
  [puzzle, full-check?]
  (when (true? full-check?)
    (if (contains? puzzle 0) false true))
  (let [boxes {0 '(0 0), 1 '(0 3), 2 '(0 6), 3 '(3 0), 4 '(3 3)
               5 '(3 6), 6 '(6 0), 7 '(6 3), 8 '(6 6)}]
    (loop [i 0]
      (if (<= i 8)
        (if-not (and (check-line (m/get-row puzzle i))
                     (check-line (m/get-column puzzle i))
                     (check-line (apply concat
                                        (m/matrix
                                         (m/submatrix puzzle
                                                      (first (boxes i)) 3
                                                      (second (boxes i)) 3)))))
          false
          (recur (inc i)))
        true))))

(defn pretty-print
  "Printlns out a sudoku puzzle in a nice format."
  [p]
  (loop [s (first p) r (rest p)]
    (println s)
    (if-not (empty? r)
      (recur (first r) (rest r))
      (println ""))))

(defn backtracker
  "Solves a sudoku puzzle using a brute-force backtracking method."
  [puzzle]
  (loop [p puzzle y 0 x 0 n 1 stack ()]
    (if (< y 9)
      (if (and (zero? (m/mget p y x)) (< n 10))
        (if (check (m/mset p y x n) false)
          (recur ; successful branch found
           (m/mset p y x n) (if (= x 8) (inc y) y) (if (= x 8) 0 (inc x)) 1
           (cons {:state p :y y :x x :n n} stack))
          (if (< n 9)
            (recur ; trx another number here
             p y x (inc n) stack)
            (if (> 9 (get (first stack) :n))
              (recur ; backtrack!
               (get (first stack) :state)
               (get (first stack) :y)
               (get (first stack) :x)
               (inc (get (first stack) :n))
               (rest stack))
              (recur ; double-time!
               (get (second stack) :state)
               (get (second stack) :y)
               (get (second stack) :x)
               (inc (get (second stack) :n))
               (rest (rest stack))))))
        (recur ; skip hints
         p (if (= x 8) (inc y) y) (if (= x 8) 0 (inc x)) 1 stack))
      p)))

(defn get-current
  "Gets a set of the coordinates of the current board."
  [board]
  (loop [x 0 y 0 coordinates #{}]
    (if (< y 9)
      (recur (if (= x 8) 0 (inc x)) (if (= x 8) (inc y) y)
             (if-not (zero? (m/mget board y x))
               (conj coordinates (list x y))
               coordinates))
      coordinates)))

; (defn remove-errors
;   "Removes all errors from a sudoku board excepting initial hints."
;   [board initial]
;   (loop [b board n 0]
;     (if (= 9 n)
;       b
;       (if-not (check-line (m/get-row b n))
;         (comment fix row)
;         (if-not (check-line (m/get-col b n))
;           (comment fix column)
;           (if-not (check-line (apply concat
;                                      (m/matrix
;                                        (m/submatrix b))))
;                                                     (first (boxes i)) 3
;                                                     (second (boxes i)) 3))))
;             (comment fix square)
;             (recur b (inc n))))))))

; (defn randomizer
;   "Solves a sudoku puzzle using a random fill, remove, refill algorithm."
;   [board]
;   (loop [b board x 0 y 0 n (inc (rand-int 9)) initial (get-current board)
;          freqs (frequencies (apply concat board))]
;     (pretty-print b)
;     (println x y n)
;     (println initial)
;     (println freqs)
;     (println)
;     (println)
;     (if (= (freqs 0) 0)
;       (if (check b true)
;         b
;         (recur (remove-errors b initial) 0 0 (inc (rand-int 9))
;                initial (frequencies (apply concat board))))
;       (if-not (zero? (m/mget b y x))
;         (recur b (if (= x 8) 0 (inc x)) (if (= x 8) (inc y) y) n initial freqs)
;         (if (> 9 (freqs n))
;           (recur (m/mset b y x n)
;                  (if (= x 8) 0 (inc x))
;                  (if (= x 8) (inc y) y)
;                  (inc (rand-int 9))
;                  initial
;                  (assoc freqs n (inc (freqs n)) 0 (dec (freqs 0))))
;           (recur b x y (inc (rand-int 9)) initial freqs))))))
