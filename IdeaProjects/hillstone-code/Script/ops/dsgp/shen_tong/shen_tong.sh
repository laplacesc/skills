#!/bin/bash

docker stop shentongdb && docker rm shentongdb && docker run -itd --name shentongdb -p 2003:2003 -p 7712:7712 shentongdata/shentongdb:7.0.8.191204

isql -h 127.0.0.1 -p 2003 -d osrdb -u sysdba/szoscar55

isql -h 127.0.0.1 -p 2003 -d osrdb -u sysftsdba/szoscar55

isql -h 127.0.0.1 -p 2003 -d osrdb -u sysaudit/szoscar55

isql -h 127.0.0.1 -p 2003 -d osrdb -u syssecure/szoscar55

isql -h 127.0.0.1 -p 2003 -d osrdb -u JXWU_USER_1/hillstone

isql -h 127.0.0.1 -p 2003 -d osrdb -u JXWU_USER_2/hillstone
