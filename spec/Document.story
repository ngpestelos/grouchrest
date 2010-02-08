import grouchrest.*

TESTDB="test_98574"

before_each "clean database", {
  HttpClient.delete("http://127.0.0.1:5984/${TESTDB}")
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

scenario "destroy existing document", {
  given "a document", {
    doc = new Document("name" : "Nesingwary 4000")
    doc.database = db
    doc.save()
    assert (doc.get("_id") != null)
    assert (doc.get("_rev") != null)
  }

  when "destroyed", {
    doc.destroy()
  }

  then "it's gone", {
    doc.get("_id").shouldBe null
    doc.get("_rev").shouldBe null 
  }
}

scenario "destroy unsaved document", {
  given "a document", {
    doc = new Document("name" : "Nesingwary 4000")
    doc.database = db
  }

  then "it should fail", {
    ensureThrows(Exception) { doc.destroy() }
  }
}
