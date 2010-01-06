import grouchrest.*

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
    doc.id.shouldNotBe null
    doc.rev.shouldNotBe null
  }
}

// for now let's assume it's there
class X extends Document {
  static def DB = "xdb"
}

class Y extends Document {
  static def DB = "ydb"
}
  
scenario "default database name for subclasses", {
  given "two document subclasses", {
    x = new X()
    y = new Y()
  }

  then "it should use different databases", {
    x.DB.shouldBe "xdb"
    y.DB.shouldBe "ydb"
  }
}

class Video extends Document {
  static def DB = "test_98574" 

  def Video() {
    database = new Server().getDatabase(DB)
  }
}

scenario "default database", {
  given "a Video document", {
    v = new Video()
  }

  then "it should have a database property", {
    v.database.name.shouldBe TESTDB
  }

  then "it should be overwritten", {
    replacement = new Server().getDatabase("x")
    v.useDatabase replacement
    v.database.name.shouldBe "x"
  }
}
