# lottery-draw
运行步骤：

1.下载到本地。如果是zip格式下载，请于下载后解压。

2.编辑抽奖人员信息
  修改src/main/resources/profiles.xlsx， 然后保存。如有人员图片，请于image URL列填入图片名（例：a.jpg, 不需要任何路径），并把图片文件放入src/main/webapp/img 文件夹。 如果image列没有内容，则默认使用name列的第一个英文字母作为显示图片。  最后一列为人员状态，如果输入了内容，则会在页面上特别标示。

3.编辑奖项信息
  修改src/main/resources/tablePrize.xlsx， 然后保存。如有奖项图片，请于第2列填入图片名（例：a.jpg, 不需要任何路径），并把图片文件放入src/main/webapp/img 文件夹。 最后一列为奖项派发数量。
  
4.运行程序， 打开命令行：

(1) cd <lottery-draw根目录>

(2)

在linux下：

./mvnw spring-boot:run

or

在windows下：

mvnw.cmd spring-boot:run

当第4步执行完毕， 命令行会显示 Tomcat started on port(s): 8098 (http)， 此时打开浏览器，访问http://localhost:8098/ 。
所有抽奖结果均记录于 log/draw-result.log 文件中。

