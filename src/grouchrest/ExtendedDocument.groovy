package grouchrest

// has a default database and a design document
class ExtendedDocument extends Document {

    protected def design

    private def callbacks = [
        "beforeDestroy",
        "afterDestroy",
        "beforeSave",
        "afterSave"
    ]

    protected ExtendedDocument(String dbname, Map attributes = null) {
        super(attributes)
        def db = new Server().getDatabase(dbname)
        useDatabase(db)
        design = makeDesignDocument(db)
    }

    protected static def makeDesignDocument(db) {
        def des

        try {
            des = new Design(db.get("_design/${db.name}"))            
            des.name = db.name
            des.database = db
        } catch (e) {            
            des = new Design()
            des.name = db.name
            des.database = db            
            des.save()
        }

        return des
    }

    // standard views
    static def makeView(String dbname, List attributes) {
        def db = new Server().getDatabase(dbname)        
        
        try {
            def newDesign = new Design(db.get("_design/${dbname}"))
            newDesign.remove("views")
            attributes.each { newDesign.viewBy(it) }
            def aViews = newDesign.get("views")

            def baseline = new Design(db.get("_design/${dbname}"))
            def bViews = baseline.get("views")

            if (aViews.keySet() == bViews.keySet()) {
                //println "equal set"
                return
            }

            // TODO Something's wrong with Design.save
            db.save(newDesign.attributes)
            
        } catch (e) {
            println "Creating new design document: ${dbname}"
            def des = new Design()
            des.name = dbname
            des.database = db
            attributes.each { des.viewBy(it) }
            des.save()
        }        
    }    

    def destroy() {
        beforeDestroy()
        super.destroy()
        afterDestroy()
    }

    def save() {
        beforeSave()
        super.save()
        afterSave()
    }

    def methodMissing(String name, args) {
        if (callbacks.find { it == name })
            return
        else
            throw new MissingMethodException(name, ExtendedDocument.class, args)
    }

    /**
     * Queries the view for matching documents
     *
     * @return null if nothing found
     * @return an instance of ExtendedDocument
     * @return a List of ExtendedDocuments
     */
    static def findBy(ExtendedDocument doc, String bySomething, Map params = [:], Boolean isDocIncluded = true) {
        if (isDocIncluded && !params["include_docs"])
            params["include_docs"] = true

        def res = doc.design.view(bySomething, params)
        def rows = res["rows"]
        if (rows.size() == 0)
            return null

        def clazz = doc.getClass()
        //println clazz
        def list = rows.collect { clazz.newInstance(it["doc"]) }
        if (list.size() == 1)
            list.first()
        else
            return list    
    }    
}

