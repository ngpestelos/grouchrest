package grouchrest

class Design extends Document {

    def Design() { }

    def Design(Map props) {
        props.each { k, v -> put(k, v) }
    }

    def setName(name) {
        put("_id", "_design/${name}")
    }

    def getName() {
        if (id)
            id.replace('_design/', '')
    }

    def viewBy(keys, opts = [:]) {
        if (keys instanceof String)
            keys = [keys]        
        
        if (!has("views")) put("views", [:])

        def methodName = "by_${keys.join("_and_")}"
        
        def views = has("views") ? get("views") : [:]
        views[methodName] = [:]
        put("views", views)
        
        if (opts["map"]) {
            def view = [:]
            view["map"] = opts.remove("map")
            if (opts["reduce"])
                view["reduce"] = opts.remove("reduce")

            views[methodName] = view
        } else {
            def docKeys = keys.collect { k -> "doc['${k}']"}
            def keyEmit = (docKeys.size() == 1) ? "${docKeys.first()}" : "[${docKeys.join(", ")}]"
            //def guards = opts["guards"] ? opts.remove("guards") : []
            def guards = []
            guards.addAll(docKeys)
            def mapFunction = {
            """function (doc) {
                 if (${guards.join(" && ")})
                   emit (${keyEmit}, null);
               }"""
            }
            views[methodName] = ["map" : mapFunction()]
            put("views", views)
        }
        if (opts)
            views[methodName]["grouchrest-defaults"] = opts
        
        return methodName
    }

    // dispatch to a named view
    def view(String viewName, query = [:], closure = null) {
        if (!database)
            throw new IllegalStateException("_design docs require a database")

        if (query == null)
            query = [:]

        viewOn database, viewName, query, closure
    }    

    def save() {
        if (!getName())
            throw new IllegalStateException("_design docs require a name")

        super.save()
    }

    private def viewOn(Database db, String viewName, query = [:], closure = null) {
        def viewSlug = "${name}/${viewName}"
        def defaults = (get("views")[viewName] && get("views")[viewName]["grouchrest-defaults"]) ?
            get("views")[viewName]["grouchrest-defaults"] : [:]
        db.view(viewSlug, defaults.plus(query), closure)
    }
	
}