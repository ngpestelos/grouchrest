package grouchrest

class Server {

    def uri
    def availableDatabases

    static final String DEFAULT = "default"

    def Server(server = "http://127.0.0.1:5984") {
        this.uri = server
        def dict = [:]
        dict.getMetaClass().hasKey = { k ->
            k in delegate.keySet()
        }
        this.availableDatabases = dict
    }

    def defineAvailableDatabase(key, name, createUnlessExists = true) {
        availableDatabases[key] = createUnlessExists ? createDatabase(name) : getDatabase(name)
    }

    def getURI() {
        uri
    }

    def availableDatabase(key) {
        availableDatabases.hasKey(key)
    }

    def setDefaultDatabase(name) {
        defineAvailableDatabase(DEFAULT, name)
    }

    def getDefaultDatabase() {
        availableDatabases[DEFAULT]
    }

    def createDatabase(name) {        
        //println "createDatabase ${uri}/${name}"
        HttpClient.put("${uri}/${name}")
        new Database(this, name)
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