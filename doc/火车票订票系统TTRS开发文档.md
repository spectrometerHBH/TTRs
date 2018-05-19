

#火车票订票系统TTRS开发文档

---

#简介

软件名称：小熊猫12306

适用平台：Android 6.0(Marshmallow) - Android 8.1(Oreo)  (API 23 - API 27)

开发工具：Android Studio 3.1.2

开发环境：windows10、macOS High Sierra 10.13.4、ubuntu16.04

测试平台：Android Virtual Devices、Huawei MLA-AL 10、VIVO Xplay6、 Sony Xperia xzs

测试环境：Android 6.0、 Android 7.11、Android 8.0

开发技术：Java + xml


##模块划分图

##模块设计

本app的通讯基于http协议，采用了OkHttp3.10.0通讯框架，利用轻量级数据交换格式JSON对传输数据进行封装，使用AES-256对称加密算法和Base64编码两重包装进行数据的安全传递。

###通讯模块

封装`AESUtil`类用于字符串的AES和Base64加解密

```java
public class AESUtil {

    public static String Encrypt(String sSrc, String sKey) throws Exception {
        byte[] raw = sKey.getBytes("utf-8");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
        return Base64.encodeToString(encrypted, Base64.NO_WRAP);
    }

    public static String Decrypt(String sSrc, String sKey) throws Exception {
        try {
            byte[] raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = Base64.decode(sSrc, Base64.NO_WRAP);
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original,"utf-8");
                return originalString;
            } catch (Exception e) {
                System.out.println(e.toString());
                return null;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return null;
        }
    }

}
```



封装`HttpClient` 类用于向服务器发起Http请求并收到回复

```java
public class HttpClient {
    private String command;
    private String responseData;
    private Context context;
    //KEY

    public HttpClient(){
        this.context = context;
    }
    public void setCommand(String command) {
        this.command = command;
    }

    public String run() {
        try {
            String commandEncoded = AESUtil.Encrypt(command, KEY);
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(100, TimeUnit.SECONDS).readTimeout(200, TimeUnit.SECONDS).build();
            RequestBody requestBody = new FormBody.Builder().add("input", commandEncoded).build();
            Request request = new Request.Builder().url("ip:port").post(requestBody).build();
            Response response = client.newCall(request).execute();
            responseData = AESUtil.Decrypt(response.body().string(), KEY);
        }catch (Exception e){
            e.printStackTrace();
        }
        return responseData;
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


### 用户模块



### 车次模块

### 辅助模块

##《使用手册》

###《系统安装手册》

###《用户手册》

