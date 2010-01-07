package grouchrest.fixtures

import grouchrest.ExtendedDocument

class Article extends ExtendedDocument {

    static def DB = "grouchrest_test"

    static {
        
    }

    def bdest = 0
    def adest = 0

    def bsave = 0
    def asave = 0

    def Article() {
        super(Article.class)
    }

    def Article(Map doc) {
        super(Article.class, doc)
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

}