if exist bin goto run
mkdir bin

:run
cd src\gui
javac -classpath .. -d ..\..\bin Top.java
echo 编译完毕
set /p A=按回车结束
