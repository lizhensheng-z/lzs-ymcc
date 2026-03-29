@echo off
REM 创建 media-topic
REM 参数说明：
REM -n NameServer 地址
REM -t Topic 名称
REM -w 可写队列数
REM -r 可读队列数

echo 正在创建 media-topic...
D:\develop\rocketmq-all-4.9.8-bin-release\bin\mqadmin.bat updateTopic -n localhost:9876 -t media-topic -c DefaultCluster -w 4 -r 4

echo media-topic 创建完成！
pause
