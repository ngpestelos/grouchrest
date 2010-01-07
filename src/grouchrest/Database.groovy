package grouchrest

import org.apache.commons.codec.net.URLCodec
import org.apache.commons.lang.time.StopWatch
import org.apache.http.client.HttpResponseException

import org.json.JSONArray
import org.json.JSONObject

class Database {

    def server
    def host
    def name
    def uri

    def getJSONObject = CouchUtils.&getJSONObject
    def getMap = CouchUtils.&getMap
    def getList = CouchUtils.&getList
    def paramify = CouchUtils.&paramifyURL

    def Database(server, name) {
        this.server = server
        this.name = name
        this.host = server.getURI()
        this.uri = "${host}/${name.replaceAll("/", "%2F")}"
    }

    def getURI() {
        uri
    }

    def getInfo() {
        HttpClient.get(getURI())
    }

    // Use with caution
    def delete() {
        HttpClient.delete("${uri}")
    }

    List bulkSave(Map doc) {
        bulkSave([doc])
    }

    List bulkSave(List array) {
        def docs = getJSONObject(["docs" : array]).toString()
        def res = HttpClient.post("${getURI()}/_bulk_docs", docs)
        getList(res)
    }

    Map tempView(view, params = [:]) {
        def keys = params.remove("keys")
        if (keys)
            view = view.plus(["keys" : keys])

        def url = paramify("${getURI()}/_temp_view", params)
        def res = HttpClient.post(url, getJSONObject(view).toString())
        getMap(res)
    }

    Map save(Map doc) {
        if (!doc)
            throw new IllegalArgumentException("Document is required.")

        def json = getJSONObject(doc)
        def id = json.has("_id") ? json.get("_id") : getUUID()
        def res = HttpClient.put("${getURI()}/${id}", json.toString())
        getMap(res)
    }

    Map view(name, params = [:], closure = null) {
        def keys = params.remove("keys")

        name = name.split("/")
        def dname = name[0]
        def vname = name[1]
        def url = paramify("${getURI()}/_design/${dname}/_view/${vname}", params)

        def res
        if (keys)
            res = HttpClient.post(url, getJSONObject(["keys" : keys]).toString())
        else
            res = HttpClient.get(url)

        def map = getMap(res)

        if (closure)
            map["rows"].each { row -> closure(row) }

        return map
    }

    Map get(String id) {
        def res = HttpClient.get("${getURI()}/${id}")
        getMap(res)
    }

    def getDocuments() {
        def res = HttpClient.get("${getURI()}/_all_docs")        
        getMap(res)
    }

    def deleteDoc(Map doc) {        
        def res = HttpClient.delete("${getURI()}/${doc["_id"]}?rev=${doc["_rev"]}")
        getMap(res)
    }

    ////
    //// private methods
    ////

    private def getUUID() {
        def res = HttpClient.get("${host}/_uuids")
        getMap(res)["uuids"][0]
    }
	
}

