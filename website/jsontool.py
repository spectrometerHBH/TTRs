#-*- coding:utf-8 -*-
def encode_login(data):
    para = ("id", "password")
    for item in para:
        if not data.has_key(item):
            return ""
    command = "login {id} {password}\n\0".format(**data)
    return command

def decode_login(data):
    if data == "1\n\0":
        return {"success" : True}
    else:
        return {"success" : False}

def encode_register(data):
    para = ("name", "password", "email", "phone")
    for item in para:
        if not data.has_key(item):
            return ""
    command = "register {name} {password} {email} {phone}\n\0".format(**data)
    return command

def decode_register(data):
    result = {}
    if (data == "0\n\0"):
        result["success"] = False
    else:
        result["success"] = True
        result["id"] = data[:-2]
    return result

def encode_query_profile(data):
    para = ("id",)
    for item in para:
        if not data.has_key(item):
            return ""
    command = "query_profile {id}\n\0".format(**data)
    return command

def decode_query_profile(data):
    result={}
    if data == "0\n\0":
        result["success"] = False
    else:
        result["success"] = True
        info = data[:-2].split(' ')
        result["name"]  = info[0]
        result["email"] = info[1]
        result["phone"] = info[2]
    return result

def encode_modify_profile(data):
    para = ("id", "name", "password", "email", "phone")
    for item in para:
        if not data.has_key(item):
            return ""
    command = "modify_profile {id} {name} {password} {email} {phone}\n\0".format(**data)
    return command

def decode_modify_profile(data):
    result={}
    if data == "0\n\0":
        result["success"] = False
    else:
        result["success"] = True
    return result

def encode_modify_privilege(data):
    para = ("id1", "id2", "privilege")
    for item in para:
        if not data.has_key(item):
            return ""
    command = "modify_privilege {id1} {id2} {privilege} \n\0".format(**data)
    return command

def decode_modify_privilege(data):
    result={}
    if data == "0\n\0":
        result["success"] = False
    else:
        result["success"] = True
    return result
