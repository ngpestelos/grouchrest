package grouchrest

// has a default database and a design document
class ExtendedDocument extends Document {    

    protected def design

    // Expects a "DB" static property from the subclass
    protected ExtendedDocument(Class clazz) {
        //println "In super: ${clazz.getName()}"

        if (!clazz.metaClass.hasMetaProperty("DB"))
            throw new IllegalStateException("Could not find static property 'DB'.")        
        
        def database = new Server().getDatabase(clazz."DB")
        useDatabase(database)
        design = new Design(database.get("_design/${DB}"))
        design.name = DB
        design.database = database

        setupFinders(clazz, design)
    }

    private static def setupFinders(Class clazz, design) {
        //println "Attach class methods to ${clazz.getName()}"

        def mc = clazz.metaClass
        mc.'static'.methodMissing = { String name, args ->
            def m = name =~ /by(\w+)/

            if (m.size() == 0)
                throw new MissingMethodException(name, clazz, args)

            def property = m[0][1].toLowerCase()
            if (!design.get("views")["by_${property}"])
                throw new MissingMethodException(name, clazz, args)

            if (args.size() == 0)
                design.view("by_${property}")
            else if (args.size() == 1 && args[0] instanceof Map)
                design.view("by_${property}", args[0])
            else
                throw new MissingMethodException(name, clazz, args)
        }        
    }    
	
}

