import json

import requests


# author JunXian Wu
# created 2025-07-02

def device_push_rule_add(username, password):
    session = requests.Session()
    try:
        print("正在登录云瞻...")
        response = session.post(
            f'https://user.hillstonenet.com.cn/oauth/open/login?account={username}&passwd={password}&captchaValue=&captchaKey=',
            verify=True
        )
        response = session.post(
            f"https://user.hillstonenet.com.cn/oauth/authorize?account={username}&passwd={response.json().get('data').get('context')}",
            verify=True
        )
        response = session.get(
            'https://user.hillstonenet.com.cn/oauth/authorize?client_id=3&response_type=code&redirect_uri=https://ti.hillstonenet.com.cn/management&lang=zh_CN&state=aHR0cDovL3RpLmhpbGxzdG9uZW5ldC5jb20uY24vbWFuYWdlbWVudA==',
            verify=True
        )

        response = session.get(
            "https://ti.hillstonenet.com.cn/user/status",
            verify=True
        )
        print("获取登录状态：" + response.text)
    except Exception as e:
        print("登录失败：" + str(e))
        exit(1)

    with open('device_push_rule.json', 'r') as fcc_file:
        rules = json.load(fcc_file)

    for rule in rules:
        try:
            print("\n正在处理规则：", rule)
            response = session.post(
                "https://ti.hillstonenet.com.cn/management/hotthreat/device-model",
                json=rule
            )
            response.raise_for_status()
            print("处理结果：" + response.json())
        except Exception as e:
            print("处理结果：" + response.text if response else str(e))


if __name__ == '__main__':
    device_push_rule_add(
        'joker001',
        'QcJSRmWGd0s%2BoN93Vor2oK%2BKVVHfUXb5vaxlh8kUP%2BnR%2FiHuTtmuXAiUzqudhKc0HAvgG22HpDgFm6t06VXFA%2Fp2QGG07wn36NMvk%2Fvwjpm1T%2Frnjei5RkW0BOmQBuQ137wK%2F5c4YlVvNuN4HgLNFprIM3j8FAZY7eyKOSja%2BnuneYbEbErG0KxfStUCBo7bzePEMycYNp3ADSrcfEQUWRsoMv7%2FOqAQbUHlxFKe6%2BSa0Iluxr8sSuDdBDd%2FeL%2F2m3MwVnAeRAqe7Rz%2FOidS9IAFiBiuKbgtrzmggCUCHAZ0TRACwtQzf%2FUFVqz%2B6gyAtFoFKYq5fqZ8S0r0FGKvdg%3D%3D'
    )
