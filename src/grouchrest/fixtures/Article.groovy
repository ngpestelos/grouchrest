package grouchrest.fixtures

import grouchrest.ExtendedDocument

class Article extends ExtendedDocument {

    static def DB = "grouchrest_test"

    def Article() {
        super(Article.class)
    }

}