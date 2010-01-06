package grouchrest

// has a default database and a design document
class ExtendedDocument extends Document {    

    protected def design

    protected ExtendedDocument(DB) {
        def database = new Server().getDatabase(DB)
        useDatabase(database)
        design = new Design(database.get("_design/${DB}"))
        design.name = DB
    }    

    // Copied from "Dynamic finders in Grails"
    protected static def setupDynamicFinders(Class clazz) {
        // TODO Cache (but where to put it?)
        def mc = clazz.metaClass

        if (!mc.hasMetaProperty("DB"))
            throw new IllegalStateException("Could not find static property 'DB'.")

        def db = new Server().getDatabase(clazz."DB")
        def design = new Design()
        design.database = db
        design.name = clazz."DB"

        mc.'static'.methodMissing = { String name, args ->
            def m = name =~ /by(\w+)/

            def property = m[0][1].toLowerCase()
            println property
            println args
            //design.view(property)

            /*println m[0].size()
            println "${name}"
            println "${m[0][1]}"
            println clazz."DB"
            println mc.hasMetaProperty("DB")*/
            //method = ?

            /*
            mc.'static'."${name}" = { List varArgs ->
                method.invoke(clazz, methodName, varArgs)
            }

            result = method.invoke(clazz, methodName, varArgs)*/
        }
    }
	
}

