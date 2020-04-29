@echo off
chcp 65001
echo redis哨兵服务前台运行，退出此窗口将关闭redis哨兵服务
echo redis哨兵服务若顺利启动，将持续保持监听，直到被系统或用户关闭
echo 准备启动redis哨兵服务...
redis-server.exe sentinel.conf --sentinel
pause