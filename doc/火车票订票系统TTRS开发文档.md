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

![](images/project.png)

##模块设计

###通讯模块

> 一切安全问题开始于隔阂（Gap）。比如说，如果从现在开始来设计编译器和CPU，还会有缓冲区溢出吗？这里体现的就是历史的隔阂。人无法超越他所处的时代，时代的变化产生人所无法预料的新隔阂。
>
> ——《网络安全的哲学思考》 作者：devway (from xfocus bbs)

本app与服务器的通讯基于http协议，采用了`OkHttp3.10.0`通讯框架，利用轻量级数据交换格式JSON对传输数据进行封装，使用$AES-256$对称加密算法和$Base64$编码两重包装进行数据的安全传递。

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

用户模块主要由`Login/Register/MainActivity/ModifyUserInfo/ModifyUserInfoAdmin/UserQuery/OrderManifest`七个`Activity` 负责



`Login`是用户/管理员登录的活动，同时也是用户/管理员进入app的第一个活动

`Register`是用户注册的活动

`MainActivity`是用户/管理员筛选自己订票信息的活动，同时也是整个app的主活动和主页

`ModifyUserInfo`是用户查询和修改自己信息的活动

`ModifyUserInfoAdmin`是管理员查询和修改自己信息的活动

`UserQuery`是管理员查询和修改用户信息的活动

`OrderManifest`是用户/管理员查看自己筛选后的订票具体信息和退票的活动



### 车次模块

车次模块主要由`TrainQuery(ContentFragment_train_query/ContentFragment_train_detail)/TrainOperation(ContentFragment_train_add/ContentFragment_train_other_operation)/GetStation/TicketManifest/TrainDetailManifest`四个`Activity`和四个`Fragment`负责，同时开发了工具类`Seats/SeatsAdapter/Train/TrainAdapter`



`TrainQuery`是用户/管理员进行车票查询（包括中转）的活动，其中依附于该活动的`ContentFragment_train_query`和`ContentFragment_train_detail`两个碎片分别负责 $$1.$$根据站点、时间及席别查询 $2.$根据车次的ID查询

`TrainDetailManifest`是用户/管理员根据车次ID查询火车后显示车次具体信息的活动

`TrainOperation`是管理员进行车次管理的活动，其中依附于该活动的`ContentFragment_train_add`和`ContentFragment_train_other_operation`两个碎片分别负责$1.$车次的新建和修改 $2.$车次的公开和删除

`GetStation`是管理员在新建和修改车次时设置各站名、到时、发时、停时和票价的活动



### UI模块

> 1、材料是个隐喻
>
> 2、表面是直观和自然的
>
> 3、维度提供交互
>
> 4、适应性设计
>
> 5、目录用黑体和图形设计，并带有意图
>
> 6、颜色、表面和图标都强调动作效果
>
> 7、用户发起变化
>
> 8、动画效果要在统一的环境下显示
>
> 9、动作提供了意义
>
> ——Material Design

为了尽可能优化用户的细节体验，我们在app的UI支持上做了如下工作

1、活动之间的跳转

采用符合Material Design设计理念的`NavigationView`组件，布局文件`nav_view/nav_header`使用户既能够在导航中自由地跳转活动，也能够实时查看自己的简略信息

并且利用`Broadcast`，如果用户修改了自己的信息，导航中也能实时更新

2、车票的显示

在参考了多个火车票订票软件和Material Design官方的demo之后，我们最终采用时刻优先，站名次之，票价最次的理念设计，整体票的展示使用`ExpandableListview`组件，为`parentView`和`childView`编写了布局文件`train_ticket_query.xml`和`ticket_purchase.xml`

并且

3、时刻表的显示

我们采用了透明黑底色、纯白字体的设计来显示时刻表，布局文件`activity_time_table.xml`和`station.xml`

4、加载动画

我们采用了随机小熊猫表情包的设计，利用`DialogFragment`组件，布局文件`progress_dialog.xml`

5、购票/退票的确认对话框

为了更好更多地显示必要的确认信息，利用`DialogFragment`组件，布局文件`dialog_layout.xml`

6、日历

从时刻表的设计中获取灵感，我们将`Calender`组件和透明黑底的设计结合在一起，布局文件`calendar.xml`

7、`Toast`提示信息

为了更直观地体现信息，突出信息，丰富界面的色彩，我们采用了开源的`Toasty`组件替代原生的`Toast`组件，达到多种颜色显示提示信息的效果

8、车站选择列表

为了方便用户选择出发地和目的地车站，我们从后端获取当前所有车站数据，利用`pinyin4j-2.5.0.jar`对所有车站按拼音排序，并设置侧边栏和关键字搜索框方便用户定位和查找

9、标签页切换

在车次查询和车次管理界面中使用`TabLayout`和`Fragment`，让用户在一个`Activity`里面直接选择或切换查询车次方法以及车次管理操作，减少`Activity`间跳转

10、统一风格的设计

统一使用Google官方的material design图标包，并使用Google官方的色调推荐，统一整个app的UI风格



##《使用手册》

###《系统安装手册》

从https://pan.baidu.com/s/1Tpy77cNBqImNr3Iu4hk70g获取APK文件，打开并按提示完成安装过程

###《用户手册》

#### 基础操作

1、启动、注册、登录
<img src="images\start_registe_login.gif" \>

2、记住密码
<img src="images\remember_password.gif" \>

3、注册、登录错误提醒
<img src="images\wrong_login.gif" \><img src="images\wrong_registe.gif" \>

#### 用户相关

1、用户信息修改
<img src="images\modify_user_profile.gif" \>

2、用户信息、权限修改
<img src="images\modify_user_profile_privilege.gif" \>

4、用户信息查询错误提醒
<img src="images\wrong_query_userid.gif" \>

####车票相关

1、购票、退票
<img src="images\ticket_purchase_return.gif" \>

2、查票
<img src="images\query_ticket.gif" \>

3、购票、退票错误提醒
<img src="images\wrong_ticket_purchase_return.gif" \>

4、查票错误提醒
<img src="images\wrong_query_ticket.gif" \>

####车次相关

1、查询车次
<img src="images\query_train.gif" \><img src="images\query_train_trans.gif" \>

2、新增、修改车次
<img src="images\add_train.gif" \><img src="images\modify_train.gif" \>

3、公开、删除车次
<img src="images\publish_train.gif" \><img src="images\delete_train.gif" \>

4、查看时刻表
<img src="images\timetable.gif" \><img src="images\timetable2.gif" \>

5、查询车次错误提醒
<img src="images\wrong_query_train.gif" \>

6、新增车次错误提醒
<img src="images\wrong_add_train.gif" \>

7、公开、删除车次错误提醒
<img src="images\wrong_train_delete_publish.gif" \>

##收获

>“不妨假设心灵是一张白纸，没有任何符号，没有任何想法。
>你们心灵是如何丰富起来的？
>人类无限的想象力在其中描绘出了无穷无尽的可能性，这是从哪里来的？
>知识和推理，又是从哪里来的？
>我的答案只有一句话，从经历中来。”
>——约翰·洛克《人类理解论》

积累了一些Android开发经验以及UI设计经验
对Android软件架构有了深刻理解
理解了一些软件开发哲学
当然，最重要的，是经历