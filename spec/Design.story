import grouchrest.*

before "setup database", {
  TESTDB = "test_39579"
  HttpClient.delete("http://127.0.0.1:5984/${TESTDB}")
  HttpClient.put("http://127.0.0.1:5984/${TESTDB}")
  db = new Server().getDatabase(TESTDB)
  design = new Design(db, "foo")
}


scenario "new design doc", {
  given "a new design doc", {
    assert (design.get("_id") == "_design/foo")
  }
}

scenario "view something", {
  given "a view", {
    design.makeBasicView("name")
    design.save()
  } 

  given "a document", {
    db.save("name" : "Nesingwary 4000")
  }

  then "it should see the document", {
    res = design.view("by_name")
    assert (res["total_rows"] == 1)
  }
}

scenario "unknown view", {
  then "it should fail", {
    ensureThrows(Exception) { design.view("abc") }
  }
}

scenario "add a view", {
  given "another view", {
    design.makeBasicView("code")
    design.save()
  }

  then "it should be updated", {
    assert (design.get("_rev") =~ /^2/)
  }

  then "it should be accessible", {
    res = design.view("by_code")
    assert (res["total_rows"] == 0)
  }  
}

scenario "get view names", {
  then "it should return 2 views", {
    assert (design.getViewNames().size() == 2)
  }
}

scenario "has view", {
  then "it should return true", {
    assert (design.hasView("by_name") == true)
  }
}
