(ns hangtime.server.model
  (:require [clojure.data.json :as json]))

(defprotocol Base (structure [this]))
(defprotocol Model (save [this]))
(defprotocol Field (v [this]))

(defn ->json [b]
  (json/write-str (.structure b)))

(defrecord ObjectIdField [oid]
  Field (v [this] (.oid this))
  Base (structure [this] (.v this)))
(def hex-chars "0123456789abcdef")
(def mongo-objectid-length 24)
(def null-objectid (apply str (repeat mongo-objectid-length "0")))
(defn is-hex [c] (>= (.indexOf hex-chars (int c)) 0))
(defn mongo-objectid-valid? [oid]
  (and (string? oid) (every? is-hex oid)
    (= (.length oid) mongo-objectid-length)))
(defn ->ObjectId [v]
  (cond
    (nil? v) v
    (mongo-objectid-valid? v) (ObjectIdField. v)
    :else (throw (Exception. "Invalid ObjectId"))))

(defrecord StringField [s]
  Field (v [this] (.s this))
  Base (structure [this] (.v this)))
(defn ->String [v]
  (cond
    (nil? v) v
    (string? v) (StringField. v)
    :else (throw (Exception. "Expected string"))))

(defrecord IntField [i]
  Field (v [this] (.i this))
  Base (structure [this] (.v this)))

(defn Model->structure [m ctors]
  (into {} (for [[field-name ctor] ctors]
    (let [kw (keyword field-name)]
      [kw
       (.structure (ctor (kw m)))]))))

(defmacro defmodel [name & specs]
  (let [fields-map (apply hash-map (flatten specs))
        field-ctors
        (into {} (for [[field-name ctor-sym] fields-map]
                   [(keyword field-name)
                    (symbol (str (.toString *ns*) "/" ctor-sym))
                    ]))
        field-names (map symbol (keys fields-map))]
    `(do
       (defrecord ~(symbol name) [~@field-names]
         Base (structure [this#] (Model->structure this# ~field-ctors)))
         )))

(defmodel Event
  [id ->ObjectId]
  [name ->String])

(def e (Event. null-objectid "hey"))

; fields-map should be a map from symbols (the field names) to
; constructor functions (like ->String).
;(defn Model->json [m fields-map]
;  (json/write-str
;    (into {} (for [[k v] fields-map]
;               [k (json-convert v)]))))

;(defmacro defmodel [name & specs]
;  (let [[fields-map (zipmap specs)]
;        [field-names (map symbol (keys fields-map))]
;        [record-name (symbol name)]]
;    `(defprotocol ~record-name [~@field-names])
;    )
;
;
;
;(defmodel Event
;  [id ->ObjectId]
;  [name ->String])
;
;(Event-find {:id 2})
;(.save event)

;(Event/<-json)
