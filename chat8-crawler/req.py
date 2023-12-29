# -*- coding: utf-8 -*-
import requests
import execjs
import sys
import os
import io

def chat8Api(question):

    # 设置不使用代理的环境变量
    os.environ['no_proxy'] = '*'

    headers = {
        'Accept': 'application/json, text/plain, */*',
        'Accept-Language': 'zh-CN,zh;q=0.9,en-GB;q=0.8,en-US;q=0.7,en;q=0.6',
        'Authorization': '7XgxWDjc8OxFCB8YvKTUUvdeVQyzrgYvNQmv6ctWHEA=',
        'Connection': 'keep-alive',
        'Content-Type': 'application/json',
        'Origin': 'https://chat.chat826.com',
        'Referer': 'https://chat.chat826.com/',
        'Sec-Fetch-Dest': 'empty',
        'Sec-Fetch-Mode': 'cors',
        'Sec-Fetch-Site': 'cross-site',
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36',
        'sec-ch-ua': '"Google Chrome";v="119", "Chromium";v="119", "Not?A_Brand";v="24"',
        'sec-ch-ua-mobile': '?0',
        'sec-ch-ua-platform': '"Windows"',
    }

    json_data = {
        'version': '1.1.1',
        'os': 'pc',
        'language': 'zh',
        'pars': {
            'user_id': '589487',
            'question': question,
            'group_id': '701078236',
            'question_id': '',
            'server_id': '1',
        },
    }

    response = requests.post('https://chatapi.chat86.cn/go/api/steam/see', headers=headers, json=json_data)
    encoding = response.encoding
    magicStr = response.content.decode(encoding)
    magicStr = magicStr[1:-1]
#     print(magicStr)# Note: json_data will not be serialized by requests

    # exactly as it was in the original request.
    #data = '{"version":"1.1.1","os":"pc","language":"zh","pars":{"user_id":"589487","question":"hahahha","group_id":"701078236","question_id":"","server_id":"1"}}'
    #response = requests.post('https://chatapi.chat86.cn/go/api/steam/see', headers=headers, data=data)

    # 读取JavaScript脚本文件
    with open('decode.js', 'r') as file:
        script_code = file.read()

    # 创建一个执行环境
    ctx = execjs.compile(script_code)

    # 调用JavaScript函数
    result = ctx.call("decrypt", magicStr)

    # print(result)

    token = "7XgxWDjc8OxFCB8YvKTUUvdeVQyzrgYvNQmv6ctWHEA="

    headers = {
        'Accept': 'text/event-stream',
        'Accept-Language': 'zh-CN,zh;q=0.9,en-GB;q=0.8,en-US;q=0.7,en;q=0.6',
        'Cache-Control': 'no-cache',
        'Connection': 'keep-alive',
        'Origin': 'https://chat.chat826.com',
        'Referer': 'https://chat.chat826.com/',
        'Sec-Fetch-Dest': 'empty',
        'Sec-Fetch-Mode': 'cors',
        'Sec-Fetch-Site': 'cross-site',
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36',
        'sec-ch-ua': '"Google Chrome";v="119", "Chromium";v="119", "Not?A_Brand";v="24"',
        'sec-ch-ua-mobile': '?0',
        'sec-ch-ua-platform': '"Windows"',
    }

    params = {
        'question_id': result['data']['question_id'],
        'group_id': '701078236',
        'user_id': '589487',
        'token': token,
        'server_id': '1',
    }

    response = requests.get('https://chatapi.chat86.cn/go/api/event/see', params=params, headers=headers, stream=True)

    # print(response.content)

    response_content = response.content

    # 解码为字符串
    response_str = response_content.decode('utf-8')

    # 初始化一个列表，用于存储提取到的内容
    data_parts = []

    # 按行分割字符串，提取Data和Status之间的内容
    for line in response_str.split('\n'):
        if line.startswith('data:'):
            data_start_index = line.find('{"Data":') + len('{"Data":') + 1
            status_start_index = line.find('"Status":') - 2
            data_status_part = line[data_start_index:status_start_index]
            if data_status_part == '':
                continue
            data_parts.append(data_status_part)

    # 拼接提取到的内容为一个字符串
    result = ''.join(data_parts)

    # 打印拼接后的字符串
    # print(result)
    return result


if __name__ == "__main__":
    filePath = sys.argv[0]
    workDir = os.path.dirname(filePath)
    arg = sys.argv[1]
    os.chdir(workDir)
    res = chat8Api(arg)
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer,encoding='utf8')
    print(res)