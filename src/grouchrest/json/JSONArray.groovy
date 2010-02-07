package grouchrest.json

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

class JSONArray {

    def list    

    def JSONArray(String json) throws JSONException {
        this(new JSONTokener(json))
    }

    def JSONArray(JSONTokener x) throws JSONException {
        list = []
        char c = x.nextClean()
        char q
        if (c == '[') {
            q = ']'
        } else if (c == '(') {
            q = ')'
        } else {
            throw x.syntaxError("A JSONArray text must start with '['")
        }

        if (x.nextClean() == ']') {
            return
        }

        x.back()
        for(;;) {
            if (x.nextClean() == ',') {
                x.back()
                list << null
            } else {
                x.back()
                list << x.nextValue()
            }

            c = x.nextClean()
            switch (c) {
                case ';':
                case ',':
                    if (x.nextClean() == ']') {
                        return
                    }
                    x.back()
                    break
                case ']':
                case ')':
                    if (q != c) {
                        throw x.syntaxError("Expected a '" + new Character(q) + "'")
                    }
                    return
                default:
                    throw x.syntaxError("Expected a ',' or ']'")
            }
        }
    }

    List getInternalList() {
        return list
    }

    int size() {
        return list.size()
    }
	
}

