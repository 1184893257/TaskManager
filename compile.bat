if exist bin goto run
mkdir bin

:run
cd src\gui
javac -classpath .. -d ..\..\bin Top.java
echo �������
set /p A=���س�����
