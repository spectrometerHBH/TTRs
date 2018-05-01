#-*- coding:utf-8 -*-
# 导入socket库:
import socket

#HOST = "120.79.236.3"
#PORT = 2333
HOST = "localhost"
PORT = 2333

def send(command):
# 创建一个socket:
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
# 建立连接:
    s.connect((HOST, PORT))
    s.send(command)
    buffer = []
    while True:
# 每次最多接收1k字节:
        d = s.recv(1024)
        if d:
            buffer.append(d)
        else:
            break
    data = ''.join(buffer)
    s.close()
    return data

def query_profile(userid):
    return send("query_profile "+userid+"\n\0")

def login(userid, password):
    return send("login "+userid+" "+password+"\n\0")

def register(name, password, email, phone):
    return send("register "+name+" "+password+" "+email+" "+phone+"\n\0")

def query_profile(userid):
    return send("query_profile "+userid+"\n\0")

def modify_profile(userid, name, password, email, phone):
    return send("modify_profile "+userid+" "+name+" "+password+" "+email+" "+phone+"\n\0")

def modify_profile2(userid, name, email, phone):
    return send("modify_profile2 "+userid+" "+name+" "+email+" "+phone+"\n\0")

def modify_privilege(id1, id2, privilege):
    return send("modify_privilege "+id1+" "+id2+" "+privilege+"\n\0")

def main():
    while(True):
        readin = raw_input(">>")
        print(send(readin+"\0"))