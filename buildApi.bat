@echo off
cd C:\Users\Maximilian\Desktop\IntelliJ\Workspace\NoteblockRecorder
cp src\main\java\de\cerus\noteblockrecorder\api\NoteblockRecorderApi.java api-maker\src\main\java\de\cerus\noteblockrecorder\api\
rm -rf src\main\java\de\cerus\noteblockrecorder\song
cp -r src\main\java\de\cerus\noteblockrecorder\song api-maker\src\main\java\de\cerus\noteblockrecorder
call api-maker\build.bat
cd C:\Users\Maximilian\Desktop\IntelliJ\Workspace\NoteblockRecorder
echo === Done ===