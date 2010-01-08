package grouchrest.fixtures


import grouchrest.ExtendedDocument

class Course extends ExtendedDocument {    

    def Course() {
        super("grouchrest_test")
    }

    def Course(Map doc) {
        super("grouchrest_test", doc)
    }
	
}

