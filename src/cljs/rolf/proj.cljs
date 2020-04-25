(ns rolf.proj
  "Projection utils"
  (:require ["ol/proj" :as ol-proj])
  (:require-macros [rolf.macros :refer [define-ol-fn]]))

(define-ol-fn from-lon-lat [lon lat]
  ol-proj "fromLonLat"
  #js [lon lat])
