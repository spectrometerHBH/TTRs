

#火车票订票系统TTRS开发文档

---

#简介

软件名称：小熊猫12306

适用平台：Android 6.0(Marshmallow) - Android 8.1(Oreo)  (API 23 - API 27)

开发工具：Android Studio 3.1.2

开发环境：Linux、windows10

测试平台：Android Virtual Devices、Huawei MLA-AL 10、VIVO Xplay6、 Sony Xperia xzs

测试环境：Android 6.0、 Android 7.11


##模块划分图

##模块设计

本app的通讯基于http协议，采用了OkHttp3.10.0通讯框架，利用轻量级数据交换格式JSON

###通讯模块

```java
public class HttpClient {
    private String command;
    private String responseData;

    public void setCommand(String command) {
        this.command = command;
    }

    public String run() {
        try {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(100, TimeUnit.SECONDS).readTimeout(200, TimeUnit.SECONDS).build();
            RequestBody requestBody = new FormBody.Builder().add("input", command).build();
            Request request = new Request.Builder().url("http://120.79.236.3:5000/action/post").post(requestBody).build();
            Response response = client.newCall(request).execute();
            responseData = response.body().string();
        }catch (Exception e){
            e.printStackTrace();
        }
        return responseData;
    }
}
```

封装`HttpClient` 类用于通讯

```java
public class JSONArrayStringCreate {
    private String result;

    public JSONArrayStringCreate() { result = ""; }
    public void addString(String item){
        result = result + "\"" + item + "\",";
    }
    public void addInt(String jsonObject){
        result = result + jsonObject + ",";
    }
    public void addJSONObject(String jsonObject){
        result = result + jsonObject + ",";
    }
    public String getResult(){
        String ans = result.substring(0, result.length() - 1);
        return "[" + ans + "]";
    }
}
```

封装`JSONArrayStringCreate`类用于构造JSON对象，进行通讯传递

```java
public class JSONArrayStringCreate {
    private String result;

    public JSONArrayStringCreate() { result = ""; }
    public void addString(String item){
        result = result + "\"" + item + "\",";
    }
    public void addInt(String jsonObject){
        result = result + jsonObject + ",";
    }
    public void addJSONObject(String jsonObject){
        result = result + jsonObject + ",";
    }
    public String getResult(){
        String ans = result.substring(0, result.length() - 1);
        return "[" + ans + "]";
    }
}
```
封装`JSONArrayStringCreate`类用于构造JSONArray对象，进行通讯传递

### 用户模块

### 车次模块

### 辅助模块

##《使用手册》

###《系统安装手册》

###《用户手册》

