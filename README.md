# 6.0动态权限申请工具类

## 使用方法：
1. 在需要动态申请权限的地方调用：
RequestPermissionsUtil.needPermission（）方法。

2. 同时在该activity或者fragment中的onRequestPermissionsResult方法中调用 RequestPermissionsUtil.setPermissionsResult（）方法即可。

## 该工具类优点：
1. 会在失败方法中显示用户是第几次使用该权限申请。（适配所有机型）（在某些机型中，只要用户第一次拒绝该权限申请，以后再申请该权限则没有任何提示。）
   而在该工具类中，你可以根据次数自行进行处理。      
2. 代码中不用判断是否是6.0以上机器，只用调用这两个方法即可。工具中会自动判断。


##写在最后：
 工程是使用该工具类的一个Demo。
 
 支持原创。yhGo
