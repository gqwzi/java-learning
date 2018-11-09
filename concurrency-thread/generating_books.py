#!/usr/bin/env python3
# -*- coding: utf-8 -*-
# @Time    : 2018/9/6 23:27
# @Author  : pankui
# @Site    : https://github.com/pankui
# @File    : demo.py
# @Software: PyCharm
# python 获取文件生成html 转换 mobi



import subprocess

import os

# 返回当前工作路径：os.getcwd()
rootdir = os.getcwd()

pathname = []

for (dirpath, dirnames, filenames) in os.walk(rootdir):
    for filename in filenames:
        # 输出.md 结尾的文件，
        # print("###########  输出路径:", dirpath)
        # print("输出文件夹名称:", dirnames)
        # for dir in dirnames:
        # print("#***** 文件夹",dir)
        # print("## 文件夹路径",dirpath)
        if (os.path.splitext(filename)[1] == '.md'):
            str = dirpath + "/" + filename
            print("文件绝对路径:", str)
            if filename == "CAS原理.md":
                file = dirpath + "/CAS原理.md"
                print(file)
                #p1 = subprocess.Popen(['ping', '-c 2', host], stdout=subprocess.PIPE)
                #p1 = subprocess.Popen(['pandoc', file + " -o CAS原理.html -V mainfont='STHeiti'"], stdout=subprocess.PIPE)
                #output = p1.communicate()[0]
                #print(output)
                subprocess.call(["pandoc ", file + " -o CAS原理.html -V mainfont='STHeiti'"])
            # print("输出文件名:", filename)
            # print("当前路径 ######", subprocess.check_call('ls'))
            # subprocess.check_call(['your_command', 'arg 1', 'arg 2'], cwd=working_dir)
            # subprocess.check_call('cd ', shell=True, cwd=dirpath)
            # print("路径=====", subprocess.check_call('ls'))
