import grouchrest.*

before_each "setup test database", {
  server = new Server()
  server.deleteDatabase("test_54737")
  db = server.createDatabase("test_54737")
}

scenario "name includes a slash", {
  then "it should escape the name in the URI", {
    dbx = server.getDatabase("foo/bar")
    dbx.getName().shouldBe "foo/bar"
    dbx.getURI().shouldBe "http://127.0.0.1:5984/foo%2Fbar"
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
 
    res = db.bulkSave([
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
    res = db.view("test/test")
    res["total_rows"].shouldBe 1
  }

  then "it should round trip", {
    println "it should round trip"
    db.get("_design/test")["views"].shouldBe view
  }
}

scenario "select from an existing view", {
  given "a view", {
    println "select from an existing view"

    view = [
      "test" : ["map" : """function(doc) {
                             for (var w in doc) {
                               if (!w.match(/^_/))
                                 emit(w, doc[w]);
                             }
                           }"""]
    ]
    doc = [
      "_id" : "_design/first",
      "views" : view
    ]

    db.save(doc)
  }

  given "some documents", {
    db.bulkSave([
      ["wild" : "and random"],
      ["mild" : "yet local"],
      ["another" : ["set", "of", "keys"]]
    ])
  }

  then "it should have the view", {
    println "it should have the view"

    fun = db.get("_design/first")["views"]["test"]["map"]
    def buf = new StringBuffer("for (var w in doc)")
    fun.contains(buf).shouldBe true
  }

  then "it should list from the view", {
    println "it should list from the view"

    res = db.view("first/test")
    sub = res["rows"].findAll { r -> r["key"] == "wild" && r["value"] == "and random" }
    sub.size().shouldBe 1
  }

  then "it should work with a range", {
    println "it should work with a range"

    res = db.view("first/test", ["startkey" : "b", "endkey" : "z"])
    res["rows"].size().shouldBe 2
  }

  then "it should work with a key", {
    println "it should work with a key"

    res = db.view("first/test", ["key" : "wild"])
    res["rows"].size().shouldBe 1
  }

  then "it should work with a limit", {
    println "it should work with a limit"

    res = db.view("first/test", ["limit" : 1])
    res["rows"].size().shouldBe 1
  }

  then "it should work with multi-keys", {
    println "it should work with multi-keys"

    res = db.view("first/test", ["keys" : ["another", "wild"]])
    res["rows"].size().shouldBe 2
  }

}

scenario "GET (document by id) when the doc exists", {
  given "a document", {
    println "GET (document by id) when the doc exists"

    res = db.save(["lemons" : "from texas", "and" : "spain"])
    docid = "http://example.com/stuff.cgi?things=and%20stuff"

    db.save(["_id" : docid, "will-exist" : "here"])
  }

  then "it should get the document", {
    println "it should get the document"

    doc = db.get(res["id"])
    doc["lemons"].shouldBe "from texas"
  }

  then "it should work with a funky id", {
    println "it should work with a funky id"

    db.get(docid)["will-exist"].shouldBe "here"
  }
}

scenario "POST (adding bulk documents)", {

  given "some documents", {
    println "POST (adding bulk documents)"
    res = db.bulkSave([
      ["wild" : "and random"],
      ["mild" : "yet local"],
      ["another" : ["set","of","keys"]]
    ])
  }

  then "it should add them without ids", {
    println "it should add them without ids"
    res.each { r -> db.get(r["id"])["_rev"].shouldBe r["rev"] }
  }

}

scenario "new document without an id", {

  given "an empty database", {
    println "new document without an id"
    res = db.getDocuments()
    assert (res["total_rows"] == 0)
  }

  then "it should create the document and return the id", {
    println "it should create the document and return the id"
    res = db.save(["lemons" : "from texas", "and" : "spain"])
    res2 = db.get(res["id"])
    res2["lemons"].shouldBe "from texas"
  }

  then "it should use PUT with UUIDs", {
    println "it should use PUT with UUIDs"
    res = db.save(["just" : "another document"])
    res["id"].shouldNotBe null
  }
}

scenario "new document with id", {

  given "an empty database", {
    println "new document with id"
    res = db.getDocuments()
    assert (res["total_rows"] == 0)
  }

  then "it should create the document", {
    res = db.save(["_id" : "my-doc", "will-exist" : "here"]) 
    db.get("my-doc")["will-exist"].shouldBe "here"
  }

}

scenario "PUT (existing document with rev)", {

  given "a document", {
    println "PUT (existing document with rev)"
    db.save(["_id" : "my-doc", "will-exist" : "here"])
  }

  then "it should fail to resave without the rev", {
    println "it should fail to resave without the rev"
    doc = db.get("my-doc")
    doc["song"] = "Magic Carpet Ride"
    doc.remove("_rev")
    ensureThrows(Exception) { db.save(doc) }
  }

  then "it should update the document", {
    println "it should update the document"
    doc = db.get("my-doc")
    doc["song"] = "Magic Carpet Ride"
    res = db.save(doc)
    now = db.get("my-doc")
    now["song"].shouldBe "Magic Carpet Ride"
  }
}

scenario "DELETE existing document", {
  given "DELETE existing document", {
    println "DELETE existing document"
  }

  then "it should delete a document", {
    println "it should delete a document"
    res = db.save(["lemons" : "from texas", "and" : "spain"])
    doc = db.get(res["id"])
    db.deleteDoc(doc)
    ensureThrows(Exception) { db.get(res["id"]) }
  }

  then "it should fail without an _id", {
    println "it should fail without an _id"
    res = db.save(["lemons" : "from texas", "and" : "spain"])
    doc = db.get(res["id"])
    doc.remove("_id")
    ensureThrows(Exception) { db.deleteDoc(doc) }
  }
}

scenario "DELETE Document object", {
  given "a property list", {
    doc = new Document()
    doc.useDatabase(db)
    doc.put("lemons", "from texas")
    doc.put("and", "spain")
    doc.put("_id", "abcde")
    doc.save()
  }

  when "the document gets deleted", {
    db.deleteDoc(doc)
  }

  then "it should be gone", {
    ensureThrows(Exception) { db.get("abcde") }
  }
}

scenario "cached bulk save", {
  then "it stores documents in a database-specific cache", {
    td = ["_id" : "btd1", "val" : "test"]
    db.save(td, true)
    db.bulkSaveCache.size().shouldBe 1
    db.bulkSaveCache.first().shouldBe td
  }

  then "it doesn't save to the database until the configured cache size is exceeded", {
    db.BULK_LIMIT = 3
    db.bulkSaveCache.clear()

    td1 = ["_id" : "td1", "val" : true]
    td2 = ["_id" : "td2", "val" : 4]
    db.save(td1, true)
    db.save(td2, true)
    ensureThrows(Exception) { db.get(td1["_id"]) }
    ensureThrows(Exception) { db.get(td2["_id"]) } 
    td3 = ["_id" : "td3", "val" : "foo"]
    db.save(td3, true)
    db.get(td1["_id"])["val"].shouldBe td1["val"]
    db.get(td2["_id"])["val"].shouldBe td2["val"]
    db.get(td3["_id"])["val"].shouldBe td3["val"]
  }

  then "it should flush the cache", {
    td1 = ["_id" : "xtd1", "val" : true]
    db.save(td1, true)

    td2 = ["_id" : "xtd2", "val" : 4]
    db.save(td2, true)

    td3 = ["_id" : "xtd3", "val" : "foo"]
    db.save(td3)

    db.get(td1["_id"])["val"].shouldBe td1["val"]
    db.get(td2["_id"])["val"].shouldBe td2["val"]
    db.get(td3["_id"])["val"].shouldBe td3["val"]
  }
}

scenario "flush the bulk save cache", {
  given "a bulk limit of three", {
    db.BULK_LIMIT = 3
    assert (db.bulkSaveCache.size() == 0)
  }

  given "two documents are saved in bulk", {
    td1 = ["_id" : "td1", "val" : true]
    td2 = ["_id" : "td2", "val" : 4]
    db.save(td1, true)
    db.save(td2, true)
  }

  when "a third document is saved", {
    td3 = ["_id" : "td3", "val" : "last"]
    db.save(td3, true)
  }

  then "it should trigger the flush", {
    db.bulkSaveCache.size().shouldBe 0
  }
}

scenario "bulk delete", {
  given "a bulk limit of two", {
    db.BULK_LIMIT = 2
  }

  given "two documents saved", {
    td1 = ["_id" : "td1", "val" : true]
    td2 = ["_id" : "td2", "val" : 4]
    db.save(td1, true)
    db.save(td2, true)
  }

  when "both documents deleted", {
    doc1 = db.get("td1")
    doc2 = db.get("td2")
    db.deleteDoc(doc1, true)
    db.deleteDoc(doc2, true) 
  }

  then "both docs should be gone", {
    ensureThrows(Exception) { db.get("td1") }
    ensureThrows(Exception) { db.get("td2") }
  }
}

scenario "database exists", {
  then "it should see the test database", {
    db.exists().shouldBe true
  }

  then "test_blah database should not exist", {
    blah = server.getDatabase("test_blah")
    blah.exists().shouldBe false
  }
}
