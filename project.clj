(defproject hangtime "1.0.0"
  :source-paths ["src/clj"]
  :plugins [[lein-cljsbuild "0.3.0"]]
  :dependencies [[org.clojure/clojure "1.5.0"]
                 [aleph "0.3.0-SNAPSHOT"]
                 [compojure "1.1.5"]
                 [de.ubercode.clostache/clostache "1.3.1"]
                 [prismatic/dommy "0.1.0"]]
  :main hangtime.server.core
  :aot [hangtime.server.core]
  :cljsbuild {:builds
              [{:source-paths ["src/cljs"]
                :id "dev"
                :compiler {:pretty-print true
                           :output-dir "resources/public/gen"
                           :output-to "resources/public/gen/app.js"
                           :optimizations :none}
                }]
              })

