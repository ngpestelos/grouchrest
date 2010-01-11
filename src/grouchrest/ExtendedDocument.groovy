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
        //println "In super: ${clazz.getName()}"
        super(attributes)
        useDatabase(new Server().getDatabase(dbname))
        design = makeDesignDocument()        
    }

    protected static def makeView(ExtendedDocument doc, attribute, Boolean saveNow = true) {
        def des = doc.design

        if (des.has("views") && des.get("views")["by_${attribute}"])
            return

        des.viewBy(attribute)
        
        if (saveNow)
            des.save()
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

    static def findBy(ExtendedDocument doc, String bySomething, Map params, Boolean isDocIncluded = true) {
        if (isDocIncluded && !params["include_docs"])
            params["include_docs"] = true

        def res = doc.design.view(bySomething, params)
        def rows = res["rows"]
        if (rows.size() == 0)
            return null

        def clazz = doc.getClass()
        println clazz
        def list = rows.collect { clazz.newInstance(it["doc"]) }
        if (list.size() == 1)
            list.first()
        else
            return list    
    }

    ////
    //// Private methods
    ////

    private def makeDesignDocument() {
        def des
        
        try {
            des = new Design(database.get("_design/${database.name}"))
            des.name = database.name
            des.database = database
        } catch (e) {
            des = new Design()
            des.name = database.name
            des.database = new Server().getDatabase(des.name)
            des.save()
        }

        return des
    }
}

