package grouchrest.fixtures

import grouchrest.ExtendedDocument

class Article extends ExtendedDocument {

    static def DB = "grouchrest_test"

    static {
        
    }

    def Article() {
        super(Article.class)
    }

    def Article(Map doc) {
        super(Article.class, doc)
    }

}