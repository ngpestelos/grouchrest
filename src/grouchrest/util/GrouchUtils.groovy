package grouchrest.util

import org.json.JSONArray
import org.json.JSONObject

class GrouchUtils {

    // plain dict => JSON
    static def toJSON(map) {
        if (!(map instanceof Map))
            return map

        def json = new JSONObject()
        map.each { k, v ->
            json.put(k, toJSON(v))
        }
    }
	
}

