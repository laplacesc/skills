package main

/*
GO 程序调用C的接口的SO案例
根据本地SO和头文件存放的路径调整如下的CGO 路径
*/

/*
#cgo CFLAGS: -I./lib
#cgo LDFLAGS: -L./lib -ltxsdk -Wl,-rpath,lib
#include "libtxsdk.h"
*/
import "C"
import (
	"fmt"
	"unsafe"
)

// 情报升级函数
func sdkUpgradeTest() {
	//情报升级,各情报库的绝对路径通过逗号分隔
	upgradeFiles := "./testdata/META-IOC_sdk_all_20241211_1733850309_v3.0.dat"
	code := C.TxDataUpGrade(C.CString(upgradeFiles), C.CString("ALL"))
	fmt.Println("upgrade:", code)
}

// 情报匹配函数
func detectorTest() {
	//建议返回缓冲区设置在2048字节或更大
	dst1 := make([]byte, 2048)

	//第一个参数是iType,第二个参数为待检测的IP或域名数据，第三个参数为检测模式，第四个参数为返回值缓冲区，第五个参数为输入的缓冲区的长度
	C.TxSdkDetector(C.CString("2"), C.CString("dns.companymartec.com"), C.CString("2"), (*C.char)(unsafe.Pointer(&dst1[0])), C.int(2048))
	fmt.Println("active res:", string(dst1))
}

/*测试主函数入口*/
func main() {
	//第一步：先获取本机设备指纹
	fmt.Println(C.GoString(C.TxGetDeviceFingerprint()))

	//第二步：将腾讯分发的授权license文件存放在授权文件目录，建议放到和LIB目录的同级目录（没有这个目录则手动创建一个license目录）（没有文件找腾讯接口人获取）
	//第三步：根据第一步获取的设备指纹和第二步腾讯分发的授权文件APPKEY到云端去申请激活码文件，将激活码文件放到本地的激活文件存放目录
	//----获取激活码的方式参考对接文档的第六章节
	//----激活文件目录可以在config.yaml文件中authorize_file_path进行调整，建议放到和LIB目录的同级目录
	//----获取APPKEY的方式可以参考对接文档的3.5章节

	//第四步：初始化SDK,第一个参数为腾讯分发的授权文件的路径，第二个为配置文件存放的路径（用户可以根据实际情况调整）
	code := C.TxSdkInit(C.CString("./testdata/PcMgrLicense-C231-LP92-7K85-XJXC.qcer", "./testdata/config.yaml"))
	fmt.Println("code:", code)

	//第五步：根据腾讯分发的授权文件的APPKEY到云端去自动下载情报库，情报支持种类package_type 由腾讯侧决定，将下载好的情报库存在在DB目录。
	//----自动获取升级包的方式参考对接文档的第五章节
	//----DB文件目录可以在config.yaml文件中db_path进行调整，建议放到和LIB目录的同级目录

	//第六步：数据升级，第一次和升级的时候执行一次就行了，后面执行第七步数据加载函数即可
	sdkUpgradeTest()

	//第七步：数据加载
	//----加载meta和info数据
	code = C.TxInitMetaData()
	fmt.Println("init meta:", code)
	code = C.TxInitInfoData()
	fmt.Println("init info:", code)

	//第八步：数据匹配
	detectorTest()

	//第九步：资源释放
	C.TxSdkDestroy()
}
