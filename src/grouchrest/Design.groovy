package grouchrest

// A Design Document contains views, among other things.

// It has a special id (_design/<name>).

/**
 * A Design Document contains named views, among other things.
 *
 * 
 */
class Design extends Document {

    def Design() { }

    /**
     * Create a Design Document
     *
     * @param props (design attributes)
     *
     * See CouchDB Document API
     *
     * Assumes an _id property is present
     */
    def Design(Map props) {
        //println "*** Design: ${props}"

        props.each { k, v -> put(k, v) }

        put("language", "javascript")
    }    

    /**
     * Execute a named view (_view/name)
     *
     * @param vname (view name)
     * @param params
     * @param closure
     *
     * @return a Map (total_rows, offset, rows)
     */
    Map view(vname, params = [:], closure = null) {
        if (!database)
            throw new Exception("Requires a database.")        

        database.view("${getName()}/${vname}", params, closure)
    }    
    
    def getName() {
        if (id)
            id.replace('_design/', '')
    }

    def makeBasicView(attribute) {
        def mapFunction = {"""
          function(doc) {
            if (doc.${attribute})
              emit(doc.${attribute}, null);
          }"""
        }
                
        def views = get("views")
        views["by_${attribute}"] = ["map" : mapFunction()]
    }
	
}