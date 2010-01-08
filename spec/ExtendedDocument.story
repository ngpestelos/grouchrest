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

scenario "unknown finder", {
  then "it should fail", {
    ensureThrows(MissingMethodException) { Student.findByFoo() }
  }
}

scenario "find by lastname", {
  then "it should work", {
    res = Student.findByLastname()
    res["total_rows"].shouldBe 2
  }

  then "it should include docs", {
    res = Student.findByLastname("include_docs" : true)
    res["total_rows"].shouldBe 2
    res["rows"][0]["key"].shouldBe "A"
  }

  then "it should find by key", {
    res = Student.findByLastname("key" : "A")
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
