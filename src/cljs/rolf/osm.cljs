(ns rolf.osm
  "OpenStreetMap source"
  (:require [rolf.core :refer [->constructor]]
            ["ol/source/OSM" :as ol-source-OSM]))

(def osm-source (->constructor ol-source-OSM))
