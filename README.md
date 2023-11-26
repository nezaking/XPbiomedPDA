# PDA扫描入库系统
条码采集PDA系统

## 主要产品流程  
通过安卓PDA巴枪结合飞书系统来搭建一个扫描出入库，产品溯源，产品防伪

##  实现思路 
通过Bartender来给每个条码生成唯一的二维码，在入库时读取该二维码包含的货号，批次，生产日期，随机多位数。再附加自己的用户post到表格  
在出库时，可以通过扫描订单号，唯一码等信息，将订单与该销售信息绑定，实现了溯源，防窜货  
同理，我们可以认为，只有经过仓库扫码出库的产品方为正品！否则视为假冒产品




## 实现条件

###  硬件篇

硬件，我们可以采用闲鱼二手PDA产品，类似智联天地，优博讯等等，一维300到500不等，二维的600到800不等，尽量选用快递系统采用的型号，毕竟99%的情况，您的使用环境，你的业务量，在中国的快递系统面前就是一个弟弟


###  软件篇

软件我们可以白嫖一些大公司产品，来降低我们的产品

这里使用飞书，因为飞书有非常完整的多维表格，云文档调用接口。今日头条的实力，在中国互联网大厂中也是头几位

参考文档
[飞书多维表格新增记录](https://open.feishu.cn/document/server-docs/docs/bitable-v1/app-table-record/create)


通过Bartender来生成唯一的条码  [Bartender官网] https://www.seagullscientific.com/cn/
记得不要买苏州马克丁代理的那个，那个是假的！

下面是生成唯一条码的VB代码，在数据源那里添加引用即可


```C#
str1 =CHR(round((Rnd*25)+65,0))
str2 =CHR(round((Rnd*25)+97,0))
str3=Cstr(round((Rnd*1000000),0))
str4 =CHR(round((Rnd*25)+65,0))
str5 =CHR(round((Rnd*25)+65,0))
makeday=Cstr(DateDiff("s", "1970-01-01 00:00:00", Now) )
Value="#spm="+str1+str2+str3+str4+str5+"&makeday"+makeday+"model"+model+"batch"+batch+"sn"+sn

```


###  关于作者

*  Email:nezaking@qq.com,nezaking@gmail.com  
*  有任何建议或者使用中遇到问题都可以给我发邮件



##  下面是参考别人的效果图




登录效果图

![登录效果图](https://github.com/ygg404/hmfpda/blob/master/screenshoot/%E7%99%BB%E5%BD%95%E9%A1%B5%E9%9D%A2.png)

主界面效果图

![主界面效果图](https://github.com/ygg404/hmfpda/blob/master/screenshoot/%E4%B8%BB%E8%8F%9C%E5%8D%95.png)

入库采集效果图

![入库采集效果图](https://github.com/ygg404/hmfpda/blob/master/screenshoot/入库.png)

入库查询效果图

![入库查询效果图](https://github.com/ygg404/hmfpda/blob/master/screenshoot/入库查询.png)

系统配置图

![系统配置图](https://github.com/ygg404/hmfpda/blob/master/screenshoot/系统配置页面.png)
