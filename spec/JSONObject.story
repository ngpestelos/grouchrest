import grouchrest.json.*

scenario "null literal", {
  then "it should return null objects", {
    input = "{\"null\": \"null\"}"
    json = new JSONObject(input)
    json.size().shouldBe 1 // [null : null]
  }
}

scenario "basic values", {
  then "it should return an Integer", {
    input = "{\"key\" : 1}"
    json = new JSONObject(input)
    json.get("key").shouldBe 1
  }

  then "it should return a Double", {
    input = "{\"key\" : 1.0}"
    json = new JSONObject(input)
    json.get("key").shouldBe 1.0
  }

  then "it should return a String", {
    input = "{\"key\" : \"foo\"}"
    json = new JSONObject(input)
    json.get("key").shouldBe "foo"
  }

  then "it should accept zero", {
    input = "{\"price\" : 0}"
    json = new JSONObject(input)
    json.get("price").shouldBe 0
  }
}

scenario "to string", {
  then "it should print zero", {
    input = "{\"price\":0}"
    json = new JSONObject(input)
    json.toString().shouldBe input 
  }

  then "it should print negative", {
    input = "{\"temp\":-14}"
    json = new JSONObject(input)
    json.toString().shouldBe input 
  }

  then "it should strip trailing zeros", {
    input = "{\"price\":0.0}"
    json = new JSONObject(input)
    json.toString().shouldBe "{\"price\":0}"
  }

  then "it should print decimal values", {
    input = "{\"price\":3.5}"
    json = new JSONObject(input)
    json.toString().shouldBe input
  }

  then "it should print positive", {
    input = "{\"temp\":14}"
    json = new JSONObject(input)
    json.toString().shouldBe input 
  }

  then "it should print quoted strings", {
    input = "{\"name\":\"foo\"}"
    json = new JSONObject(input)
    json.toString().shouldBe input
  }
}

scenario "pairs", {
  then "it should get two values", {
    json = new JSONObject("{\"foo\" : 1, \"bar\" : 2}")
    json.size().shouldBe 2
    json.get("foo").shouldBe 1
    json.get("bar").shouldBe 2
  }  
}

scenario "basic lists", {
  then "it should get a JSONArray as its value", {
    json = new JSONObject("{\"list\" : [1,2,3]}")
    json.get("list").size().shouldBe 3
  }
}

scenario "closure", {
  then "it should call the closure", {
    foo = null
    closure = { foo = it }
    input = "{\"key\" : 1}"
    json = new JSONObject(input, closure)
    foo.get("key").shouldBe 1
  }
}

scenario "takes a map", {
  then "it should create a JSONObject", {
    json = new JSONObject(["key" : 1])
    json.get("key").shouldBe 1
  }
}

scenario "map with closure", {
  then "it should call the closure", {
    foo = null
    closure = { foo = it }
    json = new JSONObject(["key" : 1], closure)
    foo.get("key").shouldBe 1
  }
}
