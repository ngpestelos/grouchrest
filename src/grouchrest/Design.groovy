package grouchrest

class Design extends Document {    

    def setName(name) {
        put("_id", "_design/${name}")
    }

    def getName() {
        if (id)
            id.replace('_design/', '')
    }

    def viewBy(keys) {
        if (keys instanceof String)
            keys = [keys]

        def opts = [:]
        if (keys.last() instanceof Map)
            opts = keys.pop()
        
        if (!has("views")) put("views", [:])

        def methodName = "by_${keys.join("_and_")}"
        
        def views = has("views") ? get("views") : [:]
        views[methodName] = [:]
        put("views", views)
        
        if (opts["map"]) {
            
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

        return methodName
    }

    // dispatch to a named view
    def view(String viewName, query = [:], closure = null) {
        viewOn database, viewName, query, closure
    }

    def viewOn(Database db, String viewName, query = [:], closure = null) {
        def viewSlug = "${name}/${viewName}"
        db.view(viewSlug, query, closure)
    }

    def save() {
        if (!getName())
            throw new IllegalStateException("_design docs require a name")

        super.save()
    }
	
}