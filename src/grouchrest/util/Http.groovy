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

    static def put(uri, payload = null) {
        try {
            def client = new DefaultHttpClient()
            def put = new HttpPut(uri)
            if (payload) {
                def entity = new StringEntity(payload)
                put.setEntity(entity)
            }
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

}