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
