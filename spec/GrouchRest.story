import grouchrest.*
import org.json.JSONArray
import org.json.JSONObject

scenario "get list", {
  then "it should return itself", {
    def json = new JSONArray()
    json.put("a")
    json.put(2)
    (CouchUtils.getList(json) instanceof List).shouldBe true
  }

  then "JSONObject => Map", {
    def json = new JSONArray()
    json.put("a")
    json.put(2)
    def dict = new JSONObject()
    dict.put("more", "value")
    json.put(dict)
    res = CouchUtils.getList(json)
    (res[2] instanceof Map).shouldBe true
  }
}
