package grouchrest.util

import org.json.JSONArray
import org.json.JSONObject

import org.apache.commons.codec.net.URLCodec

class GrouchUtils {

    // plain dict => JSON
    static def toJSON(map) {
        def json = new JSONObject()
        map.each { k, v ->
            json.put(k, toJSON(v))
        }
    }
    
    static def paramifyURL(baseURL, params) {
        def url = baseURL
        def codec = new URLCodec()
        if (params && params.size() > 0) {
            def query = params.collect { k, v ->
                if (k.toString() in ["startkey", "key", "endkey"])
                    v = new JSONObject(v)
                "${k}=${codec.encode(v.toString())}"
            }.join("&")
            url = "${baseURL}?${query}"
        }
        println url
        url
    }
}

