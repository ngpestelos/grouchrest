package grouchrest

class ExtendedDocument extends Document {

    // is a document plus a default design doc
    protected ExtendedDocument(Class clazz) {
        setupDynamicFinders(clazz)
    }

    // Copied from "Dynamic finders in Grails"
    private def setupDynamicFinders(Class clazz) {
        // TODO Cache (but where to put it?)
        def mc = clazz.metaClass

        mc.'static'.methodMissing = { String name, args ->
            println "${name}"
            println "${database.name}"
            //method = ?

            /*
            mc.'static'."${name}" = { List varArgs ->
                method.invoke(clazz, methodName, varArgs)
            }

            result = method.invoke(clazz, methodName, varArgs)*/
        }
    }
	
}

