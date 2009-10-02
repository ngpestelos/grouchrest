package grouchrest.util

import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPut
import org.apache.http.client.methods.HttpDelete

import grouchrest.util.GrouchUtils

// TODO Content-Type?
class Http {

    static def toMap = GrouchUtils.&toMap

    static def get(uri) {
        def httpclient = new DefaultHttpClient()
        def httpget = new HttpGet(uri)
        def handler = new BasicResponseHandler()
        def resp = httpclient.execute(httpget, handler)
        toMap(resp)
    }

    static def put(uri) {
        def httpclient = new DefaultHttpClient()
        def httpput = new HttpPut(uri)
        def handler = new BasicResponseHandler()
        def resp = httpclient.execute(httpput, handler)
        toMap(resp)
    }

    static def delete(uri) {
        def httpclient = new DefaultHttpClient()
        def httpdelete = new HttpDelete(uri)
        def handler = new BasicResponseHandler()
        def resp = httpclient.execute(httpdelete, handler)
        toMap(resp)
    }

}