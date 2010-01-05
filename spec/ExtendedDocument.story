import grouchrest.*

class Student extends ExtendedDocument {
  
  def Student() {
    super(Student.class)
    useDatabase (new Server().getDatabase("student_test"))
  }

}

class Teacher extends ExtendedDocument {

  def Teacher() {
    super(Teacher.class)
    useDatabase (new Server().getDatabase("teacher_test"))
  }

}

scenario "use database", {
  given "a student and a teacher", {
    s = new Student()
    t = new Teacher()
  }  

  then "it should use different databases", {
    s.database.name.shouldBe "student_test"
    //t.database.name.shouldBe "teacher_test"
  }
}
