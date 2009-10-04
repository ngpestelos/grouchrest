package grouchrest.core

import org.json.JSONObject

class Document {

    def properties

    def Document() {
        properties = [:]
    }

    def getId() {
        properties["_id"] ?: null
    }

    def toJSON() {
        new JSONObject(properties).toString()
    }

    def put(key, value) {
        properties[key] = value
    }

    def has(key) {
        (properties.keySet().find { it == key }) ? true : false
    }

    def get(key) {
        has(key) ? properties[key] : null
    }

    def remove(key) {
        if (has(key))
            properties.remove(key)
    }
	
}

