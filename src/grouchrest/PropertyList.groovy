package grouchrest

class PropertyList {

    private def _attributes = [:]    

    protected def PropertyList() { }

    protected def PropertyList(Map map) { _attributes = map }

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

    protected def getAttributes() {
        _attributes
    }

}