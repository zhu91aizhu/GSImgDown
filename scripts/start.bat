@ECHO OFF

:: 原始路径
SET SOURCE_PATH=%CD%

:: 工作路径
SET BASE_PATH=%~dp0..

CD /D %BASE_PATH%

:: 定义 JAVA CLASSPATH 环境变量
SET CP=.

:: 将 lib 下的 jar 包添加到 CLASSPATH 环境变量
FOR %%F IN (lib\*.jar) DO CALL bin/addclasspath.bat %%F

:: 启动程序
java -classpath %CP% com.jpycrgo.gsimgdown.Main

:: 程序执行完后切换回原始路径
CD "%SOURCE_PATH%"

PAUSE