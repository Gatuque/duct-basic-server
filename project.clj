(defproject duct-basic-server "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [duct/core "0.8.0"]
                 [duct/module.logging "0.5.0"]
                 [duct/module.web "0.7.3"]
                 [duct/module.logging "0.5.0"]
                 [duct/migrator.ragtime "0.3.2"]
                 [duct/database.sql.hikaricp "0.4.0"]
                 [metosin/muuntaja "0.6.8"]
                 [metosin/reitit "0.7.0-alpha7"]
                 [metosin/reitit-dev "0.7.0-alpha7"]
                 [duct/database.sql.hikaricp "0.4.0"]
                 [org.postgresql/postgresql "42.6.0"]
                 [org.clojure/tools.cli "1.0.206"]
                 [conman "0.9.6"]
                 [buddy/buddy-hashers "1.4.0"]]
  :plugins [[duct/lein-duct "0.12.3"]]
  :main ^:skip-aot duct-basic-server.main
  :resource-paths ["resources" "target/resources"]
  :prep-tasks     ["javac" "compile" ["run" ":duct/compiler"]]
  :middleware     [lein-duct.plugin/middleware]
  :profiles
  {:dev  [:project/dev :profiles/dev]
   :repl {:prep-tasks   ^:replace ["javac" "compile"]
          :repl-options {:init-ns user}}
   :uberjar {:aot :all}
   :profiles/dev {}
   :project/dev  {:source-paths   ["dev/src"]
                  :resource-paths ["dev/resources"]}
   :test  {:source-paths   ["src" "dev/src" "test"]
           :resource-paths ["resources" "target/resources" "dev/resources"]
           :main ^:skip-aot test}}
  :aliases {"migrate" ["run" ":duct/migrator"]})
