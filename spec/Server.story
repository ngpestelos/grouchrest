import greenapple.couch.HttpClient
import greenapple.couch.Server

before "setup local server", {
  couch = new Server()
}

after_each "", {
  try { HttpClient.delete("http://127.0.0.1:5984/grouch-server-test-db") } catch (e) { }
  try { HttpClient.delete("http://127.0.0.1:5984/grouch-server-default-db") } catch (e) { }
}

scenario "add databases", {

  then "it should let you add more databases", {
    couch.getAvailableDatabases().shouldBeEmpty
    couch.defineAvailableDatabase(Server.DEFAULT, "grouch-server-test-db")
    couch.getAvailableDatabases()[Server.DEFAULT].shouldNotBe null
  }

}

scenario "verify database", {

  then "it should verify that a database is available", {
    couch.defineAvailableDatabase(Server.DEFAULT, "grouch-server-test-db")
    couch.availableDatabase(Server.DEFAULT).shouldBe true
    couch.availableDatabase("foo").shouldBe false
  }

}

scenario "default database", {
  then "it should set a default database", { 
    couch.setDefaultDatabase("grouch-server-default-db")
    couch.availableDatabase(Server.DEFAULT).shouldBe true
  }
}

scenario "get info", {
  then "it should return something", {
    couch.getInfo().shouldNotBe null
  }
}
