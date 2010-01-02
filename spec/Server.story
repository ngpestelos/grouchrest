import greenapple.couch.Server

before "setup local server", {
  couch = new Server()
}

after_each "", {
  couch.getAvailableDatabases().each { k, db ->
    db.delete()
  }
}

scenario "available databases", {

  then "it should let you add more databases", {
    couch.getAvailableDatabases().shouldBeEmpty
    couch.defineAvailableDatabase(Server.DEFAULT, "grouch-server-test-db")
    couch.getAvailableDatabases()[Server.DEFAULT].shouldNotBe null
  }

  then "it should verify that a database is available", {
    couch.defineAvailableDatabase(Server.DEFAULT, "grouch-server-test-db")
    couch.availableDatabase(Server.DEFAULT).shouldBe true
    couch.availableDatabase("foo").shouldBe false
  }

  then "it should let you set a default database", {
    couch.setDefaultDatabase("grouch-server-default-db")
    couch.availableDatabase(Server.DEFAULT).shouldBe true
  }

}
