## Git 常用操作

初始化

全局变量

```java
git config --global user.name "name"
git config --global user.eamil "user@user.com"
git config --list
```

初始化新版本库：

```java
git init #只会在根目录下创建一个.git文件
```

设置忽略文件：

设置每个人想要忽略的文件

- 在根目录下新建一个名为.gitignore的文本文件

```java
echo *.jpg>.gitignore
```

- 将.gitignore文件加入版本库并提交

设置只有自己忽略的文件

- 修改.git/info/exclude文件

- 可使用正则

  *.[oa] 等价于 *.o and *.a

添加新文件到版本库

- 添加单个文件

  ```
  git add a.txt
  ```

- 添加所有类型文件

  ```java
  git add *.类型
  ```

- 添加所有文件

  ```java
  git add .
  ```

提交

```java
git commit -m "message"
```

撤销

- 撤销提交

  反转提交（反转最近一次提交）

  ```java
  相当于提交最近的一次提交的反操作
  git revert --no-commit head
  ```

  复位

  取消暂存

  ```java
  git reset head 或 git reset head <filename>
  ```

  复位到head之前的版本

  ```java
  git reset --head head^^
  ```

查看状态

- 查看当前状态

  ```java
  git status
  ```

- 历史记录

  ```java
  git log #历史记录
  gitk #查看当前分支的历史记录
  gitk <branchname> #查看某一分支
  gitk --all #查看所有分支
  ```

- 每个分支最后的提交

  ```java
  git branch -v
  ```

查看远程分支 

```java
git branch -a
```

新建分支

```java
git checkout -b 分支名
git push --set-upstream origin 分支名
```

删除远程库中不存在的分支

```java
git remote prune origin
```

删除分支

```ajva
git branch -d 分支名 #如果分支没有被合并会删除失败
git branch -D 分支名 #即使分支没有被合并也会删除
```

重命名分支

```java
git branch -m <branchname> <newname> #不会覆盖存在的同名分支
git branch -M <branchname> <newname> #会覆盖存在的同名分支
```

更新origin索引

如果看不到新建的远程分支，可以先用git fetch命令，更新origin索引

```java
git fetch
```

查看本地分支

```java
git branch
```

切换分支

git checkout -b v0.9rc1 origin/v0.9rc1

推入远程库

```java
git push origin master #远程的master不能是当前分支
```

从远程库获取

- 获取但不合并

  ```java
  git fetch <远程库名称>
  ```

- 获取并合并到当前本地分支

  ```java
  git pull 等价于 git pull origin
  ```

克隆版本库

```java
git clone -b <分支> URL
```



配置SSH

ssh-keygen -t rsa -C "这里换上你的邮箱"

执行命令后需要进行3次或4次确认：

确认秘钥的保存路径（如果不需要改路径则直接回车）；
如果上一步置顶的保存路径下已经有秘钥文件，则需要确认是否覆盖（如果之前的秘钥不再需要则直接回车覆盖，如需要则手动拷贝到其他目录后再覆盖）；
创建密码（如果不需要密码则直接回车）；
确认密码；

在指定的保存路径下会生成2个名为id_rsa和id_rsa.pub的文件：![秘钥文件](https://img-blog.csdn.net/20180105182651279)