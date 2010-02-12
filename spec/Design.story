import grouchrest.*

class MyDesign extends Design {
  def MyDesign(Database db, String name) {
    super(db, name)
  }

  def refresh() {
    def map = "function(doc) { if (doc.code) emit(doc.code, null); }"

    clearViews()
    putView("by_code", ["map" : map])
  }
}

scenario "database does not exist", {
  given "an unknown database", {
    try { HttpClient.delete("http://127.0.0.1:5984/nulldb") } catch (e) { }
    db = new Server().getDatabase("nulldb")
    assert (db.exists() == false)
  }

  when "design doc is created", {
    design = new MyDesign(db, "foo")
  }

  then "it should have an _id", {
    design.get("_id").shouldBe "_design/foo"
  }

  then "it should trigger database creation", {
    db.exists().shouldBe true
  } 

  then "it should have a view", {
    design.get("views").size().shouldBe 1
  }
}

before_each "clean up", {
  TESTDB = "test_39579"
  HttpClient.delete("http://127.0.0.1:5984/${TESTDB}")
  HttpClient.put("http://127.0.0.1:5984/${TESTDB}")
  db = new Server().getDatabase(TESTDB)
  design = new MyDesign(db, "foo")
}

scenario "view something", {
  given "a design doc", {
    assert (design.get("_id") == "_design/foo")
  }

  given "a document", {
    db.save("code" : "12345")
  }

  then "it should see the document", {
    res = design.view("by_code")
    assert (res["total_rows"] == 1)
  }
}

scenario "unknown view", {
  then "it should fail", {
    ensureThrows(Exception) { design.view("abc") }
  }
}

scenario "add a view", {
  when "another view is added", {
    design.putBasicView("some_property")
    design.save()
  }

  then "it should be updated", {
    assert (design.get("_rev") =~ /^2/)
  }

  then "it should be accessible", {
    res = design.view("by_some_property")
    assert (res["total_rows"] == 0)
  }

  then "it should return 2 views", {
    assert (design.getViewNames().size() == 2)
  }  
}

scenario "has view", {
  then "it should return true", {
    assert (design.hasView("by_code") == true)
  }

  then "it should return false", {
    assert (design.hasView("") == false)
  }
}
