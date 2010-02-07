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
        
        def client = getClient()
        def httpget = new HttpGet(uri)
        def response = client.execute(httpget)
        def map = ["status" : response.getStatusLine()]

        def entity = response.getEntity()
        if (entity != null) {
            def stream = entity.getContent()

            try {                

                def reader = new BufferedReader(
                    new InputStreamReader(stream))                

                map["json"] = new JSONObject(new JSONTokener(reader), closure)
                return map
                
            } catch (IOException e) {
                throw e
            } catch (RuntimeException e) {
                httpget.abort()
                throw e
            }
        }
    }

    static def put(uri) {
        getClient().execute(new HttpPut(uri), new BasicResponseHandler())
    }

    static def put(uri, String payload) {
        def httpPut = new HttpPut(uri)
        httpPut.setEntity(new StringEntity(payload))
        getClient().execute(httpPut, new BasicResponseHandler())
    }

    static def post(uri, String payload) {        
        def httpPost = new HttpPost(uri)
        httpPost.setEntity(new StringEntity(payload))        
        getClient().execute(httpPost, new BasicResponseHandler())
    }

    static def delete(uri) {
        getClient().execute(new HttpDelete(uri), new BasicResponseHandler())
    }

    static void main(args) {
        get("http://127.0.0.1:5984/omgwtfbbq")
    }

    ////
    //// Private methods
    ////

    static def getClient() { return new DefaultHttpClient() }
	
}