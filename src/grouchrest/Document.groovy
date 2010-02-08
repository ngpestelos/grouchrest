package grouchrest

class Document extends PropertyList {    

    def database

    def useDatabase(db) {
        database = db
    }

    def getId() {
        get("_id")
    }

    def getRev() {
        get("_rev")
    }

    def Document(Map attributes = null) {
        //println "new Document ${params}"
        attributes?.each {k, v -> put(k, v) }
    }

    def save() {
        //println getAttributes()

        if (!database)
            throw new IllegalStateException("database required for saving")

        def res = database.save(getAttributes())
        if (res["ok"]) {
            put("_id", res["id"])
            put("_rev", res["rev"])
            return res["ok"]
        }
    }

    // Removes the "_id" and "_rev fields
    def destroy() {
        if (!has("_id"))
            throw new IllegalStateException("Could not find _id. The document appears to be new.")

        if (!database)
            throw new IllegalStateException("database required for saving")

        // TODO: Funky syntax. I should learn how to overload operators (c.getFoo() => c["foo"])
        def res = database.deleteDoc(getAttributes())
        if (res["ok"]) {
            remove("_id")
            remove("_rev")
        }

        res["ok"]
    }
	
}