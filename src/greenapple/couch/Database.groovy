package greenapple.couch

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
	
}

