(defproject hangtime "1.0.0"
  :source-paths ["src/clj"]
  :plugins [[lein-cljsbuild "0.3.0"]]
  :dependencies [[org.clojure/clojure "1.5.0"]
                 [aleph "0.3.0-SNAPSHOT"]
                 [de.ubercode.clostache/clostache "1.3.1"]
                 [prismatic/dommy "0.1.0"]
                 [hiccup "1.0.3"]
                 [com.novemberain/monger "1.5.0"]]
  :main hangtime.server.core
  :aot [hangtime.server.core hangtime.crossover.route]
  :cljsbuild {:crossovers [hangtime.crossover]
              :builds
              [{:source-paths ["src/cljs"]
                :id "dev"
                :compiler {:pretty-print true
                           :output-dir "resources/gen"
                           :output-to "resources/gen/app.js"
                           :optimizations :none}
                :crossover-path "crossover-cljs"
                :crossover-jar false
                }]
              })

