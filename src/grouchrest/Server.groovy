package grouchrest

class Server {

    def uri    

    def Server(server = "http://127.0.0.1:5984") {
        this.uri = server        
    }    

    def getURI() {
        uri
    }    

    /**
     * Sends a PUT request to CouchDB to create a database
     *
     * @param name - database
     * @return Database representing the database
     */
    def createDatabase(name) {
        HttpClient.put("${uri}/${name}")
        new Database(this, name)
    }

    /**
     * The inverse of createDatabase
     *
     * @param name - database
     */
    def deleteDatabase(name) {
        HttpClient.delete("${uri}/${name}")
    }

    def getInfo() {
        HttpClient.get("${uri}")        
    }

    def allDatabases() {
        def res = HttpClient.get("${uri}/_all_dbs")
        CouchUtils.getList(res)
    }

    ////
    //// Private methods
    ////    

    private def getDatabase(name) {
        new Database(this, name)
    }    
	
}