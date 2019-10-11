(ns rolf.demo
  (:require [reagent.core :as r]
            [rolf.core :refer [Map TileLayer View source-osm]]))

(defn rolf-demo []
  (r/with-let [show-layer? (r/atom true)
               zoom (r/atom 4)
               center (r/atom (js/ol.proj.fromLonLat #js [47.31, 8.82]))
               source (source-osm)]
    [:div
     "this is ROLF = Reagent OpenLayers Functions/Framework"
     [:button {:on-click #(swap! show-layer? not)}
      (str "toggle OSM layer: " @show-layer?)]
     " Zoom: " @zoom " "
     [:button {:on-click #(swap! zoom inc)} "zoom in"]
     [:button {:on-click #(swap! zoom dec)} "zoom out"]

     [Map {:width "100vw" :height "600px"
           :controls []}
      [View {:zoom @zoom
             :center @center}]
      (when @show-layer?
        [TileLayer {:source source}])]]))

(defn ^:export main []
  (r/render [rolf-demo] (js/document.getElementById "demoapp")))
