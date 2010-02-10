import grouchrest.json.*

scenario "null literal", {
  then "it should return null objects", {
    json = new JSONObject(new JSONTokener("{\"null\": \"null\"}"))
    json.size().shouldBe 1 // [null : null]
  }
}

scenario "basic values", {
  then "it should return an Integer", {
    json = new JSONObject("{\"key\" : 1}")
    json.get("key").shouldBe 1
  }

  then "it should return a Double", {
    json = new JSONObject("{\"key\" : 1.0}")
    json.get("key").shouldBe 1.0
  }

  then "it should return a String", {
    json = new JSONObject("{\"key\" : \"foo\"}")
    
    try { json.get("key") } catch (e) { e.printStackTrace() }
  }

  then "it should accept zero", {
    json = new JSONObject("{\"price\" : 0}")
    json.get("price").shouldBe 0
  }
}

scenario "to string", {
  then "it should print zero", {
    json = new JSONObject("{\"price\" : 0}")
    json.toString().shouldBe '{"price":0}'
  }
  then "it should print negative", {
    json = new JSONObject("{\"temp\" : -14}")
    json.toString().shouldBe '{"temp":-14}'
  }
  then "it should strip trailing zeros", {
    json = new JSONObject("{\"price\" : 0.0}")
    json.toString().shouldBe '{"price":0}'
  }
  then "it should print positive", {
    json = new JSONObject("{\"temp\" : 14}")
    json.toString().shouldBe '{"temp":14}'
  }
}

scenario "pairs", {
  then "it should get two values", {
    json = new JSONObject("{\"foo\" : 1, \"bar\" : 2}")
    json.size().shouldBe 2
  }  
}

scenario "basic lists", {
  then "it should get a JSONArray as its value", {
    json = new JSONObject("{\"list\" : [1,2,3]}")
    json.get("list").size().shouldBe 3
  }
}

scenario "callback", {
  then "it should call the callback only once", {
    x = null 
    cb = { x = it }
    json = new JSONObject("{\"key\" : 1}", cb)
    assert(x.size() == 1)
    x.get("key").shouldBe 1
  }
}

scenario "takes a map", {
  then "it should create a JSONObject", {
    json = new JSONObject(["key" : 1])
    json.get("key").shouldBe 1
  }
}
