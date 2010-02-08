package grouchrest

import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.time.StopWatch

import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.entity.StringEntity

import grouchrest.json.*


// Hides the details of talking to commons-httpclient
class HttpClient {    

    static def get(uri, closure = null) {
        request(new HttpGet(uri), closure)        
    }

    static def put(uri, closure = null) {        
        request(new HttpPut(uri), closure)
    }

    static def put(uri, JSONObject payload, closure = null) {
        def httpPut = new HttpPut(uri)
        httpPut.setEntity(new StringEntity(payload.toString()))
        request(httpPut, closure)
    }    

    static def post(uri, JSONObject payload) {
        def httpPost = new HttpPost(uri)
        httpPost.setEntity(new StringEntity(payload.toString()))
        getClient().execute(httpPost, new BasicResponseHandler())                
    }

    static def postWithStreaming(uri, JSONObject payload, closure = null) {
        def httpPost = new HttpPost(uri)
        httpPost.setEntity(new StringEntity(payload.toString()))
        request(httpPost, closure)
    }

    static def delete(uri, closure = null) {
        request(new HttpDelete(uri), closure)
    }    

    ////
    //// Private methods
    ////

    private static def getClient() { return new DefaultHttpClient() }

    /**
     * Sends a HTTP request. Handles response streaming.
     *
     * TODO Make this work with JSONArray
     *
     * @param method HTTP method (HttpDelete, HttpGet, HttpPost, HttpPut)     
     * @param closure callback (invoked for each JSONObject in the response)
     *
     * @return ["status" : <status line>, "response" : <json response>]
     */
    private static def request(method, closure = null) {

        def client = getClient()
        def response = client.execute(method)

        def entity = response.getEntity()
        if (entity != null) {
            def stream = entity.getContent()

            try {
                
                def reader = new BufferedReader(new InputStreamReader(stream))
                return [
                    "status" : response.getStatusLine(),
                    "response" : new JSONObject(new JSONTokener(reader), closure)
                ]
                
            } catch (IOException e) {
                throw e
            } catch (RuntimeException e) {
                method.abort()
                throw e
            }
            
        }
    }
	
}