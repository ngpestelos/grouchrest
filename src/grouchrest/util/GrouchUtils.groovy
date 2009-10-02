package grouchrest.util

import org.json.JSONObject

class GrouchUtils {

    // TODO consider calling these functions recursively for each value

    // JSON => plain dict
    static def toMap(doc) {
        def json = new JSONObject(doc)
        def map = [:]
        json.keys().each { k ->
            map[k] = json.get(k)
        }
        map
    }

    // plain dict => JSON
    static def toJSON(map) {
        def json = new JSONObject()
        map.each { k, v ->
            json.put(k, v)
        }
        json
    }
	
}

