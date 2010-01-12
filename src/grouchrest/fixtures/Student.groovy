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

    static def findByLastname(Map params = [:]) {
        s.design.view("by_lastname", params)
    }
	
}