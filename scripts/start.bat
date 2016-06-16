@ECHO OFF
cd ..
SET BASE_PATH=%cd%

SET CLASSPATH=.;%CLASSPATH%

FOR %%F IN (%BASE_PATH%\lib\*.jar) DO CALL %BASE_PATH%/bin/addclasspath.bat %%F

java com.jpycrgo.gsimgdown.Main

PAUSE