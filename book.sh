#!/usr/bin/env bash



pwd_path=/kobe/learn/project/java/study-notes/java/concurrency-thread/java.util.concurrent包源码阅读及其原理

cd $pwd_path

work_path=$(pwd)

#echo ${work_path}

cd ${work_path}

path=$1

files=$(ls $path)


for filename in $files
    do

        if [ "${filename##*.}"x = "md"x ];then

            echo $filename
            fileName=${filename%.*}
            pandoc --template=custom_default.html5 -s $filename  -o /kobe/learn/project/java/study-notes/book/html/$fileName.html -V mainfont="STHeiti" --toc  --metadata title=$fileName  -H /kobe/learn/project/java/study-notes/tools/header.html
            break
        fi

    done