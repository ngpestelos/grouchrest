package grouchrest.fixtures


import grouchrest.ExtendedDocument

class Student extends ExtendedDocument {    

    private static def s

    static {        
        makeView("grouchrest_test", ["lastname"])
        s = new Student()
    }

    def Student() {
        super("grouchrest_test")        
    }

    def Student(Map attributes) {
        super("grouchrest_test", attributes)
    }    

    static def findByLastname(String name = null) {        
        if (name)
            ExtendedDocument.findBy(s, "by_lastname", ["key" : name])
        else
            ExtendedDocument.findBy(s, "by_lastname")
    }
	
}