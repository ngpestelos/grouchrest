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

scenario "a view with default options", {
  given "some documents", {
    db = resetTestDatabase()
    des = new Design()
    opts = ["descending" : true]
    des.name = "test"
    des.viewBy("name", opts)
    des.database = db
    des.save()
    db.bulkSave([["name" : "a"], ["name" : "z"]])
  }

  then "it should save them", {
    doc = db.get(des.id)
    doc.get("views")["by_name"]["grouchrest-defaults"].shouldBe (["descending" : true])
  }

  then "it should use them", {
    res = des.view("by_name")
    res["rows"].first()["key"].shouldBe "z"
  }

  then "it should override them", {
    res = des.view("by_name", ["descending" : false])
    res["rows"].first()["key"].shouldBe "a"
  }
}
