package grouchrest.core

import grouchrest.util.Http

class Server {

    def hostname

    def Server(hostname = "http://127.0.0.1:5984") {
        this.hostname = hostname
    }

    def getInfo() {
        Http.get(hostname)
    }

    // returns a single uuid
    def getUUID() {
        def res = Http.get("${hostname}/_uuids")
        res["uuids"].get(0)
    }

    def createDatabase(name) {
        try {
            Http.put("${hostname}/${name}")
            return new Database(this, name)
        } catch (e) {
            e.printStackTrace()
            throw new Exception("Error while creating database ${name}")
        }
    }

    def deleteDatabase(name) {
        try {
            return Http.delete("${hostname}/${name}")
        } catch (e) {
            throw new Exception("Error while deleting database ${name}")
        }
    }
}