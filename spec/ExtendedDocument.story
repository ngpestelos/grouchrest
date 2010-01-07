import grouchrest.*
import grouchrest.fixtures.*

before "setup the database", {
  TESTDB = "grouchrest_test"
  try { HttpClient.delete("http://127.0.0.1:5984/${TESTDB}") } catch (e) { }
  HttpClient.put("http://127.0.0.1:5984/${TESTDB}")
  db = new Server().getDatabase(TESTDB)
  db.bulkSave([
    ["lastname" : "A"],
    ["lastname" : "B"]
  ])
}

scenario "setup", {
  given "a student", {
    try {
      student = new Student()
    } catch (e) { e.printStackTrace() }
  }

  then "it should have a default database", {
    student.database.name.shouldBe "grouchrest_test"
  }

  then "it should have a design document", {
    student.design.name.shouldBe "grouchrest_test"
    student.design.has("views").shouldBe true
  }
}

scenario "unknown static method", {
  then "it should fail", {
    ensureThrows(MissingMethodException) { Student.foo() }
  }
}

scenario "dynamic finder", {
  then "it should find by lastname", {
    res = Student.byLastname()
    res["total_rows"].shouldBe 2
    res["rows"][0]["key"].shouldBe "A"
  }

  then "it should fail for junk views", {
    ensureThrows(MissingMethodException) { Student.byFirstname() }
  }

  then "it should include docs", {
    res = Student.byLastname(["include_docs" : true])
    res["total_rows"].shouldBe 2
    res["rows"][0]["doc"].shouldNotBe null
  }

  then "it should find by key", {
    res = Student.byLastname(["key" : "A"])
    res["total_rows"].shouldBe 2
    res["rows"].size().shouldBe 1
    res["rows"][0]["key"].shouldBe "A"
  }
}

scenario "a new model", {
  then "it should be a new document", {
    doc = new Article()
    doc.rev.shouldBe null
  }
}

scenario "getting a model", {
  given "an article", {
    art = new Article()
    art.put("title", "All About Getting")
    art.save()
  }

  then "it should load and instantiate it", {
    foundart = Article.get(art.id)
    (foundart instanceof Article).shouldBe true
    foundart.get("title").shouldBe "All About Getting"
    foundart.id.shouldNotBe null
    foundart.rev.shouldNotBe null
  }

  then "it should fail if there are no args", {
    ensureThrows(MissingMethodException) { Article.get() }
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
    try {
      art.save()
    } catch (e) { e.printStackTrace() }
  }

}

/*
scenario "saving a model with a unique_id configured", {
  given "an article", {
    art = new Article()
    try { 
      old = Article.get("this-is-the-title")
      db.deleteDoc(old)
    } catch (e) { }
    
  }

  then "it should be a new document", {
    art.get("title").shouldBe null
    art.id.shouldBe null
    art.rev.shouldBe null
  }

  then "it should require the title", {
    ensureThrows(IllegalStateException) { art.save() }
    //try { art.save() } catch (e) { e.printStackTrace() }
  }
}*/
