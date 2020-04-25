(ns rolf.demo
  (:require [reagent.core :as r]
            [reagent.dom :as dom]
            [rolf.core :refer [Map View]]
            [rolf.tile :refer [TileLayer]]
            [rolf.osm :refer [osm-source]]
            [rolf.proj :as proj]))

(defn rolf-demo []
  (r/with-let [show-layer? (r/atom false)
               zoom (r/atom 4)
               center (r/atom (proj/from-lon-lat 8.82  47.31))
               source (osm-source)]
    (js/console.log "RENDER ROLF DEMO")
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
  (dom/render [rolf-demo] (js/document.getElementById "app")))
