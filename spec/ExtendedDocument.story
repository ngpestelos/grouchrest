import grouchrest.*
import grouchrest.fixtures.*

before "setup the database", {
  TESTDB = "grouchrest_test"

  cleanup = {
    try { HttpClient.delete("http://127.0.0.1:5984/${TESTDB}") } catch (e) { }
    HttpClient.put("http://127.0.0.1:5984/${TESTDB}")
  }

  db = new Server().getDatabase(TESTDB)
}

scenario "view by some property", {
  given "a clean database", {
    cleanup()
  }

  when "views are created for these attributes", {
    ExtendedDocument.makeView(TESTDB, ["foo", "bar", "baz"])
  }

  then "it should create a design document", {
    views = db.get("_design/${TESTDB}").get("views")
    views.size().shouldBe 3
    views["by_foo"].shouldNotBe null
    views["by_bar"].shouldNotBe null
    views["by_baz"].shouldNotBe null
  }

  then "it should not update the design doc if the list is constant", {
    ExtendedDocument.makeView(TESTDB, ["foo", "bar", "baz"])

    design = new Design(db.get("_design/${TESTDB}"))
    design.rev[0].shouldBe "1"
  }

  then "it should update if an attribute is added", {
    ExtendedDocument.makeView(TESTDB, ["foo", "bar", "baz", "abc"])

    design = new Design(db.get("_design/${TESTDB}"))
    design.rev[0].shouldBe "2"
  }

  then "it should update if an attribute is removed", {
    ExtendedDocument.makeView(TESTDB, ["bar", "baz", "abc"])

    design = new Design(db.get("_design/${TESTDB}"))
    design.rev[0].shouldBe "3"
  }
}

scenario "setup", {
  given "a student", {
    cleanup() 
    student = new Student()
  }

  then "it should have a default database", {
    student.database.name.shouldBe "grouchrest_test"
  }

  then "it should have a design document", {
    student.design.name.shouldBe "grouchrest_test"
    student.design.has("views").shouldBe true
  }
}

scenario "unknown finder", {
  then "it should fail", {
    ensureThrows(MissingMethodException) { Student.findByFoo() }
  }
}

scenario "find by lastname", {
  given "two students", {
    cleanup()

    // you have to re-create the view
    ExtendedDocument.makeView(TESTDB, ["lastname"])

    x = new Student()
    x.put("lastname", "Foo")
    x.save()  

    y = new Student()
    y.put("lastname", "Bar")
    y.save()
  }

  then "it should work", {
    res = Student.findByLastname()
    res.shouldNotBe null
    res.size().shouldBe 2
    first = res.first()
    assert(first instanceof Student)
    first.get("lastname").shouldBe "Bar"
  }

  then "it should find by key", {
    res = Student.findByLastname("Bar")
    res.shouldNotBe null
    assert (res instanceof Student)
    res.get("lastname").shouldBe "Bar"
  }

  then "it should not find anything", {
    res = Student.findByLastname("Xyz")
    res.shouldBe null
  }
}

scenario "a new model", {
  given "a clean database", {
    cleanup()
  }

  then "it should be a new document", {
    doc = new Article()
    doc.rev.shouldBe null
  }
}

scenario "getting a model with sub-objects", {
  given "a course document", {
    doc = [
      "title" : "Metaphysics 200",
      "questions" : [
        ["q" : "Carve the ___ reality at the ___.",
         "a" : ["beast", "joints"]],
        ["q" : "Who layed the smack down on Leibniz Law?",
         "a" : "Willard Von Orman Quine"]
      ]
    ]

    course = new Course(doc)
    course.save()
  }

  then "it should get the title", {
    course.get("title").shouldBe "Metaphysics 200"
  }
  
  then "it should get the questions", {
    course.get("questions").size().shouldBe 2
    first = course.get("questions")[0]
    first["q"].shouldBe "Carve the ___ reality at the ___."
    first["a"][0].shouldBe "beast"
    first["a"][1].shouldBe "joints"
    course.get("questions")
  }
}

scenario "callback on destroy", {
  then "it should invoke callbacks", {
    art = new Article(["title" : "foo"])
    art.save()
    art.destroy()
    art.bdest.shouldBe 1
    art.adest.shouldBe 1
  }
}

scenario "callback on save", {
  then "it should invoke callbacks", {
    art = new Article(["title" : "bar"])
    art.save()
    art.bsave.shouldBe 1
    art.asave.shouldBe 1
  }
}

scenario "count regular documents", {
  given "two articles", {
    a1 = new Article("title" : "foo", "type" : "article")
    a2 = new Article("title" : "bar", "type" : "article")
    a1.save()
    a2.save()
  }

  given "a database", {
    db = new Server().getDatabase(TESTDB)
  }

  then "it should count two", {
    Article.count().shouldBe 2 
  }
}
