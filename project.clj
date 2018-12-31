(defproject sudoku "0.1.0-SNAPSHOT"
            :description "FIXME: write description"
            :notes "Puzzles and solutions downloaded
                    from: http://lipas.uwasa.fi/~timan/sudoku/"
            :url "http://example.com/FIXME"
            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies [[org.clojure/clojure "1.8.0"]
                           [net.mikera/core.matrix "0.62.0"]]
            :main ^:skip-aot sudoku.core
            :target-path "target/%s/"
            :profiles {:uberjar {:aot :all}})
