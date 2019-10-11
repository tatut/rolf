(ns rolf.macros
  "Macros to define OpenLayers components."
  (:require [clojure.string :as str]))

(defmacro define-ol-component [name js-class init cleanup]
  `(defn ~name [options#]
     (let [instance# (atom nil)]
       (reagent.core/create-class
        {:component-did-mount
         (fn [_#]
           (let [~'this (~(symbol "js" (str js-class "."))
                         (rolf.core/->js (dissoc options# :rolf/map)))
                 ~'m (:rolf/map options#)]
             (reset! instance# ~'this)
             ~init))
         :component-did-update
         (fn [this# prev-props#]
           (let [old-props# (nth prev-props# 1)
                 new-props# (reagent.core/props this#)]

             (doseq [[prop# new-val#] new-props#
                     :let [old-val# (get old-props# prop#)]
                     :when (not= old-val# new-val#)]
               (let [prop-name# (name prop#)

                     ;; PENDING: this setter name determination is very simplistic
                     setter# (str "set"
                                  (.toUpperCase (.substring prop-name# 0 1))
                                  (.substring prop-name# 1))]

                 ;; get setter function, bind it to instance and call it with new value
                 ((.bind (aget @instance# setter#) @instance#) new-val#)))))

         :component-will-unmount
         (fn [_#]
           (let [~'this @instance#
                 ~'m (:rolf/map options#)]
             ~cleanup))

         :reagent-render
         (fn [options#]
           [:span {:data-ol-component ~js-class}])}))))

(defmacro define-ol-constructor [name js-class]
  `(defn ~name
     ([] (~name {}))
     ([options#]
      (~(symbol "js" (str js-class ".")) (rolf.core/->js options#)))))

(defmacro define-ol-constructors [& names-and-js-classes]
  `(do
     ~@(for [[name js-class] (partition 2 names-and-js-classes)]
         `(define-ol-constructor ~name ~js-class))))
