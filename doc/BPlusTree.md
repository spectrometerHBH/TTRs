## B+树文档

B+树的具体原理在这里不再提及

### 硬盘空间管理(class file_alloc)

处理硬盘空间管理的代码在alloc.h和alloc.cpp两个文件当中，其中实现了file_alloc类，用于管理硬盘空间。支持的操作有申请新空间、释放空间，和malloc、free与new、delete类似。

#### 接口说明

+ 构造函数

  `file_alloc::file_alloc`

  其实并不需要什么参数。

+ 从文件中加载信息

  `void file_alloc::load(const char * filename = "")`

  从文件名为filename的文件中加载硬盘空间使用信息

+ 把信息写入文件

  `void file_alloc::dump(const char * filename = "")`

  把硬盘空间使用情况写入filename当中

+ 判断是否为空

  `inline bool file_alloc::empty()`

+ 清空

  `void file_alloc::clear();`

+ 申请新空间

  `off_t file_alloc::alloc(size_t len);`

+ 释放空间

  `void file_alloc::free(off_t pos, size_t len);`

+ 打印空间使用情况

  `void file_alloc::print()`

  ### B+树结构(class bptree)

这是一个类模板

```
template <class key_t, class value_t, size_t node_size = 4096, class Compare = std::less<key_t>>
class bptree
```

#### 接口说明

+ 构造函数

  `bptree(const char * fname, const char * index_fname)`

  fname:储存B+树数据的文件

  index_fname:储存记录磁盘空间使用情况的文件

+ 删库

  `init()`

+ 询问是否有某个key

  `int count(const key_t &key)`

  如果有返回1，否则返回0

+ 根据key查找元素

  `value_t find(const key_t &key, const value_t & d = value_t())`

  返回key所对应的value，若key不存在，返回d

+ 判断是否为空

  `inline bool empty()`

+ 插入元素

  `insert(const key_t &key, const value_t &v)`

  若key已存在，则什么事情都不会做

+ 修改

  `void set(const key_t &key, const value_t &v)`

  如果不存在key，会报错，返回not_found

+ 删除

  `void remove(const key_t &key)`

  如果不存在key，会报错，返回not_found

+ 区间查找

  `void search(array_t & arr, const key_t & key, std::function<bool(const key_t &, const key_t &)> compar)`

  **请确保compar所确定的区间范围在磁盘上连续**

  compar的意义为“小于”。将B+树中key既不大于key又不小于key的记录添加到arr中。arr是一个vector。