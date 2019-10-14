(ns rolf.core
  "Reagent OpenLayers Function components."
  (:require [reagent.core :as r])
  (:require-macros [rolf.macros :refer [define-ol-component define-ol-constructors]]))

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

(defn Map
  "Main OpenLayers map component.

  Options:

  :width    CSS width of the map component
  :height   CSS height of the map component"
  [options & _]
  (let [instance (r/atom nil)
        init! (fn [elt]
                (reset! instance (js/ol.Map. (->js (assoc options
                                                          :target elt)))))]
    (fn [options & children]
      [:<>
       [:div {:style {:width (or (:width options) "100vw")
                      :height (or (:height options) "100vh")}
              :data-ol-class "ol.Map" :ref init!}]
       (when-let [instance @instance]
         (doall
          (map-indexed
           (fn [i child]
             ;; Child is a vector child component like
             ;; [somecomponent {:options "here"}]
             (update child 1
                     (fn [child-opts]
                       (-> child-opts
                           (update :key #(or % i))
                           (assoc :rolf/map instance
                                  :rolf/index i)))))
           children)))])))

(define-ol-constructors
  format-geojson "ol.format.GeoJSON"
  format-mvt "ol.format.MVT"
  source-osm "ol.source.OSM"
  source-vector-tile "ol.source.VectorTile"
  source-vector "ol.source.Vector"
  tilegrid-wmts "ol.tilegrid.WMTS"
  source-wmts "ol.source.WMTS")

(define-ol-component View "ol.View"
  (.setView m this)
  nil)

(define-ol-component TileLayer "ol.layer.Tile"
  (do
    (js/console.log "init tile layer!")
    (.addLayer m this))

  (do
    (js/console.log "removing tile layer!")
    (.removeLayer m this)))

(define-ol-component VectorLayer "ol.layer.Vector"
  (.addLayer m this)
  (.removeLayer m this))

(define-ol-component VectorTileLayer "ol.layer.VectorTile"
  (.addLayer m this)
  (.removeLayer m this))
