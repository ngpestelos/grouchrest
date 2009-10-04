package grouchrest.util

import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.entity.StringEntity

import grouchrest.util.GrouchUtils

import org.json.JSONObject

class Http {

    static def toMap = GrouchUtils.&toMap

    static def get(uri) {
        try {
            def client = new DefaultHttpClient()
            def get = new HttpGet(uri)
            def resp = client.execute(get, new BasicResponseHandler())
            return new JSONObject(resp)
        } catch (e) {
            e.printStackTrace()
            throw new Exception("Error while sending a GET request ${uri}")
        }
    }

    static def put(uri, payload) {
        try {
            def client = new DefaultHttpClient()
            def put = new HttpPut(uri)
            def entity = new StringEntity(payload)
            put.setEntity(entity)
            def resp = client.execute(put)
            def status = resp.getStatusLine()
            def map = ["reason" : status.getReasonPhrase(),
                       "code" : status.getStatusCode(),
                       "protocol" : status.getProtocolVersion()]
            return map
        } catch (e) {
            e.printStackTrace()
            throw new Exception("Error while sending a PUT request ${uri}")
        }
    }

    static def put(uri) {
        try {
            def client = new DefaultHttpClient()
            def put = new HttpPut(uri)
            def resp = client.execute(put)
            def status = resp.getStatusLine()
            def map = ["reason" : status.getReasonPhrase(),
                       "code" : status.getStatusCode(),
                       "protocol" : status.getProtocolVersion()]
            return map
        } catch (e) {
            throw new Exception("Error while sending a PUT request ${uri}")
        }
    }

    static def delete(uri) {
        try {
            def client = new DefaultHttpClient()
            def delete = new HttpDelete(uri)
            def resp = client.execute(delete)
            def status = resp.getStatusLine()
            def map = ["reason" : status.getReasonPhrase(),
                       "code" : status.getStatusCode(),
                       "protocol" : status.getProtocolVersion()]
            return map
        } catch (e) {
            throw new Exception("Error while sending a DELETE request ${uri}")
        }
    }

    static def post(uri, payload) {
        try {
            def client = new DefaultHttpClient()
            def put = new HttpPost(uri)
            def entity = new StringEntity(payload)
            put.setEntity(entity)
            def resp = client.execute(put)
            def respEntity = resp.getEntity()
            def status = resp.getStatusLine()
            def map = ["reason" : status.getReasonPhrase(),
                       "code" : status.getStatusCode(),
                       "protocol" : status.getProtocolVersion(),
                       "payload" : new JSONObject(respEntity.getContent().text)]
            return map
        } catch (e) {
            throw new Exception("Error while sending a POST request ${uri}")
        }
    }

    static void main(args) {
        def d = [:]
        d["map"] = "function(doc) { if (doc.title) emit(doc.title, null); }"
        def doc = new JSONObject(d).toString()
        println Http.post("http://127.0.0.1:5984/nblog_development/_temp_view", doc)
    }

}