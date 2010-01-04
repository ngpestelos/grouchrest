import greenapple.couch.*

class FooDesign extends DesignDocument {

  def FooDesign(db) {
    super(db, "foo")
  }

}

before_each "clear existing database", {
  try { HttpClient.delete("http://127.0.0.1:5984/test_67584") } catch (e) { }
  HttpClient.put("http://127.0.0.1:5984/test_67584")
  server = new Server()
  db = server.defineAvailableDatabase("test", "test_67584", false) 
}

scenario "manual push", {
  then "it should save a design doc", {
    res = db.save(["_id" : "_design/foo"])
    res["id"].shouldNotBe null
    res["rev"].shouldNotBe null
    res["ok"].shouldBe true
  }
}

scenario "push using subclass", {
  given "a subclass of DesignDocument", {
    foo = new FooDesign(db)
  }

  then "it should save its design document", {  
    println foo.push()
  }
}
