# xpprojects
* **学习记录**
* 关于xposed+sekiro 的安卓端demo；
* 实现xposed通过http主动rpc调用方法的demo；
* 框架地址：https://github.com/virjar/sekiro

## xplvsk 
低版本的sekiro1.0.1的和xposed配合
* 主要api
    * 查看分组：http://localhost:5600/groupList
    * 调用方法：http://localhost:5600/invoke?group=test&action=t1&arg1=1&arg2=1
    * 剩余的参见：https://github.com/gl953236368/sekiro

## xphvsk
当前版本sekiro1.4的和xposed配合
* 主要api
    * 查看分组：http://localhost:5620/business-demo/groupList
    * 调用方法：http://localhost:5620/business-demo/invoke?group=TestDemo&action=test&param1=10&param2=1
    * 剩余的参见：https://sekiro.virjar.com/sekiro-doc/01_user_manual/

## xpinline
当前版本sekiro1.4的和xposed（native层hook）配合
* 测试功能：
   * 利用inline-hook，对native层函数拦截、存储调用（框架地址：https://github.com/ele7enxxh/Android-Inline-Hook）
   * sekiro服务调用xposed模块，主动调用生成参数
* 测试demo：zy5.7.3（aes-key、请求头）
