import greenapple.couch.Database
import greenapple.couch.Server

before "setup server", {
  TESTDB = "db_12345"
  server = new Server()
}

before_each "create", {
  db = server.createDatabase(TESTDB)
}

after_each "nuke", {
  try { db.delete() } catch(e) { }
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

scenario "map query with _temp_view in Javascript", {
  given "some documents and a view function", {
    println "map query with _temp_view"

    db.bulkSave([
      ["wild" : "and random"],
      ["mild" : "yet local"],
      ["another" : ["set", "of", "keys"]]
    ])

    view = ["map" :
      """function(doc) {
           for (var w in doc) { if (!w.match(/^_/)) emit(w, doc[w]); }
         }"""]
  }

  then "it should get a result", {
    println "it should get a result"

    res = db.tempView(view)
    wild = res["rows"].find { r -> r["key"] == "wild" }
    wild.shouldNotBe null
  }

  then "it should work with a range", {
    println "it should work with a range"

    res = db.tempView(view, ["startkey" : "b", "endkey" : "z"])
    res["rows"].size().shouldBe 2
  }

  then "it should work with a key", {
    println "it should work with a key"

    res = db.tempView(view, ["key" : "wild"])
    res["rows"].size().shouldBe 1
  }

  then "it should work with a limit", {
    println "it should work with a limit"

    res = db.tempView(view, ["limit" : 1])
    res["rows"].size().shouldBe 1
  }

  then "it should work with multi-keys", {
    println "it should work with multi-keys"

    res = db.tempView(view, ["keys" : ["another", "wild"]])
    res["rows"].size().shouldBe 2
  }
}

scenario "map/reduce query with _temp_view in Javascript", {
  given "some data", {
    println "map/reduce query with with _temp_view"
 
    db.bulkSave([
      ["beverage" : "bourbon", "count" : 1],
      ["beverage" : "scotch", "count" : 1],
      ["beverage" : "beer", "count" : 1],
      ["beverage" : "beer", "count" : 1]
    ])
  }

  then "it should return the result of the temp function", {
    println "it should return the result of the temp function"

    res = db.tempView(["map" :
      "function(doc) { emit(doc.beverage, doc.count) }",
      "reduce" : "function(beverage, counts) { return sum(counts) }"])
    rows = res["rows"]
    rows[0]["value"].shouldBe 4
  }
}

scenario "saving a view", {
  given "a design doc", {
    println "saving a view"

    // I'm getting backslash escape errors
    view = ["test" :
      ["map" : """function(doc) {
                    if (doc.word && !/[^a-zA-Z0-9_]/.test(doc.word))
                      emit(doc.word, null);
                  }"""]
    ]

    doc = ["_id" : "_design/test", "views" : view]
    db.save(doc)
  }

  then "it should work properly", {
    println "it should work properly"
    db.bulkSave([["word" : "once"], ["word" : "and again"]])
    db.view("test/test")["total_rows"].shouldBe 1
  }

  then "it should round trip", {
    println "it should round trip"
    db.get("_design/test")["views"].shouldBe view
  }
}