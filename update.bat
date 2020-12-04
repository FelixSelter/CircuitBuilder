@echo off
set /p msg=Commit? 
git add --all
git status
git commit -m %msg%
git push
pause