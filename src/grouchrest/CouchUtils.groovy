package grouchrest

import org.apache.commons.codec.net.URLCodec

import org.json.JSONArray
import org.json.JSONObject
import org.codehaus.groovy.runtime.GStringImpl

// See couchrest.rb
class CouchUtils {

    private static def codec = new URLCodec()

    static def getJSONObject(o) {
        if (o instanceof Map) {
            def json = new JSONObject()
            o.each { key, value -> json.put(key, getJSONObject(value)) }
            return json
        } else if (o instanceof Boolean || o instanceof Number || o instanceof String || o instanceof GStringImpl ) {
            return o
        } else if (o instanceof List) {
            return getJSONArray(o)
        }

        return JSONObject.null
    }

    static def getMap(String jsonObject) {
        getMap(new JSONObject(jsonObject))
    }

    static def getList(String jsonArray) {
        getList(new JSONArray(jsonArray))
    }

    // see CouchRest
    static def paramifyURL(url, params = [:]) {
        if (!params)
            return url

        //println "params ${params}"

        def query = params.collect { k, v ->
            if (k == "id")
                return k
            else if (k in ["key", "startkey", "endkey"]) {
                // Hack: Strings literally need to be quoted
                // Hack: Integers cannot be URL encoded
                if (v instanceof String) {
                    def qv = "\"${v}\""
                    return "${k}=${codec.encode(qv)}"
                } else if (v instanceof Number || v instanceof Boolean) {
                    return "${k}=${v}"
                } else
                    return "${k}=${codec.encode(getJSONObject(v).toString())}"
            } else if (k in ["limit", "skip", "include_docs"]) {
                return "${k}=${v}"
            }
            else
                return "${k}=${codec.encode(v)}"
        }.join("&")

        "${url}?${query}"
    }

    ////
    //// Private methods
    ////

    private static def getJSONArray(o) {
        def array = new JSONArray()
        o.each {
            array.put(getJSONObject(it))
        }
        return array
    }    

    private static def getMap(JSONObject json) {
        def map = [:]

        json.keys().each { k ->
            def value = json.get(k)
            if (value instanceof JSONObject)
                map[k] = getMap(value)
            else if (value instanceof JSONArray)
                map[k] = getList(value)
            else
                map[k] = value
        }

        return map
    }    

    private static def getList(JSONArray json) {
        if (json.length() == 0)
            return []
        
        def list = []

        0.upto(json.length() - 1) {
            def element = json.get(it)

            if (it instanceof JSONObject)
                list << getMap(element)
            else if (it instanceof JSONArray)
                list << getList(element)
            else
                list << element
        }

        return list
    }    
	
}

