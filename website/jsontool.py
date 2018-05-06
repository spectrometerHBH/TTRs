#-*- coding:utf-8 -*-
import json

def register(data):
    para = ("name", "password", "email", "phone")
    for item in para:
        if not data.has_key(item):
            return ""
    result = client.register(data["name"],
            data["password"],
            data["email"],
            data["phone"])
    json_obj={}
    if result == "0\n\0":
        json_obj["success"] = False
    else:
        json_obj["success"] = True
        json_obj["id"] = result[:-2]
    return json.dumps(json_obj)

def json_login(data):
    para = ("id", "password")
    for item in para:
        if not data.has_key(item):
            return ""
    result = client.login(data["id"],
            data["password"],)
    json_obj={}
    if result == "0\n\0":
        json_obj["success"] = False
    else:
        json_obj["success"] = True
    return json.dumps(json_obj)

def json_query_profile(data):
    para = ("id",)
    #print data,data.has_key("id")
    for item in para:
        if not data.has_key(item):
            print item
            return ""
    #print "here"
    result = client.query_profile(data["id"])
    json_obj={}
    if result == "0\n\0":
        json_obj["success"] = False
    else:
        json_obj["success"] = True
        info = result[:-2].split(' ')
        json_obj["name"]  = info[0]
        json_obj["email"] = info[1]
        json_obj["phone"] = info[2]
    return json.dumps(json_obj)

def json_modify_profile(data):
    para = ("id", "name", "password", "email", "phone")
    for item in para:
        if not data.has_key(item):
            return ""
    result = client.modify_profile(data["id"],
            data["name"],
            data["password"],
            data["email"],
            data["phone"])
    json_obj={}
    if result == "0\n\0":
        json_obj["success"] = False
    else:
        json_obj["success"] = True
    return json.dumps(json_obj)

def json_modify_privilege(data):
    para = ("id1", "id2", "privilege")
    for item in para:
        if not data.has_key(item):
            return ""
    result = client.modify_privilege(data["id1"],
            data["id2"],
            data["privilege"])
    json_obj={}
    if result == "0\n\0":
        json_obj["success"] = False
    else:
        json_obj["success"] = True
    return json.dumps(json_obj)

@app.route('/action/post', methods=['POST', 'GET'])
def action_post():
    if request.method == 'POST':
        raw_text = request.form.get('input','')
        try:
            data = json.loads(raw_text)
        except ValueError:
            return ""
        #return str(data)
        if (not (isinstance(data, dict) and data.has_key("type"))):
            return ""
        func = {"register":json_register,
                "login":json_login,
                "query_profile":json_query_profile,
                "modify_profile":json_modify_profile,
                "modify_privilege":json_modify_privilege
                }
        #print data["type"],func.has_key(data["type"])
        if func.has_key(data["type"]):
            return func[data["type"]](data)
        else:
            return ""
    return ""

app.secret_key = 'A0Zr98j/3asdfHH!&&mN]LWX/,?RT'


if __name__ == '__main__':
    app.run(host='0.0.0.0',debug=True)
