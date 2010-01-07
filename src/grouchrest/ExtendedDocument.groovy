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

    // Expects a "DB" static property from the subclass
    protected ExtendedDocument(Class clazz, Map params = null) {
        //println "In super: ${clazz.getName()}"
        super(params)

        if (!clazz.metaClass.hasMetaProperty("DB"))
            throw new IllegalStateException("Could not find static property 'DB'.")         

        useDatabase(new Server().getDatabase(clazz."DB"))        

        design = makeDesignDocument(clazz)

        setupClassIntercept(clazz, design)
    }

    protected static def viewBy(Class clazz, plist) {
        //println "viewBy ${clazz} ${plist}"

        if (!clazz.metaClass.hasMetaProperty("DB"))
            throw new IllegalStateException("Could not find static property 'DB'.")

        def des = makeDesignDocument(clazz)        

        if (des.has("views") && des.get("views")["by_${plist}"])
            return
        
        des.viewBy(plist)
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

    private static def getDesign(Class clazz) {
        if (!clazz.metaClass.hasMetaProperty("DB"))
            throw new IllegalStateException("Could not find static property 'DB'.")

        def database = new Server().getDatabase(clazz."DB")        
        def des = new Design(database.get("_design/${clazz.'DB'}"))
        des.name = clazz."DB"
        des.database = database

        return des
    }

    // A database has one design document
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
    }

    // Intercept class methods
    private static def setupClassIntercept(Class clazz, design) {
        def mc = clazz.metaClass
        mc.'static'.methodMissing = { String name, args ->
            def match = name =~ /by(\w+)/
            if (match.size() > 0)
                return byProperty(match[0][1], name, args, clazz, design)

            match = name =~ /get/
            if (match.size() > 0)
                return get(name, args, clazz, design)

            if (match.size() == 0)
                throw new MissingMethodException(name, clazz, args)
        }
    }

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
    }

    private static get(name, args, clazz, design) {
        //println "get ${args} ${design}"
        if (args.size() == 0)
            throw new MissingMethodException(name, clazz, args)

        def doc = design.database.get(args[0])
        //println doc
        clazz.newInstance(doc)
    }    
	
}

