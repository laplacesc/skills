import sys

from scapy.layers.inet import ICMP, IP
from scapy.sendrecv import sr1


def ping(src_ip, dst_ip, data):
    """
    通过中间主机发送ICMP Echo Reply并接收响应。

    :param src_ip: 源主机IP地址
    :param dst_ip: 目标主机IP地址
    :param data: 发送的数据
    """
    # 构建ICMP Echo Reply数据包
    packet = IP(src=src_ip, dst=dst_ip) / ICMP(type=0) / data

    # 发送数据包并等待响应
    response = sr1(packet, timeout=2)

    if response:
        print(f"从 {dst_ip} 收到响应: {response.summary()}")
    else:
        print(f"未从 {dst_ip} 收到响应")


if __name__ == '__main__':
    ping(sys.argv[1], sys.argv[2], sys.argv[3])

# python3 ping.py 10.182.139.97 10.182.220.101 eRYw9YGrGF6/EHxCaNus5CIzMpeAsbrh6T5aSe2k54Ox5XwiuIJkhK0I0KbuIO6RVyipnnFWhwmOmBkD1k5anx1UMvcY+mzCHt97hzO14b8UaMTC9zTImiBbWPD7eIWi
