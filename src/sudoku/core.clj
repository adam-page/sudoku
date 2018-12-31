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
                        :solution (get-puzzle "sudokus/s01a_s.txt")},
                 :hard {:puzzle (get-puzzle "sudokus/s16.txt")
                        :solution (get-puzzle "sudokus/s16_s.txt")}}]
    (println "Easy solution result:"
             (time (= (backtracker ((get puzzles :easy) :puzzle))
                      ((get puzzles :easy) :solution))))
    (println "Hard solution result:"
             (time (= (backtracker ((get puzzles :hard) :puzzle))
                      ((get puzzles :hard) :solution))))))

(defn get-puzzle
  "Get a sudoku puzzle from a file specified by path."
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
  (let [boxes {0 '(0 0), 1 '(0 3), 2 '(0 6), 3 '(3 0), 4 '(3 3),
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
  (loop [p puzzle x 0 y 0 n 1 stack ()]
    (if (< x 9)
      (if (and (= (m/mget p x y) 0) (< n 10))
        (if (check (m/mset p x y n) false)
          (recur ; successful branch found
            (m/mset p x y n) (if (= y 8) (inc x) x) (if (= y 8) 0 (inc y)) 1
            (cons {:state p :x x :y y :n n} stack))
          (if (< n 9)
            (recur ; try another number here
              p x y (inc n) stack)
            (if (> 9 (get (first stack) :n))
                (recur ; backtrack!
                  (get (first stack) :state)
                  (get (first stack) :x)
                  (get (first stack) :y)
                  (inc (get (first stack) :n))
                  (rest stack))
                (recur ; double-time!
                  (get (second stack) :state)
                  (get (second stack) :x)
                  (get (second stack) :y)
                  (inc (get (second stack) :n))
                  (rest (rest stack))))))
        (recur ; skip hints
          p (if (= y 8) (inc x) x) (if (= y 8) 0 (inc y)) 1 stack))
      p)))
