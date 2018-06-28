# coding=utf-8
import os


def countFileLines(filename):
    count = 0
    handle = open(filename, 'rb')
    for line in handle:
        count += 1
    return count


def listdir(dir, lines):
    files = os.listdir(dir)  # 列出目录下的所有文件和目录
    for file in files:
        filepath = os.path.join(dir, file)
        if os.path.isdir(filepath):  # 如果filepath是目录，递归遍历子目录
            listdir(filepath, lines)
        elif os.path:  # 如果filepath是文件，直接统计行数
            if os.path.splitext(file)[1] == '.java' or os.path.splitext(file)[1] == '.xml':
                lines.append(countFileLines(filepath))
                #                print(file + ':'+str(countFileLines(filepath)))


lines = []
my_dir = 'D:/Work/AndroidProject/SoraShiroApp/CloudCat/app/src/main'
listdir(my_dir, lines)
print('Android Dir: ' + my_dir + '\ntotal lines: ' + str(sum(lines)))

python_dir = ['D:/Work/PythonProject/cloudcatBn/cloudcatBn',
              'D:/Work/PythonProject/cloudcatBn/normalusers',
              'D:/Work/PythonProject/cloudcatBn/poster',
              'D:/Work/PythonProject/cloudcatBn/tools']
sum_line = 0
for dir in python_dir:
    my_dir = dir
    listdir(my_dir, lines)
    sum_line += sum(lines)
print('Python Dir: D:/Work/PythonProject/cloudcatBn/' + '\ntotal lines: ' + str(sum_line))

check_str = 'sd.j.jgp.jpg'
point_index = check_str.rfind('.')
suffix = check_str[point_index:]
print suffix
