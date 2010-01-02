import greenapple.couch.Server

before "setup server", {
  TESTDB = "db_12345"

  server = new Server()
}

before_each "nuke the database", {
  try { server.deleteDatabase(TESTDB) } catch (e) { }
  db = server.createDatabase(TESTDB)
}

scenario "database name including slash", {
  then "it should escape the name in the URI", {
    db = server.getDatabase("foo/bar")
    db.getName().shouldBe "foo/bar"
    db.getURI().shouldBe "http://127.0.0.1:5984/foo%2Fbar"
  }
}

scenario "get info", {
  then "it should get a response from the database", {
    db.getInfo().shouldNotBe null
  }
}
