(defproject sudoku "0.1.0"
            :description "A simple sudoku solver."
            :author "Adam Page"
            :notes "Puzzles and solutions downloaded
                    from: http://lipas.uwasa.fi/~timan/sudoku/"
            :url "https://github.com/adam-page/sudoku"
            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies [[org.clojure/clojure "1.8.0"]
                           [net.mikera/core.matrix "0.62.0"]]
            :plugins [[lein-kibit "0.1.6"]]
            :main ^:skip-aot sudoku.core
            :target-path "target/%s/"
            :profiles {:uberjar {:aot :all}}
            :jar-name "sudoku.jar"
            :uberjar-name "sudoku-standalone.jar")
