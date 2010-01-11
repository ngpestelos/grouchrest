package grouchrest.fixtures

import grouchrest.ExtendedDocument

class Article extends ExtendedDocument {    

    def bdest = 0
    def adest = 0

    def bsave = 0
    def asave = 0

    private static def a

    static {
        a = new Article()
        makeView(a, "title", false)
        makeView(a, "type", false)
        a.design.save()
    }

    def Article() {
        super("grouchrest_test")
    }

    def Article(Map doc) {
        super("grouchrest_test", doc)
    }

    def beforeDestroy() {        
        bdest += 1
    }

    def afterDestroy() {
        adest += 1
    }

    def beforeSave() {
        bsave += 1
    }

    def afterSave() {
        asave += 1
    }

    static Integer count() {        
        a.design.view("by_type", ["key" : "article"])["rows"].size()
    }

}