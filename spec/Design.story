import grouchrest.*

before_each "test database", {
  server = new Server()

  resetTestDatabase = {
    try { HttpClient.delete("http://127.0.0.1:5984/test_39579") } catch (e) { }
    HttpClient.put("http://127.0.0.1:5984/test_39579")
    server.getDatabase("test_39579")
  }
}

scenario "defining a view", {
  then "it should add a view to the design doc", {
    des = new Design()
    method = des.viewBy("name")
    method.shouldBe "by_name"
    des.get("views")["by_name"].shouldNotBe null
  }
}

scenario "with an unsaved view", {
  before_each "setup", {
    des = new Design()
    des.viewBy("name")
  }

  then "it should accept a name", {
    des.name = "mytest"
    des.name.shouldBe "mytest"
  }

  then "it should not save on view definition", {
    des.rev.shouldBe null
  }

  then "it should freak out on view access", {
    ensureThrows(Exception) { des.view "by_name" }
  }
}

scenario "saving", {
  before "setup", {
    des = new Design()
    des.viewBy "name"
    des.database = resetTestDatabase()
  }

  then "it should fail without a name", {
    ensureThrows(Exception) { des.save() }
  }

  then "it should fail without a name", {
    des.name = "myview"
    des.save().shouldNotBe null
  }
}

scenario "save and view", {
  given "some documents", {
    db = resetTestDatabase()
    db.bulkSave([["name" : "x"], ["name" : "y"]])
    des = new Design()
    des.database = db
    des.viewBy "name"
  }

  then "it should be queryable when it is saved", {
    des.name = "mydesign"
    des.save()
    res = des.view("by_name") // Groovy complains if parentheses are missing
    res["rows"][0]["key"].shouldBe "x"
  }

  then "it should be queryable on the specified database", {
    des.name = "mydesign"
    des.save()
    des.database = null
    res = db.view("mydesign/by_name")
    res["rows"][0]["key"].shouldBe "x"
  }
}
