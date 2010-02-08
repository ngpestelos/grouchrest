import grouchrest.*

before "setup database", {
  TESTDB = "test_39579"
  HttpClient.delete("http://127.0.0.1:5984/${TESTDB}")
  HttpClient.put("http://127.0.0.1:5984/${TESTDB}")
  db = new Server().getDatabase(TESTDB)
}

scenario "access an unknown view", {
  given "an empty design doc", {
    map = [
      "_id" : "_design/foo"
    ]
    design = new Design(map)
    design.useDatabase(db)
    design.save()
  }

  then "it should fail when accessing a view", {
    ensureThrows(Exception) { design.view("abc") }
  }
}

scenario "single view", {
  given "a design doc", {
    mapFunction = "function(doc) { emit(doc.name, null) }"

    map = [
      "_id" : "_design/abc",
      "views" : ["by_name" : ["map" : mapFunction]]
    ]

    db.save(["name" : "Nesingwary 4000"])
    design = new Design(map)
    design.useDatabase(db)
    design.save()

    res = design.view("by_name")
    assert (res["total_rows"] == 1)
    assert (res["rows"].size() == 1)
  }
}

scenario "add a view", {
  given "an existing design doc", {
    map = db.get("_design/abc")

    mapFunction = "function(doc) { emit(doc.code, null); }"
    map["views"]["by_code"] = ["map" : mapFunction]

    design = new Design(map)
    design.useDatabase(db)
    design.save()
    assert (design.get("_rev") =~ /^2/)
  }
}
