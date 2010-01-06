import grouchrest.*

before "setup the database", {
  try { HttpClient.delete("http://127.0.0.1:5984/student_test") } catch (e) { }
  HttpClient.put("http://127.0.0.1:5984/student_test")
}

class Student extends ExtendedDocument {
  static def DB = "student_test"  

  static {
    println "In static block"
    def design = new Design()
    design.database = new Server().getDatabase(DB)
    design.name = DB
    design.viewBy("lastname")
    design.save()
  }

  def Student() {
    super(Student.class)
  }

}

scenario "setup", {
  given "a student", {
    student = new Student()
  }

  then "it should have a default database", {
    student.database.name.shouldBe "student_test"
  }

  then "it should have a design document", {
    student.design.name.shouldBe "student_test"
    student.design.has("views").shouldBe true
  }
}

scenario "unknown static method", {
  then "it should fail", {
    ensureThrows(MissingMethodException) { Student.foo() }
  }
}

scenario "dynamic finder", {
  given "some docs", {
    db = new Server().getDatabase("student_test")
    db.bulkSave([
      ["lastname" : "A"],
      ["lastname" : "B"]
    ])
  }

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
