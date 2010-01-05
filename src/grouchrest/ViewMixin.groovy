package grouchrest


class ViewMixin {

    

    def methodMissing(String name, args) {
        println "in view mixin ${delegate.getClassName()}"
    }
	
}

