#!/bin/sh

# 定义 JAVA CLASSPATH 环境变量
CP=.

# 工作路径变量
BASE_DIR=$(cd "$(dirname "$0")/.."; pwd)

# 切换到工作目录下
cd $BASE_DIR

# 将 lib 下的 jar 包添加到 CLASSPATH 环境变量
for file in $BASE_DIR/lib/*.jar
do
    echo 'ADD JAVA LIB' $file
    CP=$CP:$file
done

# 启动程序
java -classpath $CP com.jpycrgo.gsimgdown.Main

echo "please press any key"
read -n 1