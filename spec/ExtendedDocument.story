import grouchrest.*

before_each "database", {
  try { HttpClient.delete("http://127.0.0.1:5984/student_test") } catch (e) { }
  HttpClient.put("http://127.0.0.1:5984/student_test")
}

class Student extends ExtendedDocument {
  static def DB = "student_test"  

  static {
    def design = new Design()
    design.database = new Server().getDatabase(DB)
    design.name = DB
    design.viewBy("lastname")
    design.save()
    ExtendedDocument.setupDynamicFinders(Student.class)
  }

  def Student() {
    super(DB)
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

scenario "dynamic finder", {
  then "it should find by lastname", {
    Student.byLastname()
  }
}
