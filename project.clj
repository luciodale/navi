(defproject navi "0.0.1"
  :description "Horizontal tab based menu with scrolling tabs"
  :url "https://github.com/luciodale/navi"
  :license {:name "MIT"}
  :source-paths ["src"]
  :profiles {:uberjar {:aot :all}}
  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]])
