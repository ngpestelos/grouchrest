import grouchrest.*

before "setup local server", {
  couch = new Server()
}

scenario "get info", {
  then "it should return something", {
    info = couch.getInfo()
    assert (info["status"] =~ /200/)
    info.shouldNotBe null
  }
}

scenario "create/delete database", {
  given "a new database", {
    url = "http://127.0.0.1:5984/test_98765"
    res = HttpClient.delete(url)
    server = new Server()
  }

  then "it should create the database", {
    db = server.createDatabase("test_98765")
    db.shouldNotBe null
    db.name.shouldBe "test_98765"
  }

  then "it should exist", {
    db.exists().shouldBe true
  }

  then "it should return the same db name on multiple calls", {
    db2 = server.createDatabase("test_98765")
    db2.shouldNotBe null
    db2.name.shouldBe "test_98765"
  }

  when "database is deleted", {
    server.deleteDatabase("test_98765")  
  }

  then "it should no longer exist", {
    db.exists().shouldBe false
  }
}
