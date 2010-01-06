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

    def Document(Map params = null) {
        params?.each {k, v -> put(k, v) }
    }

    def save() {
        if (!database)
            throw new IllegalStateException("database required for saving")

        def res = database.save(getAttributes())
        if (res["ok"]) {
            put("_id", res["id"])
            put("_rev", res["rev"])
            return res["ok"]
        }
    }
	
}

