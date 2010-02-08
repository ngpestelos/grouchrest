package grouchrest

import org.apache.commons.codec.net.URLCodec
import org.codehaus.groovy.runtime.GStringImpl

import grouchrest.json.*

// See couchrest.rb
class CouchUtils {

    private static def codec = new URLCodec()    
    
    static def getMap(String jsonObject) {
        getMap(new JSONObject(jsonObject))
    }

    static def getMap(JSONObject jsonObject) {
        def map = [:]

        jsonObject.getInternalMap().each { k, v ->
            if (v instanceof JSONObject)
                map[k] = getMap(v)
            else if (v instanceof JSONArray)
                map[k] = getList(v)
            else
                map[k] = v
        }

        return map
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
            else if (k in ["limit", "skip", "include_docs"])
                return "${k}=${v}"
            else {
                // Hack: Strings literally need to be quoted
                // Hack: Integers cannot be URL encoded
                if (v instanceof String) {
                    def qv = "\"${v}\""
                    return "${k}=${codec.encode(qv)}"
                } else if (v instanceof Number || v instanceof Boolean) {
                    return "${k}=${v}"
                } else
                    return "${k}=${codec.encode(getJSONObject(v).toString())}"
            }
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

    private static def getList(JSONArray json) {
        if (json.length() == 0)
            return []
        
        def list = []

        0.upto(json.length() - 1) {
            def element = json.getInternalList()[it]

            //println "processing ${element}"
            
            if (element instanceof JSONObject)
                list << getMap(element)
            else if (element instanceof JSONArray)
                list << getList(element)
            else
                list << element

        }

        return list
    }    
	
}

