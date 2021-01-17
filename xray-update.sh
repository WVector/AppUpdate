#/usr/bin/bash
#获取当前绝对路径
#echo "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )" #/home/lenyu
cd /home/lenyu
echo
echo "准备获取最新的xray源码版本…"
echo
sh_new_ver=$(wget --no-check-certificate -qO- -t1 -T3 "https://github.com/XTLS/Xray-core/commits/main")
echo $sh_new_ver > 110.txt
sleep 2
# 把文本内容格式化输出
printf '%s\t %s\t %s\t %s\n' $(cat 110.txt) > 111 
#获得最新值1048列有待验证？
echo `sed '1048!d'   /home/lenyu/111` >112

sleep 0.2
#获得最新值
new_ver=` cat 112 | cut -b 53-92`
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
	echo $new_ver > old_ver
	sleep 0.1
	echo "#请检查脚本可用性，否则按任意键,开始编译固件…"
#	read
nolede=`cat /home/lenyu/nolede`
noopclash=`cat /home/lenyu/noopclash`
noxray=`cat /home/lenyu/noxray`
sleep 0.2
if [[("$nolede" = "update") || ("$noopclash" = "update") || ("$noxray" = "update")]]; then
	clear
	echo
	echo "发现更新，请稍后…"
	clear
	echo
	echo "准备开始编译最新固件…" 
	source /etc/environment && cd /home/lenyu/lede && git pull && git -C ~/lede/package/luci-app-openclash  pull && ./scripts/feeds update -a  && ./scripts/feeds install -a && make defconfig && make -j8 download && make -j10 V=s &&  bash rename.sh
fi
echo
if [[("$nolede" = "no_update") && ("$noopclash" = "no_update") && ("$noxray" = "no_update")]]; then
	clear
	echo
	echo "呃呃…检查lede/ssr+/passwall/openclash源码，没有一个源码更新哟…还是稍安勿躁…" 
fi
#脚本结束，准备最后的清理工作	
cd /home/lenyu
rm -rf /home/lenyu/noxray
rm -rf /home/lenyu/noopclash
rm -rf /home/lenyu/nolede
echo
echo "脚本退出！"
echo
sleep 1
#clear
exit 0
