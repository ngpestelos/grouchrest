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

    def createDatabase(dbname) {
        try {
            Http.put("${hostname}/${dbname}")
        } catch (e) {
            println "oops"
        }
        new Database(this, dbname)
    }

    def deleteDatabase(dbname) {
        Http.delete("${hostname}/${dbname}")
    }

    // returns ["uuids" : [...]]
    def getUUID(count = 1) {
        Http.get("${hostname}/_uuids?count=${count}")
    }

    def replicate(dbname, toServer) {
        throw new UnsupportedOperationException("build me")
    }

    static void main(args) {
        def s = new Server()
        println s.getInfo()
        s.createDatabase("blech")
        s.deleteDatabase("blech")
        println s.getUUID()
    }
	
}