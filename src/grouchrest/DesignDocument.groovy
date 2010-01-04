package grouchrest

class DesignDocument {    

    protected def attributes
    protected def database

    // Create a design document on a database
    protected def DesignDocument(database, String name) {
        this.database = database
        attributes = [:]
        attributes.put("_id", "_design/${name}")        
    }

    protected def put(key, value) {
        attributes.put(key, value)
    }

    protected def get(key) {
        attributes.get(key)
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