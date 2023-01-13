(ns meteo38.server
  (:require
   [babashka.fs :as fs]
   [org.httpkit.server :as srv]
   [ruuter.core :as ruuter]
   [meteo38.handlers :as h]
  ))


(set! *warn-on-reflection* true)


(defn instant->rfc1123 [^java.time.Instant instant]
  (->>  
    (.atOffset instant java.time.ZoneOffset/UTC)
    (.format java.time.format.DateTimeFormatter/RFC_1123_DATE_TIME)
    ))


(defn file-last-modified [file]
  (-> file
      (fs/last-modified-time)
      (fs/file-time->instant)
      (instant->rfc1123)
      ))

(comment

  (file-last-modified "./public/assets/style.css")
  ;; => "Fri, 13 Jan 2023 07:10:22 GMT"

  ,)


(defn static-file-route [path prefix content-type]
  {:path path 
   :method :get
   :response (fn [_]
                (let [file (fs/file (str prefix path))]
                  {:status 200
                   :headers {"Content-Type" content-type
                             "Last-Modified" (file-last-modified file)
                             }
                   :body file}
                  )
               )
   })


(def routes 
  [{:path     "/"
    :method   :get
    :response h/root-page
    }
   {:path     "/data"
    :method   :get
    :response h/data-block}
   ;
   (static-file-route "/assets/style.css" "./public" "text/css")
   
  ;;  {:path     "/todos"
  ;;   :method   :post
  ;;   :response add-item}
  ;;  {:path     "/todos/update/:id"
  ;;   :method   :patch
  ;;   :response update-item}
  ;;  {:path     "/todos/:id"
  ;;   :method   :patch
  ;;   :response patch-item}
  ;;  {:path     "/todos/:id"
  ;;   :method   :delete
  ;;   :response delete-item}
  ;;  {:path     "/todos"
  ;;   :method   :delete
  ;;   :response clear-completed}
   ])

(comment
  
  (ruuter/route routes {:uri "/assets/file.js" :request-method :get})
  
  ,)


(defn run [{:keys [host port] :as config}] 
  (println (format "listen at %s:%s" host port))
  ;; https://github.com/http-kit/http-kit/blob/master/src/org/httpkit/server.clj#L38
  (srv/run-server 
    #(ruuter/route routes (assoc % :config config))
    {:ip host :port port 
     :legacy-return-value? false
     :worker-name-prefix "http-kit-"
     }))
