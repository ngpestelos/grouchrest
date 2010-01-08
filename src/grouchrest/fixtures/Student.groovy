package grouchrest.fixtures


import grouchrest.ExtendedDocument

class Student extends ExtendedDocument {    

    private static def s

    static {
        s = new Student()
        viewBy(s, "lastname")
    }

    def Student() {
        super("grouchrest_test")        
    }

    static def findByLastname(Map params = [:]) {
        s.design.view("by_lastname", params)
    }
	
}