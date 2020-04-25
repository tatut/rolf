(ns rolf.core
  "React OpenLayers Function components."
  (:require [reagent.core :as r]
            ["ol/Map" :as ol-Map]
            ["ol/View" :as ol-View]))

(defn ->js [x]
  (cond
    (map? x)
    (reduce (fn [acc [key val]]
              (aset acc
                    (if (keyword? key)
                      (name key)
                      key)
                    (->js val))
              acc)
            #js {}
            x)

    (vector? x)
    (reduce (fn [acc val]
              (.push acc val)
              acc)
            #js []
            x)

    :else
    x))

(defn ol-class? [obj]
  (and (object? obj)
       (some? (aget obj "default"))))

(defn- create-map-instance [options elt]
  (js/console.log "INIT MAP INSTANCE")
  (new (aget ol-Map "default")
       (->js (assoc options
                    :target elt))))

(defn children-with-keys [children]
  (doall
   (map-indexed
    (fn [i child]
      (if (contains? (meta child) :key)
        child
        (with-meta child {:key i})))
    children)))

(defn Map
  "Main OpenLayers map component.

  Options:

  :width    CSS width of the map component
  :height   CSS height of the map component"
  [options & children]
  (r/with-let [instance (r/atom nil)
               init! (fn [elt]
                       (swap! instance
                              #(or % (create-map-instance options elt))))]
    [:div {:key "rolf-Map"
           :style #js {:width (or (:width options) "100vw")
                       :height (or (:height options) "100vh")}
           :data-ol-class "ol.Map"
           :ref init!}
     (when-let [instance @instance]
       (children-with-keys (map
                            #(update % 1 assoc :rolf/map instance)
                            children)))]))

(defn ->component
  "OpenLayers class to Reagent component"
  [ol-class {:keys [init cleanup]}]
  (assert (ol-class? ol-class) "Expected an OpenLayers class")
  (let [cls (aget ol-class "default")
        class-name (aget cls "name")]
    (fn [opts & children]
      (r/with-let [instance (new cls (->js (dissoc opts :rolf/map)))
                   _ (when init
                       (init instance (:rolf/map opts)))
                   prev-opts (atom opts)]
        (doseq [[key new-val] opts
                :let [old-val (get @prev-opts key)]
                :when (not= old-val new-val)]
          (let [prop-name (name key)
                ;; PENDING: this setter name determination is very simplistic
                setter-name (str "set"
                                 (.toUpperCase (.substring prop-name 0 1))
                                 (.substring prop-name 1))
                setter (aget instance setter-name)]
            (if setter
              ((.bind setter instance) new-val)
              (js/console.error (str "Invalid property for class " class-name
                                     ", no setter: " setter-name)))))
        (reset! prev-opts opts)

        [:span {:data-ol-component class-name}
         (children-with-keys children)]

        (finally
          (when cleanup
            (cleanup instance (:rolf/map opts))))))))

(defn ->constructor
  "OpenLayers class to constructor function"
  [ol-class]
  (assert (ol-class? ol-class) "Expected an OpenLayers class")
  (let [cls (aget ol-class "default")]
    (fn [options]
      (new cls (->js options)))))


(def View
  (->component
   ol-View
   {:init (fn [this m]
            (.setView m this))}))


(def layer-lifecycle {:init (fn [this m]
                              (.addLayer m this))
                      :cleanup (fn [this m]
                                 (.removeLayer m this))})
