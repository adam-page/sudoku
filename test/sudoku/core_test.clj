;;;; Sudoku Solver Tests
;;;; Author: Adam Page
;;;; Created: Dec 30, 2018

(ns sudoku.core-test
  (:require [clojure.test :refer :all]
            [sudoku.core :refer :all]))

(def prefixes
  "Puzzle name prefixes."
  ["s01" "s02" "s03" "s04" "s05" "s06" "s07" "s08"
   "s09" "s10" "s11" "s12" "s13" "s14" "s15"])

(defn add-sol-midfix
  "Adds the _s to the prefix names which makes it a solution file."
  [filenames]
  (map #(str % "_s") filenames))

(defn end-filenames
  "The adds all postfixes to the testing file names"
  [prefixes]
  (concat
    (map #(str "sudokus/" % "a") prefixes)
    (map #(str "sudokus/" % "b") prefixes)
    (map #(str "sudokus/" % "c") prefixes)))

(defn add-extension
  "Adds file extensions to the filenames for testing."
  [filenames]
  (map #(str % ".txt") filenames))

(def puzz-paths
  "Puzzle pathnames."
  (add-extension (end-filenames prefixes)))

(def sol-paths
  "Solution pathnames."
  (add-extension (add-sol-midfix (end-filenames prefixes))))

(def paths
  "Puzzle and solution pathnames."
  (map vector puzz-paths sol-paths))

(defn get-iter
  [])

(deftest backtracker-tester
  (testing "Testing the backtracker solver against all puzzles and solutions."
    (is (every? #(= (get-puzzle (second %))
                    (backtracker (get-puzzle (first %)))) paths))))







;(run-all-tests)