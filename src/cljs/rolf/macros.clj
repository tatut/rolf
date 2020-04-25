(ns rolf.macros
  "Macros to define OpenLayers components."
  (:require [clojure.string :as str]))

(defmacro define-ol-constructor [name js-class]
  `(defn ~name
     ([] (~name {}))
     ([options#]
      (new (aget ~js-class "default") (rolf.core/->js options#)))))

(defmacro define-ol-constructors [& names-and-js-classes]
  `(do
     ~@(for [[name js-class] (partition 2 names-and-js-classes)]
         `(define-ol-constructor ~name ~js-class))))

(defmacro define-ol-fn [name args js-module fn-name & js-args]
  (let [func (gensym "FUNC")]
    `(let [~func (aget ~js-module ~fn-name)]
       (defn ~name ~args
         (~func ~@(if (seq js-args)
                    js-args
                    args))))))
