(ns nav-demo2
  (:require [clojure.datafy :use datafy])
  (:import [java.nio.file Paths Path Files]))

(defn make-path
  "Create a java.nio.file.Path from an array of path components"
  [part & parts]
  (Paths/get part (into-array String parts)))

(defn maybe-children
  "If path is a directory, return the names of its children as a seq.
  Add metadata so that 'nav' will look up the underlying Path object from the name.
  Otherwise return nil."
  [path]
  (try
    (when (Files/isDirectory path (into-array java.nio.file.LinkOption []))
      (let [files (.toArray (Files/list path))
            names (map #(-> % .getFileName .toString) files)
            nav-fn (fn [_ k _] (get files k))]
        (with-meta names {'clojure.core.protocols/nav nav-fn})))
    (catch java.nio.file.AccessDeniedException e [])))

(extend-protocol clojure.core.protocols/Datafiable
  java.nio.file.Path
  ;; For a Path, return its name, size and children (if any) as pure data
  (datafy [p] (conj {:name (.toString p)
                     :size (Files/size p)}
                    (if-let [children (maybe-children p)]
                      {:children children}))))

(defn -main []
  (println (datafy (make-path "/"))))
