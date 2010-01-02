import greenapple.couch.Server

before "setup server", {
  TESTDB = "db_12345"

  server = new Server()
}

before_each "nuke the database", {
  try { server.deleteDatabase(TESTDB) } catch (e) { }
  db = server.createDatabase(TESTDB)
}
