(ns logging.core
  (:gen-class)
  (:require [environ.core :refer [env]]
            [langohr.core      :as rmq]
            [langohr.channel   :as lch]
            [langohr.queue     :as lq]
            [langohr.consumers :as lc]
            [langohr.basic     :as lb]))

(def queue-name "flowerpatch.logging")
(def queue-expiration-time (* 1000 60 60 24)) ;; How long should the queue stick around if the consumer dies?

(defn message-handler
  [ch {:keys [content-type delivery-tag type] :as meta} ^bytes payload]
  (println (format "[consumer] Received a message: %s, delivery tag: %d, content type: %s, type: %s"
                   (String. payload "UTF-8") delivery-tag content-type type)))

(defn -main
  [& args]
  (println env)
  (let [conn  (rmq/connect {:host     (env :rabbitmq-host)
                            :username (env :rabbitmq-username)
                            :password (env :rabbitmq-password)
                            :port     (Integer/parseInt (env :rabbitmq-port))
                            :vhost    (env :rabbitmq-vhost)})
        ch    (lch/open conn)
        qname queue-name]

    (println (format "[main] Connected. Channel id: %d" (.getChannelNumber ch)))
    (println "queue-name=" queue-name)

    ;; We don't want to delete our queue if we go offline, that will result in message loss.
    ;; Setting exclusive to true will also result in message loss if we go offline.
    ;; Some people recommend setting an expiration time instead, this means if we do anything
    ;; via an upgrade the old data will eventually be cleaned up but we have a window to come back
    ;; online.
    (lq/declare ch qname {:exclusive false
                          :auto-delete false
                          :arguments {"x-expires" queue-expiration-time}})
    (lc/subscribe ch qname message-handler {:auto-ack true})


    ;;(rmq/close ch)
    ;;(rmq/close conn)
    ))
