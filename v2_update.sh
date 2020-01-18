#!/bin/bash
#只能在x86-64的openwrt系统上更lean大佬的XXXPLUS和passwall的v2插件！
#	bash -c "$(wget -O- https://git.io/JvTVT)"
#Version: v2.0.2 By Len yu.
cd /tmp/tmp
rm -rf *
echo
clear
echo
echo
echo -e " $green Version: v2.0.2 By Len yu.$none"
echo
echo -e " $green 只能x86-64的openwrt系统上，可更新lean大佬的XXXPLUS和passwall的v2插件！$none"
sleep 2
clear
echo 
echo -e " $green 正在获取网络v2ray最新版信息..$none"
echo
#judgment
v2ray_latest_ver="$(curl -H 'Cache-Control: no-cache' -s https://api.github.com/repos/v2ray/v2ray-core/releases/latest | grep 'tag_name' | cut -d\" -f4)" 
sleep 2
v2ray_ver="v$(/usr/bin/v2ray/v2ray -version | grep "V2" | awk '{print $2}')" &> /dev/null 
if [ "$v2ray_ver" != "$v2ray_latest_ver" ]; then
		clear
		echo
		echo -e " $green 咦...发现新版本耶....正在拼命更新.......$none"
		echo
		v2ray_download_link="https://github.com/v2ray/v2ray-core/releases/download/$v2ray_latest_ver/v2ray-linux-64.zip"
		echo
		echo
		wget  "$v2ray_download_link"
		unzip -o v2ray-linux-64.zip &> /dev/null
		echo
else
		clear
		echo
		echo -e " $green 木有发现新版本....$none"
		echo
		exit 0
fi
##install
st="`ps | grep "passwall" | awk '{print $8}'`"  &> /dev/null
sr="`ps -ef | grep "ssr-retcp" | awk '{print $9}' | echo ${sr: 13:5}`" &> /dev/null
if [[ "`echo ${st: 10:8}`" =  "passwall" &&  -e /tmp/tmp/v2ray ]]; then
		mv -f /tmp/tmp/v2ctl  /usr/bin/v2ray
		mv -f /tmp/tmp/v2ray  /usr/bin/v2ray
		chmod 755 /usr/bin/v2ray/v2ctl
		chmod 755 /usr/bin/v2ray/v2ray
		/etc/init.d/passwall restart
		echo
        	echo -e " $green 更新成功啦...当前 V2Ray 版本: ${cyan}$v2ray_latest_ver$none"
		echo
		echo -e " $yellow 温馨提示: 为了避免出现莫名其妙的问题..V2Ray路由端的版本最好和服务器的版本保持一致$none"
		echo
		echo
		exit 0
elif [[ "$sr" = "retcp" &&  -e /tmp/tmp/v2ray ]]; then
		mv -f /tmp/tmp/v2ctl  /usr/bin/v2ray
		mv -f /tmp/tmp/v2ray  /usr/bin/v2ray
		chmod 755 /usr/bin/v2ray/v2ctl
		chmod 755 /usr/bin/v2ray/v2ray
		/etc/init.d/shadowsocksr restart
		echo
        	echo -e " $green 更新成功啦...当前 V2Ray 版本: ${cyan}$v2ray_latest_ver$none"
		echo
		echo -e " $yellow 温馨提示: 为了避免出现莫名其妙的问题...V2Ray路由端的版本最好和服务器的版本保持一致$none"
		echo
		echo
		exit 0
else
		echo
        	echo -e "$red 下载 V2Ray 失败[需要连接科学]...请重试...$none"
		echo
		echo
        	exit 0
fi
