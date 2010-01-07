package grouchrest.fixtures


import grouchrest.ExtendedDocument

class Student extends ExtendedDocument {

    static def DB = "grouchrest_test"

    static {
        viewBy (Student.class, "lastname")
    }

    def Student() {
        super(Student.class)
    }    
	
}

