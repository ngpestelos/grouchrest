import grouchrest.*

class Video extends Document { }
TESTDB="test_98574"

before_each "clean database", {
  try { HttpClient.delete("http://127.0.0.1:5984/${TESTDB}") } catch (e) { }
  HttpClient.put("http://127.0.0.1:5984/${TESTDB}")
  server = new Server()
  db = server.getDatabase(TESTDB)
}

scenario "get/set property", {
  given "a document", {
    doc = new Document()
  }

  then "it should work", {
    doc.get("enamel").shouldBe null
    doc.put("enamel", "Strong")
    doc.get("enamel").shouldBe "Strong"
  }
}

scenario "default database", {
  before_each "clear", {
    Video.useDatabase null
  }

  then "it should be set using useDatabase on the model", {
    new Video().database.shouldBe null
    Video.useDatabase db
    new Video().database.shouldBe db
    Video.useDatabase null
  }

  then "it should be overwritten by instance", {
    test = server.getDatabase("test")
    article = new Video()
    article.database.shouldBe null
    article.database = test
    article.database.shouldNotBe null
    article.database.shouldBe test
    
  }
}

scenario "new", {
  given "a new document", {
    doc = new Document("key" : [1,2,3], "more" : "values")
  }

  then "it should create itself from a Map", {
    assert doc.get("key") == [1,2,3]
    assert doc.get("more") == "values"
  }
  
  then "it should not have rev and id", {
    doc.id.shouldBe null
    doc.rev.shouldBe null
  }

  then "it should freak out when saving without a database", {
    ensureThrows(Exception) { doc.save() }
  }
}

scenario "save", {
  given "a document", {
    doc = new Document("firstname" : "Mister", "lastname" : "Suave")
    doc.database = db
  }

  then "it should save", {
    doc.save().shouldBe true
  }
}
