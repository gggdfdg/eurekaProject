# git学习（提交github）
 
------

首先介绍下本章学习的内容：
 
> * 下载git和使用git
> * 提交项目到github

## 下载git
去git官网（https://git-scm.com/）下载git版本，下载后有git-bash.exe和git-cmd.exe连个ext文件，下面讲解用处

### 1. git实践（自己去安装git和环境变量配置）
 
- [x] git-bash.exe
- [x] git-cmd.exe


#### 1. git-bash（举例使用）
 
##### 创建本地ssh key(your_email@youremail.com替换成自己的邮箱)，用来和github通讯

```seq
   ssh-keygen -t rsa -C "your_email@youremail.com"
```

直接点回车，说明会在默认文件id_rsa上生成ssh key。 
然后系统要求输入密码，直接按回车表示不设密码
重复密码时也是直接回车，之后提示你shh key已经生成成功。
这时候默认生成的key都在C:\Users\Administrator\.ssh（隐藏文件）下面，自己去把id_rsa.pub里面的字符复制出来

##### github添加id_rsa.pub里面的key（凭证，允许上传和下载github）

在github的Settings中的SSH页面卡添加new Shell Key，将id_rsa.pu复制进去，这样github和本地的git就能通讯了

##### 通讯测试

```seq
    ssh -T git@github.com
```

这时候如果有设置密码，需要写入密码，然后提示success等字眼You’ve successfully authenticated, but GitHub does not provide shell access 。这就表示已成功连上github。
                                
#### 1. git-cmd（举例使用）

##### github服务端新建项目（类似svn新建目录生成svn地址），自己去Create a new repository

创建好之后会有类似svn的地址（git@github.com:xxx/zzz.git），如果是新建的仓库，很好找（在code那一栏），
如果创建完毕有上传过东西，就在代码clone and download里面有个ssh地址，直接复制下，以下git remote add origin操作就是用到这个路径

##### git客户端检出（类似checkOut）
在本地路径新建一个跟仓库名一样的项目，我的项目是eurekaProject，就在G新建个，把代码放进去eurekaProject，
然后打开git-cmd，在黑框输入以下命令

```seq
   cd G:eurekaProject
   G:
   git remote add origin git@github.com:xxx/zzz.git
```
##### git客户端初始化项目
```seq
  git init
```

##### 本地仓库传到github上去，在此之前还需要设置username和email，因为github每次commit都会记录他们
```seq
  git config --global user.name "your name"
  git config --global user.email "your_email@youremail.com
```

##### 将本地路径的所有人上传到github（例如，本地目录是G:eurekaProject所有文件），像svn的add
```seq
  git add .
```

##### 将add的操作提交到github，例如svn的commit
```seq
  git commit -m "init"
```

##### 将add的操作提交到github主干（如果要提交到分支，自己去百度）
```seq
  git push origin master
```

##### 备注
```seq
  切记提交东西一定要push，不管是intellj idea的插件还是git本身，不push就不生效
```
