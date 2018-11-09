#!/bin/bash

basepath=$(cd `dirname $0`; pwd)

# echo ${basepath}

work_path=$(pwd)

echo ${work_path}


cd ${work_path}  # 当前位置跳到脚本位置
   # 取到脚本目录

# pwd

path=$1
files=$(ls $path)



# 判断是否是文件夹或者文件
function dirFile(){

   # echo "文件夹："+${files}

    for filename in $files
    do

       if [ -d $filename ]; then
            echo "#######  文件夹  ###### ",$filename

       elif [ -f $filename ];then
            echo "文件 ：",$filename
       else
         echo "不知道是什么鬼"+$filename

       fi
    done

}


dirFile $files;







