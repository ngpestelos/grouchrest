package greenapple.couch

class DesignDocument {    

    def attributes
    def database

    // Create a design document on a database
    protected def DesignDocument(database, String name) {
        this.database = database
        attributes = [:]
        attributes.put("_id", "_design/${name}")        
    }

    protected def push() {
        def _id = attributes.get("_id")        
        try {
            def doc = database.get(_id)
            attributes.put("_rev", doc["_rev"])
            return database.save(attributes)
        } catch (e) {            
            return database.save(attributes)
        }        
    }
	
}

