package grouchrest.json

import grouchrest.PropertyList

/*
Copyright (c) 2002 JSON.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

The Software shall be used for Good, not Evil.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/


class JSONObject extends PropertyList {    

    final static def NULL = new NullObject()

    def JSONObject(String json, closure = null) throws JSONException {
        this(new JSONTokener(json), closure)
    }

    def JSONObject(JSONTokener x, closure = null) throws JSONException {
        super()
        char c
        String key        

        def callback = { }
        if (closure)
            callback = { try { closure() } catch (e) { } }

        if (x.nextClean() != '{') {
            throw x.syntaxError("A JSONObject text must begin with '{'")
        }

        // TODO where to call the closure?

        for(;;) {

            c = x.nextClean()
            //println c
            switch (c) {
                case 0:
                    throw x.syntaxError("A JSONObject text must end with '}'")
                case '}':
                    callback()
                    return
                default:
                    x.back()
                    key = x.nextValue().toString()
            }

            /*
             * The key is followed by ':'. We will also tolerate '=' or '=>'.
             */

            c = x.nextClean()
            if (c == '=') {
                if (x.next() != '>') {
                    x.back()
                }
            } else if (c != ':') {
                throw x.syntaxError("Expected a ':' after a key")
            }


            // The value of the key gets overwritten
            put(key, x.nextValue())

            /*
             * Pairs are separated by ','. We will also tolerate ';'.
             */

            switch (x.nextClean()) {
                case ';':
                case ',':
                    if (x.nextClean() == '}') {
                        callback()
                        return;
                    }
                    x.back();
                    break;
                case '}':
                    callback()
                    return
                default:
                    throw x.syntaxError("Expected a ',' or '}'")
            }
        }
    }

    /**
     * Try to convert a string into a number, boolean, or null. If the string
     * can't be converted, return the string.
     * @param s A String.
     * @return A simple JSON value.
     */
    static def stringToValue(String s) {
        if (s.equals(""))
            return s
        if (s.equalsIgnoreCase("true"))
            return Boolean.TRUE
        if (s.equalsIgnoreCase("false"))
            return Boolean.FALSE
        if (s.equalsIgnoreCase("null"))
            return JSONObject.NULL // as opposed to JSONObject.NULL

        /*
         * If it might be a number, try converting it. We support the 0- and 0x-
         * conventions. If a number cannot be produced, then the value will just
         * be a string. Note that the 0-, 0x-, plus, and implied string
         * conventions are non-standard. A JSON parser is free to accept
         * non-JSON forms as long as it accepts all correct JSON forms.
         */

        char b = s.charAt(0)
        if ((b >= '0' && b <= '9') || b == '.' || b == '-' || b == '+') {
            if (b == '0') {
                if (s.length() > 2 &&
                    (s.charAt(1) == 'x' || s.charAt(1) == 'X')) {
                    try {
                        return new Integer(Integer.parseInt(s.substring(2), 16))
                    } catch (e) {
                        /* Ignore the error */
                    }
                } else {
                    try {
                        return new Integer(Integer.parseInt(s, 8))
                    } catch (Exception e) {
                        /* Ignore the error */
                    }
                }
            }

            try {
                if (s.indexOf('.') > -1 || s.indexOf('e') > -1 || s.indexOf('E') > -1) {
                    return Double.valueOf(s);
                } else {
                    Long myLong = new Long(s);
                    if (myLong.longValue() == myLong.intValue()) {
                        return new Integer(myLong.intValue());
                    } else {
                        return myLong;
                    }
                }
            } catch (Exception f) {
                /* Ignore the error */
            }
        }

        return s
    }

    int size() {
        getInternalMap().size()
    }

    Map getInternalMap() {
        return getAttributes()
    }

    String toString() {
        getInternalMap().toString()
    }

}