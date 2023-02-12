(ns meteo38.html
  (:require
   [hiccup2.core :refer [html raw]]
   [meteo38.config :as config]
   ))


(defn page-body [{uri :uri params :params} & content]
  (str (raw "<!DOCTYPE html>\n")
       (html
        [:html
         [:head
          [:meta {:charset "utf-8"}]
          [:title "Meteo38"] 
          "\n"
          (when (and (= "/" uri) (not (:st_list params)))
            [:script (raw (slurp (str config/ASSETS_DIR "local_redir.js")))])
          "\n"
          [:meta {:name "viewport" :content "width=device-width,initial-scale=1"}]
          [:meta {:name "description" :content "Погода в Иркутске и области в реальном времени"}]
          [:link {:rel "shortcut icon" :href (str config/ASSETS_URI "favicon.ico")}]
          [:link {:rel "stylesheet" :href (str config/ASSETS_URI "style.css")}]]
         "\n"
         [:body
          content]]
        )))


(defn layout [page-body]
  (html
   [:div.bg-indigo-100
    [:div.container.mx-auto.px-4.flex
     [:a.m-1 {:href config/METEO38_URL}
      [:img {:src (str config/ASSETS_URI "meteo38_240x70.png") 
             :width 240 :height 70
             :alt "meteo38 logo"}]]
     "header"
     ]
    ]
    [:div.container.mx-auto.px-4
     page-body
     ]
   [:div.bg-indigo-100
    [:div.container.mx-auto.px-4.py-1
     "footer"
     ]]
   ))
