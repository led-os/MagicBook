                                                    MagicBook 项目介绍

     在项目中集成 Jsoup 解析小说网站获取数据,并将数据封装用于建立用户行为.
     
     在本项目中实现的功能有：
        1.收藏书籍
        2.书架功能
        3.阅读书本详情,并记录阅读历史
        4.实现记录书本的阅读进度
        5.登录,注册
        6.排行榜
      
     项目中相关技术的使用：
        1.Mvp设计模式实现数据与视图分离
        2.网络框架使用 Rxjava+retrofit+okhttp 获取网络中的网页数据并使用 Jsoup 进行解析.
        3.使用LitePal 将数据存入数据库
        4.图片加载使用了Glide实现图片圆角化,高斯模糊,gif动图加载
        5.Activity,Fragment之间的通信使用了EventBus进行数据交换
        6.使用ImmersionBar实现沉浸式状态栏
        7.屏幕适配使用了今日头条适配方案
        8.RecyclerView的适配器使用BasequickAdapter
        
 
项目截图 ： 
1.书架    

![书架](https://raw.githubusercontent.com/pressureKai/MagicBook/master/pic/bookCase.jpg)

2.书籍阅读界面

![书籍阅读](https://raw.githubusercontent.com/pressureKai/MagicBook/master/pic/bookRead.jpg)

2.书籍阅读设置界面

![设置目录](https://raw.githubusercontent.com/pressureKai/MagicBook/master/pic/bookMenu.jpg)       ![设置](https://raw.githubusercontent.com/pressureKai/MagicBook/master/pic/bookSet.jpg)

3.书城界面

![首页](https://raw.githubusercontent.com/pressureKai/MagicBook/master/pic/bookCity.jpg)       ![类型1](https://raw.githubusercontent.com/pressureKai/MagicBook/master/pic/bookType_1.jpg)       ![类型2](https://raw.githubusercontent.com/pressureKai/MagicBook/master/pic/bookType_2.jpg)


4.排行榜
仿微信下拉进入小程序页面 ->> 书架下拉进入排行榜界面

![书架下拉](https://raw.githubusercontent.com/pressureKai/MagicBook/master/pic/book_drag_down.jpg)   ![排行榜](https://raw.githubusercontent.com/pressureKai/MagicBook/master/pic/book_rank.jpg)

5.搜索界面

![搜索界面](https://raw.githubusercontent.com/pressureKai/MagicBook/master/pic/book_search.jpg)   ![搜索结果](https://raw.githubusercontent.com/pressureKai/MagicBook/master/pic/book_search_result.jpg)


6.书籍详情


![书籍详情](https://raw.githubusercontent.com/pressureKai/MagicBook/master/pic/book_detail.jpg)

7.收藏界面

![收藏界面](https://raw.githubusercontent.com/pressureKai/MagicBook/master/pic/book_like.jpg)


8.阅读历史

![阅读历史](https://raw.githubusercontent.com/pressureKai/MagicBook/master/pic/book_history.jpg)


9.登录注册界面

![登录](https://raw.githubusercontent.com/pressureKai/MagicBook/master/pic/book_login.jpg)    ![注册](https://raw.githubusercontent.com/pressureKai/MagicBook/master/pic/book_regist.jpg)




