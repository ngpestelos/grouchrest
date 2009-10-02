package grouchrest.core

import grouchrest.util.GrouchUtils
import grouchrest.util.Http

import org.apache.commons.codec.net.URLCodec

class Database {

    def server
    def name

    def Database(server, name) {
        this.server = server
        this.name = name
    }

    // Map => Document
    def createDocument(map) {
        def json = GrouchUtils.toJSON(map)
        def uuid = server.getUUID()["uuids"].get(0)
        def doc = new URLCodec().encode(json.toString())
        def url = "${server.hostname}/${name}/${uuid}/${doc}"
        println url
        Http.put(url)
    }

    static void main(args) {
        def s = new Server()
        def db = s.createDatabase("blech")
        db.createDocument(["ugly" : "face"])
    }
	
}

