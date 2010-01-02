package greenapple.couch

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

    ////
    //// Private methods
    ////

    private def createDatabase(name) {
        try { 
            HttpClient.put("${uri}/${name}")
        } catch (e) { }
        new Database(this, name)
    }

    private def getDatabase(name) {
        new Database(this, name)
    }    
	
}