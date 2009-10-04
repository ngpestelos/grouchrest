package grouchrest.core

import grouchrest.util.GrouchUtils
import grouchrest.util.Http

import org.apache.commons.codec.net.URLCodec
import org.json.JSONObject

class Database {

    def server
    def host
    def name
    def root
    def uri

    def codec

    def Database(server, name) {
        this.server = server
        this.name = name
        host = server.getHostname()
        root = "${host}/${name.replaceAll('/','%2F')}"
        uri = root
        codec = new URLCodec()
    }

    String toString() {
        return uri
    }

    def info() {
        Http.get(uri)
    }

    def documents() {
        Http.get("${uri}/_all_docs")
    }

    def saveDocument(doc) {
        def slug = doc.getId() ?: server.getUUID()
        def payload = doc.toJSON()

        Http.put("${uri}/${codec.encode(slug)}", payload)
    }

    // dict => ["map" : "function...", "reduce" : "function..."]
    def slowView(dict, params = [:]) {
        // merge "keys" with dict
        // TODO pop an entry from the dictionary
        // TODO merge a kv pair with another dictionary

        def url = GrouchUtils.paramifyURL("${uri}/_temp_view", params)
        def doc = new JSONObject(dict)
        println doc
        Http.post(url, doc.toString())
    }

    static void main(args) {
        def server = new Server()
        def db = new Database(server, "nblog_development")
        def funcs = ["map" : "function(doc) { emit(doc.title, null); }"]
        println db.slowView(funcs)
    }
	
}

