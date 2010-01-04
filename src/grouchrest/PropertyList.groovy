package grouchrest

class PropertyList {

    private def _attributes = [:]    

    protected def PropertyList() { }

    def put(key, value) {
        _attributes[key] = value
    }

    def has(key) {
        (_attributes.keySet().find { it == key }) ? true : false
    }

    def get(key) {
        has(key) ? _attributes[key] : null
    }

    def remove(key) {
        if (has(key))
            _attributes.remove(key)
    }    

    protected def attributes() {
        _attributes
    }

}