#-*- coding:utf-8 -*-
from flask import Flask, render_template, request, session, redirect, url_for
import client
import json
from jsontool import *
app = Flask(__name__)

import sys
reload(sys)
sys.setdefaultencoding("utf8")


message = { 'login' : "Successfully login",
            'logout' : "Successfully logout",
            'signup' : "Successfully signup",
            'logfail' : "Wrong id or password",
            'modify' : "Successfully modify the profile",
            'modifyfail' : "Fail to modify the profile",
            'pwdfail' : "Repeat password has to match with the password"}

@app.route('/')
def index():
    current_user = session.get('userid','')
    fromWhere = request.args.get("from","")

    return render_template('index.html',
                            message = message.get(fromWhere, ""),
                            user = current_user)

@app.route('/user/<userid>')
def userinfo(userid="0"):
    current_user = session.get('userid','')
    fromWhere = request.args.get("from","")
    result = client.query_profile(userid)
    #print result
    if result == "0\n":
        return "Not Found"
    result = unicode(result, "utf-8")
    data = result[:-1].split(" ")
    #print data
    return render_template('userinfo.html',
                            user = current_user,
                            message = message.get(fromWhere, ""),
                            id=userid, 
                            name=data[0],
                            email=data[1],
                            phone=data[2])

@app.route('/login')
def login():
    current_user = session.get('userid','')
    fromWhere = request.args.get("from","")
    if current_user:
        return render_template("warning.html",
                            message = "You have logged in.",
                            user = current_user
            )
    return render_template('login.html',
                            message = message.get(fromWhere, ""),
                            user = current_user)

@app.route('/query')
def query():
    current_user = session.get('userid','')
    fromWhere = request.args.get("from","")

    return render_template('query.html',
                            message = message.get(fromWhere, ""),
                            user = current_user)

@app.route('/query_train')
def query_train():
    current_user = session.get('userid','')
    fromWhere = request.args.get("from","")

    return render_template('query_train.html',
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
                            message = "You have logged in.",
                            user = current_user)
    return render_template('signup.html',
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
            return redirect('/?from=login')
        else:
            return redirect('/login?from=logfail')
    return "invalid login"

@app.route('/action/signup', methods=['POST', 'GET'])
def action_signup():
    if request.method == 'POST':
        para = ("name", "password", "password2", "email", "phone")
        
        for item in para:
            if not request.form.has_key(item):
                #print item
                return ""
        if (request.form['password'] != request.form['password2']):
            return redirect('/signup?from=pwdfail')
        result = client.register(
                request.form['name'],
                request.form['password'],
                request.form['email'],
                request.form['phone']
                )
        if result == "-1\n":
            return redirect('/')
        else:
            session['userid'] = result[:-1]
            return redirect('/?from=signup')
    return "invalid signup"

@app.route('/action/modify_profile', methods=['POST', 'GET'])
def action_modify_profile():
    if request.method == 'POST':
        para = ("userid", "name", "password", "password2", "email", "phone")
        for item in para:
            if not request.form.has_key(item):
                #print item
                return ""
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
        if result == "1\n":
            return redirect('/user/'+userid+'?from=modify')
        else:
            return redirect('/user/'+userid+'?from=modifyfail')
    return "invalid login"

@app.route('/action/logout')
def action_logout():
    if (not session.has_key('userid')):
        return render_template("warning.html",
                            message = "You have not logged in yet.",
                            user = current_user)
    userid = session['userid']
    session.pop('userid', None)
    return redirect('/?from=logout')

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

@app.route('/action/query', methods=['POST', 'GET'])
def action_query():
    if request.method == 'POST':
        print request.form
        para = ("loc1", "loc2", "date", "catalog")
        command = {}
        for item in para:
            value = request.form.get(item, "")
            if value:
                command[item] = value
            else :
                return ""
        command["type"] = "query_ticket"
        #print "#",encode_query_ticket(command)
        raw_result = client.send(encode_query_ticket(command))
        print raw_result
        raw_result = unicode(raw_result, "utf-8")
        result = decode_query_ticket(raw_result)
        return render_template('query_result.html',
        data = result)
    else:
        return ""

func = {"register":(encode_register, decode_register),
        "login":(encode_login, decode_login),
        "query_profile":(encode_query_profile, decode_query_profile),
        "modify_profile":(encode_modify_profile, decode_modify_profile),
        "modify_privilege":(encode_modify_privilege, decode_modify_privilege),
        "query_ticket":(encode_query_ticket, decode_query_ticket),
        "buy_ticket":(encode_buy_ticket, decode_buy_ticket),
        "refund_ticket":(encode_refund_ticket, decode_refund_ticket),
        "add_train":(encode_add_train, decode_add_train),
        "sale_train":(encode_sale_train, decode_sale_train),
        "query_train":(encode_query_train, decode_query_train),
        "delete_train":(encode_delete_train, decode_delete_train),
        "query_order":(encode_query_order, decode_query_ticket)
        }

@app.route('/action/post', methods=['POST', 'GET'])
def action_post():
    if request.method == 'POST':
        raw_text = request.form.get('input','')
        #raw_text = unicode(raw_text, "utf-8")
        try:
            data = json.loads(raw_text)
        except ValueError:
            return "not a valid json"
        #return str(data)
        #print data
        if (not (isinstance(data, dict) and data.has_key("type"))):
            return "not a valid json or type is not defined"
        #print data["type"],func.has_key(data["type"])
        if func.has_key(data["type"]):
            #print data, func[data['type']][0](data)
            #return func[data['type']][0](data)
            command = func[data['type']][0](data)
            if command == "":
                return "wrong format"
            print "quest", command
            result = client.send(command)
            result = unicode(result, "utf-8")
            return json.dumps(func[data['type']][1](result))
        else:
            return ""
    return ""

app.secret_key = 'A0Zr98j/3asdfHH!&&mN]LWX/,?RT'


if __name__ == '__main__':
    app.run(host='0.0.0.0',debug=True)
