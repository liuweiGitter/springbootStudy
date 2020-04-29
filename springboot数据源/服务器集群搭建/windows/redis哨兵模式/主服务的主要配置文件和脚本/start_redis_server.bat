@echo off
chcp 65001
echo redis服务前台运行，退出此窗口将关闭redis服务
echo redis服务若顺利启动，将持续保持监听，直到被系统或用户关闭
echo 准备启动redis服务...
redis-server.exe redis.windows.conf --port 26379