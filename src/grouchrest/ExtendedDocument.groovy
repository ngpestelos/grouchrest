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

    /*
    */

    /*
    private static def getDesign(Class clazz) {
        if (!clazz.metaClass.hasMetaProperty("DB"))
            throw new IllegalStateException("Could not find static property 'DB'.")

        def database = new Server().getDatabase(clazz."DB")        
        def des = new Design(database.get("_design/${clazz.'DB'}"))
        des.name = clazz."DB"
        des.database = database

        return des
    }*/

    // A database has one design document
    /*
    private static def makeDesignDocument(Class clazz) {
        def des

        try {
            des = getDesign(clazz)
        } catch (e) {
            des = new Design()
            des.name = clazz."DB"
            des.database = new Server().getDatabase(clazz."DB")
            des.save()
        }

        return des
    }*/

    // Intercept class methods
    /*
    private static def setupClassIntercept(Class clazz, design) {
        //println "setupClassIntercept ${clazz} ${design}"

        def mc = clazz.metaClass
        mc.'static'.methodMissing = { String name, args ->
            def match = name =~ /by(\w+)/
            if (match.size() > 0)
                return byProperty(match[0][1], name, args, clazz, design)

            match = name =~ /get/
            if (match.size() > 0)
                return get(name, args, clazz, design)

            match = name =~ /count/
            if (match.size() > 0)
                return countDocuments(clazz)

            if (match.size() == 0)
                throw new MissingMethodException(name, clazz, args)
        }
    }*/

    /*
    private static byProperty(p, name, args, clazz, design) {
        def property = p.toLowerCase()
        if (!design.get("views")["by_${property}"])
            throw new MissingMethodException(name, clazz, args)

        if (args.size() == 0)
            design.view("by_${property}")
        else if (args.size() == 1 && args[0] instanceof Map)
            design.view("by_${property}", args[0])
        else
            throw new MissingMethodException(name, clazz, args)
    }*/

    /*
    private static get(name, args, clazz, design) {
        //println "get ${args} ${design}"
        if (args.size() == 0)
            throw new MissingMethodException(name, clazz, args)

        def doc = design.database.get(args[0])
        //println doc
        clazz.newInstance(doc)
    }*/

    /*
    private static countDocuments(clazz) {
        //println "count ${clazz}"
        def db = new Server().getDatabase(clazz."DB")
        def all = db.getDocuments()
        def filter = all["rows"].findAll { row -> !(row["id"].startsWith("_design/")) }
        filter.size()
    }*/

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

