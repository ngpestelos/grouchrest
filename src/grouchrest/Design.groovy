package grouchrest

// A Design Document contains views, among other things.

// It has a special id (_design/<name>).

/**
 * A Design Document contains named views, among other things.
 *
 * 
 */
abstract class Design extends Document {

    /**
     * Creates a new Design Document operating on a database
     *
     * In case the database is not yet setup, this triggers database creation
     *
     * Load existing views if the design document is already created
     *
     * @param db - target database
     * @param name - document name (_design/<name>)
     *
     */
    protected def Design(Database db, String name) {
        useDatabase(db)

        if (!db.exists())
            new Server().createDatabase(db.getName())

        try {
            def des = db.get("_design/${name}")
            des.each { k, v -> put(k, v) }
            return
        } catch (e) { }

        initializeDesign(name)        
    }

    // Replaces the views property with an empty Map
    protected def clearViews() {
        remove("views")
        put("views", [:])
    }
    
    protected def putView(String name, Map view) {
        if (!has("views"))
            put("views", [:])

        get("views")[name] = view
    }

    protected def putBasicView(attribute) {
        def mapFunction = {"""
          function(doc) {
            if (doc.${attribute})
              emit(doc.${attribute}, null);
          }"""
        }        

        putView("by_${attribute}", ["map" : mapFunction()])
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

    def getViewNames() {
        get("views").keySet()
    }

    def hasView(name) {
        name in getViewNames()
    }

    /**
     * Rebuilds the design document's view map
     */
    abstract def refresh();

    ////
    //// Private
    ////

    private def initializeDesign(name) {
        def db = super.database
        
        if (!db.exists())
            new Server().createDatabase(db.getName())

        if (!id)
            put("_id", "_design/${name}")

        if (!has("language"))
            put("language", "javascript")

        if (!has("views"))
            put("views", [:])

        refresh()
        save()
    }
	
}