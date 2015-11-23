(ns writer.core
  (:gen-class)
  (:require [environ.core :refer [env]]
            [langohr.core :as rmq]
            [langohr.channel :as lch]
            [langohr.queue :as lq]
            [langohr.consumers :as lc]
            [langohr.basic :as lb]))

(def ^{:const true}
  default-exchange-name "")
(def queue-name "flowerpatch.logging")
(def queue-expiration-time (* 1000 60 60 24))

(def connection-config {:host (env :rabbitmq-host)
                        :username (env :rabbitmq-username)
                        :password (env :rabbitmq-password)
                        :port (Integer/parseInt (env :rabbitmq-port))
                        :vhost (env :rabbitmq-vhost)})

;; (def connection-config {:host "172.17.0.2"
;;                         :username "guest"
;;                         :password "guest"
;;                         :port 5672
;;                         :vhost "/"})

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println env)
  (let [conn (rmq/connect connection-config)
        ch (lch/open conn)
        qname queue-name
        ]
    (Thread/sleep 10000)
    (println "Sending greetings!")
    (lb/publish ch default-exchange-name qname queue-name {:content-type "text/plain" :type "greetings.hi"})
    ))
