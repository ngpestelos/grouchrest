import grouchrest.PropertyList

before_each "start with an empty property list", {
  plist = new PropertyList()
}

scenario "insert", {
  then "it should accept an attribute and a value", {
    plist.put("hello", "world")
    plist.get("hello").shouldBe "world"
  }

  then "it should overwrite", {
    plist.put("hello", "there")
    plist.get("hello").shouldBe "there"
  }

  then "it should accept null", {
    plist.put("empty", null)
    plist.get("empty").shouldBe null
  }

  then "it should accept Boolean", {
    plist.put("bool", true)
    plist.get("bool").shouldBe true
  }

  then "it should accept a Number", {
    plist.put("int", 1)
    plist.put("decimal", new BigDecimal("2.71828"))
    plist.get("int").shouldBe 1
    plist.get("decimal").shouldBe new BigDecimal("2.71828")
  }
}

scenario "has", {
  then "it should return false if not found", {
    plist.has("blah").shouldBe false
  }

  then "it should return true if found", {
    plist.put("foo", 1)
    plist.has("foo").shouldBe true
  }
}

scenario "remove", {
  then "it should return null if non-existent", {
    plist.remove("abc").shouldBe null
  }

  then "it should return the value", {
    plist.put("abc", new BigDecimal("3.1416"))
    plist.remove("abc").shouldBe new BigDecimal("3.1416")
  }
}
