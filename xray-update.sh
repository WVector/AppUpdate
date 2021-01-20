#/usr/bin/bash
#Author Lenyu 
#version v1.1.3
#获取当前绝对路径
#echo "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )" 
cd /home/lenyu
echo
echo "准备获取最新的xray源码版本…"
echo
sh_new_ver=$(wget --no-check-certificate -qO- -t1 -T3 "https://github.com/XTLS/Xray-core/commits/main")
echo $sh_new_ver > 110
sleep 2
# 把文本内容格式化输出
printf '%s\t %s\t %s\t %s\n' $(cat 110) > 111 
#获得最新值MD5值的那列
grep "https://github.com/XTLS/Xray-core/commit/" 111 > 112
sleep 0.2
#获得最新值
new_ver=` cat 112 | cut -b 52-91`
echo $new_ver > new_ver

#if条件判断old_ver文件是否存在，不存在创建 -f 判断文件 -d 判断文件夹
if [ ! -f "old_ver" ]; then
  clear	
  echo "old_ver被删除正在创建！"
  sleep 0.1
  echo $new_ver > old_ver
fi
sleep 0.1 
old_ver=`cat old_ver`
if [ "$new_ver" = "$old_ver" ]; then
	clear
	echo "还没有最新的版本，请过段时间再试！"
	echo "no_update" > /home/lenyu/noxray
else
	clear
	echo "发现更新，准备切换到最新的commit分支MD5"
	echo "update" > /home/lenyu/noxray
	sleep 1
	#替换最新的md5值 sed要使用""才会应用变量
	sed -i "s/.*PKG_SOURCE_VERSION:.*/PKG_SOURCE_VERSION:=$new_ver/" /home/lenyu/lede/package/lean/xray/Makefile
fi
echo
###判断ssr+是否有更新-开始###
new_ssr=$(wget --no-check-certificate -qO- -t1 -T3 "https://github.com/fw876/helloworld/commits/master")
sleep 0.2
echo $new_ssr > ssr1
printf '%s\t %s\t %s\t %s\n' $(cat ssr1) > ssr2 
grep "https://github.com/fw876/helloworld/commits/master?after=" ssr2 > ssr3
sleep 0.1
#获得最新值
new_ssr=` cat ssr3 | cut -b 64-103`
echo $new_ssr > new_ssr
#if条件判断old_ssr文件是否存在，不存在创建 -f 判断文件 -d 判断文件夹
if [ ! -f "old_ssr" ]; then
  clear	
  echo "old_ssr被删除正在创建！"
  sleep 0.1
  echo $new_ssr > old_ssr
fi
sleep 0.1 
old_ssr=`cat old_ssr`
if [ "$new_ssr" = "$old_ssr" ]; then
	echo "no_update" > /home/lenyu/nossr
else
	echo "update" > /home/lenyu/nossr
	echo $new_ssr > old_ssr
fi
echo
###判断ssr+是否有更新-结束###
echo
###判断passwall是否有更新-开始###
new_passw=$(wget --no-check-certificate -qO- -t1 -T3 "https://github.com/xiaorouji/openwrt-passwall/commits/main")
sleep 0.2
echo $new_passw > passw1
printf '%s\t %s\t %s\t %s\n' $(cat passw1) > passw2
grep "https://github.com/xiaorouji/openwrt-passwall/commit/" passw2 > passw3
sleep 0.1
#获得最新值
new_passw=` cat passw3 | cut -b 70-109`
echo $new_passw > new_passw
#if条件判断old_ssr文件是否存在，不存在创建 -f 判断文件 -d 判断文件夹
if [ ! -f "old_passw" ]; then
  clear	
  echo "old_passw被删除正在创建！"
  sleep 0.1
  echo $new_passw > old_passw
fi
sleep 0.1 
old_passw=`cat old_passw`
if [ "$new_passw" = "$old_passw" ]; then
	echo "no_update" > /home/lenyu/nopassw
else
	echo "update" > /home/lenyu/nopassw
	echo $new_passw > old_passw
fi
echo
###判断passwall是否有更新-结束###
echo
#检查固件是否更新
cd /home/lenyu/lede
clear
echo "准备检查固件是否更新…"
s1=`git pull`
sleep 0.2
s2=`git -C ~/lede/package/luci-app-openclash  pull`
sleep 0.2
if [[ "$s1" = "Already up to date." ]]; then
	clear
	echo "lede、ssr+或passwall源码没有更新…"
	echo "no_update" > /home/lenyu/nolede
else
echo "update" > /home/lenyu/nolede
fi
echo
if [[ "$s2" = "Already up to date." ]]; then
	clear
	echo "luci-app-openclash源码没有更新…"
	echo "no_update" > /home/lenyu/noopclash
else
echo "update" > /home/lenyu/noopclash
fi
echo
clear
sleep 0.1
#总结判断之
nolede=`cat /home/lenyu/nolede`
noopclash=`cat /home/lenyu/noopclash`
noxray=`cat /home/lenyu/noxray`
nossr=`cat /home/lenyu/nossr`
nopassw=`cat /home/lenyu/nopassw`
sleep 0.2
if [[("$nolede" = "update") || ("$noopclash" = "update") || ("$noxray" = "update") || ("$nossr" = "update" ) || ("$nopassw"  = "update" )]]; then
	clear
	echo
	echo "发现更新，请稍后…"
	clear
	echo
	echo "准备开始编译最新固件…" 
	source /etc/environment && cd /home/lenyu/lede && git pull && git -C ~/lede/package/luci-app-openclash  pull && ./scripts/feeds update -a  && ./scripts/feeds install -a && make defconfig && make -j8 download && make -j10 V=s &&  bash rename.sh
	echo
	echo $new_ver > /home/lenyu/old_ver
	rm -rf /home/lenyu/noxray
	rm -rf /home/lenyu/noopclash
	rm -rf /home/lenyu/nolede
	rm -rf /home/lenyu/nossr
	rm -rf /home/lenyu/nopassw
	echo "固件编译成功，脚本退出！"
	echo
	exit 0
fi
echo
if [[("$nolede" = "no_update") && ("$noopclash" = "no_update") && ("$noxray" = "no_update") && ("$nossr" = "no_update" ) && ("$nopassw"  = "no_update" )]]; then
	clear
	echo
	echo "呃呃…检查lede/ssr+/xray/passwall/openclash源码，没有一个源码更新哟…还是稍安勿躁…" 
fi
#脚本结束，准备最后的清理工作	
echo $new_ver > /home/lenyu/old_ver
rm -rf /home/lenyu/noxray
rm -rf /home/lenyu/noopclash
rm -rf /home/lenyu/nolede
rm -rf /home/lenyu/nossr
rm -rf /home/lenyu/nopassw
echo
echo "脚本退出！"
echo
exit 0
