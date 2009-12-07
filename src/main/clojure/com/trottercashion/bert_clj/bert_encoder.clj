(ns com.trottercashion.bert-clj.bert-encoder)

(def *type-mappings*
  { nil               :nil
    java.lang.Boolean :boolean})

(def *type-finders*
  { clojure.lang.IPersistentMap :dictionary
    java.util.Date              :time})

(defn encoding-type [obj]
  (or (*type-mappings* (type obj))
      (last (first (filter #(instance? (first %) obj) *type-finders*)))))

(defmulti encode #(encoding-type %))

(defmethod encode :nil [_]
  ['bert 'nil])

(defmethod encode :boolean [bool]
  (let [sym (if bool 'true 'false)]
    ['bert sym]))

(defmethod encode :dictionary [dict]
  (vector 'bert 'dict (map vec dict)))

(defmethod encode :time [time]
  (let [milliseconds (.getTime time)
        seconds      (quot milliseconds 1000)
        megaseconds  (quot seconds 1000000)
        microseconds (* (rem milliseconds 1000) 1000)]
    (vector 'bert 'dict megaseconds (rem seconds 1000000) microseconds)))

(defmethod encode :default [obj] obj)