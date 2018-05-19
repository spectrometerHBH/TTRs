#-*- coding:utf-8 -*-
from flask import Flask, render_template, request, session, redirect, url_for
import client
import json
from Crypto.Cipher import AES
from Crypto import Random

import base64
import re
from jsontool import *
app = Flask(__name__)

import sys
reload(sys)
sys.setdefaultencoding("utf8")

date_re = re.compile("(\d\d:\d\d)|(xx:xx)$")


def get_station():
    result = client.send("list_station\n")
    result = unicode(result, "utf-8")
    return decode_list_station(result)["station"]

def get_privilege(userid):
    if not userid:
        return 0
    raw_result = client.send("query_profile %s\n"%(userid))
    raw_result = unicode(raw_result, "utf-8")
    result = decode_query_profile(raw_result)
    if (result["success"]):
        return result["privilege"]
    else:
        return 0

message = { 'login' : "成功登录",
            'logout' : "成功退出",
            'signup' : "注册成功",
            'logfail' : "ID或密码错误",
            'modify' : "信息修改成功",
            'modifyfail' : "信息修改失败",
            'buy' : "购票成功",
            "refund" : "退票成功",}

@app.route('/')
def index():
    current_user = session.get('userid','')
    fromWhere = request.args.get("from","")

    return render_template('index.html',
                            admin = get_privilege(current_user),
                            message = message.get(fromWhere, ""),
                            user = current_user)

@app.route('/user/<userid>')
def userinfo(userid="0"):
    current_user = session.get('userid','')
    if current_user != userid and get_privilege(current_user) != 2:
        return render_template("warning.html",
                            admin = get_privilege(current_user),
                            message = "没有权限查看",
                            user = current_user
            )
    fromWhere = request.args.get("from","")
    result = client.query_profile(userid)
    #print result
    if result == "0\n":
        return "Not Found"
    result = unicode(result, "utf-8")
    data = result[:-1].split(" ")
    #print data
    return render_template('userinfo.html',
                            admin = get_privilege(current_user),
                            user = current_user,
                            message = message.get(fromWhere, ""),
                            id=userid, 
                            name=data[0],
                            email=data[1],
                            phone=data[2],
                            privilege=int(data[3]))


@app.route('/login')
def login():
    current_user = session.get('userid','')
    fromWhere = request.args.get("from","")
    if current_user:
        return render_template("warning.html",
                            admin = get_privilege(current_user),
                            message = "您已登录",
                            user = current_user
            )
    return render_template('login.html',
                            admin = get_privilege(current_user),
                            message = message.get(fromWhere, ""),
                            user = current_user)

@app.route('/query')
def query():
    current_user = session.get('userid','')
    fromWhere = request.args.get("from","")

    return render_template('query.html',
                            admin = get_privilege(current_user),
                            message = message.get(fromWhere, ""),
                            user = current_user,
                            station = get_station())

@app.route('/query_train')
def query_train():
    current_user = session.get('userid','')
    fromWhere = request.args.get("from","")
    return render_template('query_train.html',
                            admin = get_privilege(current_user),
                            message = message.get(fromWhere, ""),
                            user = current_user)

@app.route('/query_user')
def query_user():
    current_user = session.get('userid','')
    fromWhere = request.args.get("from","")
    admin = get_privilege(current_user)
    if (admin != 2):
        return render_template("warning.html",
                            admin = get_privilege(current_user),
                            message = "权限不足",
                            user = current_user
            )        
    return render_template('query_user.html',
                            admin = get_privilege(current_user),
                            message = message.get(fromWhere, ""),
                            user = current_user)

@app.route('/query_order')
def query_order():
    current_user = session.get('userid','')
    fromWhere = request.args.get("from","")
    return render_template('query_order.html',
                            admin = get_privilege(current_user),
                            message = message.get(fromWhere, ""),
                            userid = request.args.get("id",""),
                            date = request.args.get("date",""),
                            catalog  = request.args.get("catalog",""),
                            user = current_user)

@app.route('/add_train')
def add_train():
    current_user = session.get('userid','')
    fromWhere = request.args.get("from","")
    return render_template('add_train.html',
                            admin = get_privilege(current_user),
                            message = message.get(fromWhere, ""),
                            user = current_user)

@app.route('/modify_train')
def modify_train():
    current_user = session.get('userid','')
    fromWhere = request.args.get("from","")
    train_id = request.args.get("train_id",'')
    if not train_id:
        return ""
    command = {"type" : "query_train",
               "train_id" : train_id}
    raw_result = client.send(encode_query_train2(command))
    raw_result = unicode(raw_result, "utf-8")
    result = decode_query_train2(raw_result)
    if not result["success"]:
        return render_template("warning.html",
                            admin = get_privilege(current_user),
                            message = "这辆车并不存在",
                            user = current_user)
    stations = []
    for item in result["station"]:
        for t in range(result["pricenum"]):
            item[result["ticket"][t]] = item["ticket"][t]
        item.pop("ticket")
        stations.append(item)
    return render_template('modify_train.html',
                            train_id = train_id,
                            name = result["name"],
                            catalog = result["catalog"],
                            ticket = json.dumps(result["ticket"]),
                            stations = json.dumps(stations),
                            admin = get_privilege(current_user),
                            message = message.get(fromWhere, ""),
                            user = current_user)
@app.route('/debug')
def debug():
    return render_template('debugger.html')

@app.route('/signup')
def signup():
    current_user = session.get('userid','')
    fromWhere = request.args.get("from","")
    if current_user:
        return render_template("warning.html",
                            admin = get_privilege(current_user),
                            message = "您已登录",
                            user = current_user)

    return render_template('signup.html',
                            admin = get_privilege(current_user),
                            message = message.get(fromWhere, ""),
                            user = current_user)

@app.route('/manage_train')
def manage_train():
    current_user = session.get('userid','')
    fromWhere = request.args.get("from","")
    admin = get_privilege(current_user)
    if (admin != 2):
        return render_template("warning.html",
                            admin = get_privilege(current_user),
                            message = "权限不足",
                            user = current_user
            )   
    return render_template('manage_train.html',
                            admin = get_privilege(current_user),
                            message = message.get(fromWhere, ""),
                            user = current_user)

@app.route('/action/login', methods=['POST', 'GET'])
def action_login():
    if request.method == 'POST':
        para = ("userid", "password")
        
        for item in para:
            if not request.form.has_key(item):
                #print item
                return ""
        
        result = client.login(request.form['userid'],request.form['password'])
        #print(result, len(result))
        if result == "1\n":
            session['userid'] = request.form['userid']
            return "1"
        else:
            return "0"
    return "0"

@app.route('/action/signup', methods=['POST', 'GET'])
def action_signup():
    if request.method == 'POST':
        para = ("name", "password", "email", "phone")
        
        for item in para:
            if not request.form.has_key(item):
                return "0"
        print str(request.form)
        result = client.register(
                request.form['name'],
                request.form['password'],
                request.form['email'],
                request.form['phone']
                )
        if result == "-1\n":
            return "0"
        else:
            session['userid'] = result[:-1]
            return "1"
    return "0"

@app.route('/action/modify_profile', methods=['POST', 'GET'])
def action_modify_profile():
    current_user = session.get('userid','')
    if request.method == 'POST':
        para = ("userid", "name", "password", "password2", "email", "phone")
        for item in para:
            if not request.form.has_key(item):
                #print item
                return ""
        print request.form
        userid = request.form['userid']
        if (request.form['password'] != request.form['password2']):
            return redirect('/user/'+userid+'?from=pwdfail')
        if request.form['password']:
            result = client.modify_profile(
                request.form['userid'],
                request.form['name'],
                request.form['password'],
                request.form['email'],
                request.form['phone']
                )
        else:
            result = client.modify_profile2(
                request.form['userid'],
                request.form['name'],
                request.form['email'],
                request.form['phone']
                )
        if result == "0\n":
            return redirect('/user/'+userid+'?from=modifyfail')
        privilege_input = request.form.get("privilege", "off")
        if (privilege_input == "on") != (get_privilege(userid) == 2):
            command = {
                "type":"modify_privilege",
                "id1":current_user,
                "id2":userid,
                "privilege": {"on":2, "off":1}[privilege_input]
            }
            raw_result = client.send(encode_modify_privilege(command))
            raw_result = unicode(raw_result, "utf-8")
            result = decode_modify_privilege(raw_result) 
            if (not result["success"]):
                return redirect('/user/'+userid+'?from=modifyfail')
        return redirect('/user/'+userid+'?from=modify')
    return "invalid login"

@app.route('/action/query_order')
def action_query_order():
    para = ("date", "id", "catalog")
    command = {}
    for item in para:
        value = request.args.get(item, "")
        if value:
            command[item] = value
        else:
            return ""
    current_user = session.get('userid','')
    if current_user != request.args["id"] and get_privilege(current_user) != 2:
        return u"权限不足"
    command["type"] = "query_order"
    raw_result = client.send(encode_query_order(command))
    raw_result = unicode(raw_result, "utf-8")
    result = decode_query_ticket(raw_result)
    return render_template('query_order_result.html',
        userid = request.args["id"],
        data = result)


@app.route('/action/logout')
def action_logout():
    if (not session.has_key('userid')):
        return '0'
    userid = session['userid']
    session.pop('userid', None)
    return '1'

@app.route('/action/query_train')
def action_query_train():
    train_id = request.args.get("train_id",'')
    if not train_id:
        return ""
    command = {"type" : "query_train",
               "train_id" : train_id}
    raw_result = client.send(encode_query_train(command))
    #print "#", raw_result
    raw_result = unicode(raw_result, "utf-8")
    result = decode_query_train(raw_result)
    #print result
    return render_template('query_train_result.html',
        data = result)

@app.route('/action/query_user')
def action_query_user():
    current_user = session.get('userid','')
    if get_privilege(current_user) != 2:
        return u"权限不足"
    userid = request.args.get("id","")
    if not userid:
        return ""
    command = {"type" : "query_profile",
               "id" : userid}
    raw_result = client.send(encode_query_profile(command))
    #print encode_query_profile(command)
    raw_result = unicode(raw_result, "utf-8")
    result = decode_query_profile(raw_result)
    #print result
    if not result["success"]:
        return u"未找到该用户"
    return render_template('query_user_result.html',
        id = userid,
        **result)

@app.route('/action/query', methods=['POST', 'GET'])
def action_query():
    if request.method == 'POST':
        print request.form
        para = ("loc1", "loc2", "date", "catalog","transfer")
        command = {}
        for item in para:
            value = request.form.get(item, "")
            if value:
                command[item] = value
            else :
                return ""
        if request.form["transfer"] == u"true":
            command["type"] = "query_transfer"
            raw_result = client.send(encode_query_transfer(command))
            raw_result = unicode(raw_result, "utf-8")
            result = decode_query_transfer(raw_result)
        else:
            command["type"] = "query_ticket"
            raw_result = client.send(encode_query_ticket(command))
            raw_result = unicode(raw_result, "utf-8")
            result = decode_query_ticket(raw_result)            
        return render_template('query_result.html',
        data = result)
    else:
        return ""

@app.route('/action/buy', methods=['POST', 'GET'])
def action_buy():
    current_user = session.get('userid','')
    if not current_user:
        return render_template("warning.html",
                            admin = get_privilege(current_user),
                            message = "尚未登录",
                            user = current_user)
    if request.method == 'POST':
        para = ("train_id","num","loc1", "loc2", "date", "ticket_kind")
        command = {}
        for item in para:
            value = request.form.get(item, "")
            if value:
                command[item] = value
            else :
                return ""
        command["id"] = current_user
        command["type"] = "buy_ticket"
        #rint "#",encode_buy_ticket(command)
        raw_result = client.send(encode_buy_ticket(command))
        #print "$",raw_result,"$"
        raw_result = unicode(raw_result, "utf-8")
        result = decode_buy_ticket(raw_result)
        return redirect('/query_order?from=buy&id=%s&date=%s&catalog=TZCOGDK'%(current_user, command["date"]))
    else:
        return ""

@app.route('/action/refund', methods=['POST', 'GET'])
def action_refund():
    current_user = session.get('userid','')
    if not current_user:
        return render_template("warning.html",
                            admin = get_privilege(current_user),
                            message = "尚未登录",
                            user = current_user)
    if request.method == 'POST':
        para = ("id","train_id","num","loc1", "loc2", "date", "ticket_kind")
        command = {}
        for item in para:
            value = request.form.get(item, "")
            if value:
                command[item] = value
            else :
                return ""
        command["type"] = "refund_ticket"
        #rint "#",encode_buy_ticket(command)
        raw_result = client.send(encode_refund_ticket(command))
        #print "$",raw_result,"$"
        raw_result = unicode(raw_result, "utf-8")
        result = decode_refund_ticket(raw_result)
        return redirect('/query_order?from=refund&id=%s&date=%s&catalog=TZCOGDK'%(request.form["id"], command["date"]))
    else:
        return ""

@app.route('/action/add_train', methods=['POST', 'GET'])
def action_add_train():
    if request.method == 'POST':
        print str(request.form)
        train_id = request.form.get("train_id", "")
        if not train_id:
            return u"缺少列车ID"
        name = request.form.get("name", "")
        if not name:
            return u"缺少车次名"
        catalog = request.form.get("catalog","")
        if not catalog:
            return u"缺少车次类型"
        station = request.form.get("station", "")
        if not station:
            return u"缺少车站信息"
        ticket = request.form.get("ticket", "")
        tickets = json.loads(ticket)
        stations = json.loads(station)
        station_num  = len(stations)
        if station_num < 2:
            return u"站数不够……"
        
        price_num = len(tickets)
        if price_num < 1:
            return u"票数不够……"

        command = {
            "type" : "add_train",
            "train_id" : train_id,
            "name" : name,
            "catalog" : catalog,
            "stationnum" : station_num,
            "pricenum" : price_num,
            "ticket" : tickets,
            "station" : []
        }

        for item in stations:
            if (not date_re.match(item["timearriv"])) :
                return u"时间格式错误"
            if (not date_re.match(item["timestart"])) :
                return u"时间格式错误"
            if (not date_re.match(item["timestopover"])) :
                return u"时间格式错误"
            item["ticket_num"] = []
            for t in tickets:
                try:
                    item["ticket_num"].append(float(item[t]))
                except ValueError:
                    return u"票价格式错误"


        for item in stations:
            command["station"].append({
                "name" : item["name"],
                "timearriv" : item["timearriv"],
                "timestart" : item["timestart"],
                "timestopover" : item["timestopover"],
                "ticket" : item["ticket_num"]
            })

        print encode_add_train(command)
        raw_result = client.send(encode_add_train(command))
        raw_result = unicode(raw_result, "utf-8")
        result = decode_add_train(raw_result)
        if (result.get("success",False)):
            return u"加车成功"
        else:
            return u"加车失败"
    else:
        return ""

@app.route('/action/modify_train', methods=['POST', 'GET'])
def action_modify_train():
    if request.method == 'POST':
        print str(request.form)
        train_id = request.form.get("train_id", "")
        if not train_id:
            return u"缺少列车ID"
        name = request.form.get("name", "")
        if not name:
            return u"缺少车次名"
        catalog = request.form.get("catalog","")
        if not catalog:
            return u"缺少车次类型"
        station = request.form.get("station", "")
        if not station:
            return u"缺少车站信息"
        ticket = request.form.get("ticket", "")
        tickets = json.loads(ticket)
        stations = json.loads(station)
        station_num  = len(stations)
        if station_num < 2:
            return u"站数不够……"
        
        price_num = len(tickets)
        if price_num < 1:
            return u"票数不够……"

        command = {
            "type" : "add_train",
            "train_id" : train_id,
            "name" : name,
            "catalog" : catalog,
            "stationnum" : station_num,
            "pricenum" : price_num,
            "ticket" : tickets,
            "station" : []
        }

        for item in stations:
            if (not date_re.match(item["timearriv"])) :
                return u"时间格式错误"
            if (not date_re.match(item["timestart"])) :
                return u"时间格式错误"
            if (not date_re.match(item["timestopover"])) :
                return u"时间格式错误"
            item["ticket_num"] = []
            for t in tickets:
                try:
                    item["ticket_num"].append(float(item[t]))
                except ValueError:
                    return u"票价格式错误"


        for item in stations:
            command["station"].append({
                "name" : item["name"],
                "timearriv" : item["timearriv"],
                "timestart" : item["timestart"],
                "timestopover" : item["timestopover"],
                "ticket" : item["ticket_num"]
            })

        raw_result = client.send(encode_modify_train(command))
        raw_result = unicode(raw_result, "utf-8")
        result = decode_modify_train(raw_result)
        if (result.get("success",False)):
            return u"修改成功"
        else:
            return u"修改失败"
    else:
        return ""

@app.route('/action/del_train')
def action_del_train():
    current_user = session.get('userid','')
    if get_privilege(current_user) != 2:
        return render_template("warning.html",
                            admin = get_privilege(current_user),
                            message = "权限不足",
                            user = current_user)
    train_id = request.args.get("train_id","")
    if not train_id:
        return render_template("warning.html",
                            admin = get_privilege(current_user),
                            message = "缺少参数",
                            user = current_user) 
    command = {"type" : "delete_train",
               "train_id" : train_id}
    raw_result = client.send(encode_delete_train(command))
    raw_result = unicode(raw_result, "utf-8")
    result = decode_delete_train(raw_result)
    if (result.get("success",False)):
        return u"删车成功"
    else:
        return u"删车失败"

@app.route('/action/sale_train')
def action_sale_train():
    current_user = session.get('userid','')
    if get_privilege(current_user) != 2:
        return render_template("warning.html",
                            admin = get_privilege(current_user),
                            message = "权限不足",
                            user = current_user)
    train_id = request.args.get("train_id","")
    if not train_id:
        return render_template("warning.html",
                            admin = get_privilege(current_user),
                            message = "缺少参数",
                            user = current_user) 
    command = {"type" : "sale_train",
               "train_id" : train_id}
    raw_result = client.send(encode_sale_train(command))
    raw_result = unicode(raw_result, "utf-8")
    result = decode_sale_train(raw_result)
    if (result.get("success",False)):
        return u"发售成功"
    else:
        return u"发售失败"

@app.route('/action/list_train')
def list_train():
    current_user = session.get('userid','')
    if get_privilege(current_user) != 2:
        return u"权限不足"
    command = {"type" : "list_unsale_train"}
    raw_result = client.send(encode_list_unsale_train(command))
    raw_result = unicode(raw_result, "utf-8")
    result = decode_list_unsale_train(raw_result)
    return render_template('list_train.html',
                    data = result)

func = {"register":(encode_register, decode_register),
        "login":(encode_login, decode_login),
        "query_profile":(encode_query_profile, decode_query_profile),
        "modify_profile":(encode_modify_profile, decode_modify_profile),
        "modify_profile2":(encode_modify_profile2, decode_modify_profile2),
        "modify_privilege":(encode_modify_privilege, decode_modify_privilege),
        "query_ticket":(encode_query_ticket, decode_query_ticket),
        "query_transfer":(encode_query_transfer, decode_query_transfer),
        "buy_ticket":(encode_buy_ticket, decode_buy_ticket),
        "refund_ticket":(encode_refund_ticket, decode_refund_ticket),
        "add_train":(encode_add_train, decode_add_train),
        "modify_train":(encode_modify_train, decode_add_train),
        "sale_train":(encode_sale_train, decode_sale_train),
        "query_train":(encode_query_train, decode_query_train),
        "query_train2":(encode_query_train2, decode_query_train),
        "delete_train":(encode_delete_train, decode_delete_train),
        "query_order":(encode_query_order, decode_query_ticket),
        "list_station":(encode_list_station, decode_list_station),
        "list_unsale_train":(encode_list_unsale_train, decode_list_unsale_train)
        }

BS = 16
pad = lambda s: s + (BS - len(s) % BS) * chr(BS - len(s) % BS) 
unpad = lambda s : s[0:-ord(s[-1])]
key = "tjuacTexIcOzId7p$HTbcDE@BlzkFl71"
def encrypt(raw):
    raw = pad(raw)
    cipher = AES.new(key, AES.MODE_ECB)
    return base64.b64encode(cipher.encrypt(raw))
def decrypt(enc):
    enc = base64.b64decode(enc)
    cipher = AES.new(key, AES.MODE_ECB)
    return unpad(cipher.decrypt(enc))

@app.route('/action/post', methods=['POST', 'GET'])
def action_post():
    
    if request.method == 'POST':
        raw_text = decrypt(request.form.get('input',''))
        #raw_text = unicode(raw_text, "utf-8")
        print raw_text
        try:
            data = json.loads(raw_text)
        except ValueError:
            encrypt("{'success':false}")
        #return str(data)
        #print data
        if (not (isinstance(data, dict) and data.has_key("type"))):
            encrypt("{'success':false}")
        #print data["type"],func.has_key(data["type"])
        if func.has_key(data["type"]):
            #print data, func[data['type']][0](data)
            #return func[data['type']][0](data)
            command = func[data['type']][0](data)
            if command == "":
                return "wrong format"
            print "#quest", command
            result = client.send(command)
            result = unicode(result, "utf-8")
            return encrypt(json.dumps(func[data['type']][1](result)))
        else:
            return encrypt("{'success':false}")
    return encrypt("{'success':false}")

app.secret_key = 'A0Zr98j/3asdfHH!&&mN]LWX/,?RT'


if __name__ == '__main__':
    app.run(host='0.0.0.0')
