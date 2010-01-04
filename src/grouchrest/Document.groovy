package grouchrest

class Document extends PropertyList {

    static def database

    static def useDatabase(db) {
        database = db
    }

    def getId() {
        get("_id")
    }

    def getRev() {
        get("_rev")
    }

    def Document(Map params = null) {
        params?.each {k, v -> put(k, v) }
    }

    def save() {
        if (!database)
            throw new IllegalStateException("database required for saving")

        database.save(getAttributes())["ok"]
    }
	
}

