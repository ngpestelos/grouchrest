package grouchrest

import org.apache.commons.codec.net.URLCodec
import org.apache.commons.lang.time.StopWatch
import org.apache.http.client.HttpResponseException

import grouchrest.json.*

class Database {

    def server
    def host
    def name
    def uri
    def bulkSaveCache = []
    def BULK_LIMIT = 100

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

    /**
     * POST an array of documents to CouchDB.
     * Generate UUIDs if there are documents with missing UUIDs
     *
     * If called with no arguments, saves from the cache
     */
    List bulkSave(List docs = null, use_uuids = true) {                
        if (!docs) {
            docs = bulkSaveCache
            bulkSaveCache = []
        }

        if (use_uuids) {
            def parts = docs.split { it["_id"] }
            def noids = parts[1]
            def ids = getUUID(noids.size())
            noids.eachWithIndex { doc, index ->
                doc["_id"] = ids[index]
            }
        }
        
        def json = new JSONObject(["docs" : docs])
        def res = HttpClient.post("${getURI()}/_bulk_docs", json)        
        def jsonArray = new JSONArray(res)
        getList(jsonArray)
    }

    Map tempView(view, params = [:]) {
        def keys = params.remove("keys")
        if (keys)
            view = view.plus(["keys" : keys])

        def url = paramify("${getURI()}/_temp_view", params)
        def res = HttpClient.post(url, new JSONObject(view))        
        getMap(res)        
    }

    Map save(Map doc, bulk = false) {
        if (!doc)
            throw new IllegalArgumentException("Document is required.")

        def getID = { doc["_id"] ?: getUUID()[0] }

        if (bulk) {
            bulkSaveCache << doc // buffer
            if (bulkSaveCache.size() >= BULK_LIMIT) {
                bulkSave()
            }
            return ["ok" : true] // return expects a Map
        } else if (!bulk && bulkSaveCache.size() > 0) {
            bulkSave()
        }

        def res = HttpClient.put("${getURI()}/${getID()}", new JSONObject(doc))
        //println res["status"]

        if (!(res["status"] =~ /201/))
            throw new Exception(res["status"].toString())
            
        return getMap(res["response"])
    }

    Map view(name, params = [:], closure = null) {
        def keys = params.remove("keys")

        name = name.split("/")
        def dname = name[0]
        def vname = name[1]
        def uri = paramify("${getURI()}/_design/${dname}/_view/${vname}", params)

        def res
        if (keys)
            res = HttpClient.postWithStreaming(uri, new JSONObject(["keys" : keys]), closure)
        else
            res = HttpClient.get(uri, closure)

        validateOK(res)

        return getMap(res["response"])        
    }

    Map get(String id) {
        def res = HttpClient.get("${getURI()}/${id}")

        if (!(res["status"] =~ /200/))
            throw new Exception(res["status"].toString())

        getMap(res["response"])
    }

    def getDocuments() {
        def res = HttpClient.get("${getURI()}/_all_docs")

        validateOK(res)

        getMap(res["response"])
    }

    def deleteDoc(doc) {
        if (doc instanceof Document)
            doc = doc.getAttributes()

        def res = HttpClient.delete("${getURI()}/${doc["_id"]}?rev=${doc["_rev"]}")

        validateOK(res)
        
        getMap(res["response"])
    }

    def exists() {
        def res = HttpClient.get("${getURI()}")

        if (res["status"] =~ /200/)
            return true
        else
            return false
    }

    ////
    //// private methods
    ////

    private def getUUID(count = null) {
        def uri = "${host}/_uuids"
        if (count && count > 0)
            uri = "${uri}?count=${count}"

        def res = HttpClient.get(uri)
        def json = res["response"]
        json.get("uuids")
    }

    private def validateOK(res) {
        if (!(res["status"] =~ /200/))
            throw new Exception(res["status"].toString())
    }
	
}

