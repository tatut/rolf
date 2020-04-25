(ns rolf.tile
  "Tile layers and sources"
  (:require [rolf.core :refer [->component ->constructor layer-lifecycle]]
            ["ol/layer/Tile" :as ol-layer-Tile]
            ["ol/source/VectorTile" :as ol-source-VectorTile]))

(def TileLayer (->component ol-layer-Tile layer-lifecycle))

(def vector-tile-source (->constructor ol-source-VectorTile))
