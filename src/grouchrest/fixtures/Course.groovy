package grouchrest.fixtures


import grouchrest.ExtendedDocument

class Course extends ExtendedDocument {

    static def DB = "grouchrest_test"

    static {

    }

    def Course() {
        super(Course.class)
    }

    def Course(Map doc) {
        super(Course.class, doc)
    }
	
}

