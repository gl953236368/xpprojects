#include <stdio.h>
#include <jni.h>

#include <android/log.h>
#include "include/inlineHook.h"
#include <sys/types.h>
#include <unistd.h>

#define LOG_TAG "ZY-TEST"
#define LOGD(fmt,args...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,fmt, ##args)

static unsigned long find_target_addr_of(char* soName){// 获取目标so的base_address
    char filename[32];
    char cmdline[256];

    sprintf(filename, "/proc/%d/maps", getpid());
    LOGD("filename = %s", filename);
    FILE *fp = fopen(filename, "r");
    unsigned long revalue = 0;
    if(fp){
        while(fgets(cmdline, 256, fp)){
            if(strstr(cmdline, soName) && strstr(cmdline, "r-xp")){ // 过滤目标so
                LOGD("cmdline = %s", cmdline);
                char *str = strstr(cmdline,"-");
                if(str){
                    *str='\0';
                    char num[32];
                    sprintf(num, "0x%s", cmdline);
                    revalue = strtoul(num, NULL, 0);
                    LOGD("revalue = %lu", revalue);
                    return revalue;
                }
            }
            memset(cmdline, 0, 256); // 清零
       }
       fclose(fp);
    }
    return 0L;
}

unsigned long func = NULL;  // 目标方法的 addr
int key_res[10] = {0}; // 用于存储key的全局变量 便于java层调用

void set_global_var(int *key){
    key_res[0] = *key;
    key_res[1] = *(key+1);
    key_res[2] = *(key+2);
    key_res[3] = *(key+3);
}

int *get_global_var(){
    LOGD("get global var success");
    return key_res;
}

int (*old_sub_5E274)(int *a1,int *key,int *encrypted_iv, int a4) = NULL; // hook函数声明

int new_TargetFunc(int *a1,int *key,int *encrypted_iv, int a4)
{ // hook 重定向 参数前仨是地址，最后一个是值
  int ret = old_sub_5E274(a1, key, encrypted_iv, a4);
  LOGD("hooked target(key) = %d/%d/%d/%d", *key, *(key+1), *(key+2), *(key+3));
  set_global_var(key);
  return ret;
}


int hookTargetFunc()
{ // hook 目标函数 并用新的函数替换
    LOGD("func = %x", func);
    // 参数
    // 1.目标函数地址
    // 2.hook替换函数地址
    // 3.目标函数备份地址
    if (registerInlineHook((uint32_t) func, (uint32_t) new_TargetFunc, (uint32_t **) &old_sub_5E274) != ELE7EN_OK) {
        return -1;
    }
    if (inlineHook((uint32_t) func) != ELE7EN_OK) {
        return -1;
    }
    LOGD("hookTargetFunc-------");
    return 0;
}

int unHookTargetFunc()
{//解除 hook环境
    if (inlineUnHook((uint32_t) func) != ELE7EN_OK) {
        return -1;
    }
    return 0;
}

int main(){

    LOGD("enter active call");

    // 目标so libnet_crypto.so
    unsigned long base = find_target_addr_of("libnet_crypto.so");
    LOGD("base = %x ", base);
    if (base > 0L) {
        func = base + 0x5e274 + 1; // so中对应方法的偏移地址
        void* func1 = (void*)(base + 0x5e274 + 1); // 代替方法的offset addr
        LOGD("FUNC = %x", func);
        int res = hookTargetFunc(); // 进行register 注入
        return res;
    }
    return 0;

}

// JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM*vm, void* reserved){
    // JNIONLOad 自动调用
//    LOGD("enter JNI_OnLoad");

    // 目标so libnet_crypto.so
//	unsigned long base = find_target_addr_of("libnet_crypto.so");

//    LOGD("base = %x ", base);

//    if (base > 0L) {
//        func = base + 0x5e274 + 1; // so中对应方法的偏移地址
//		void* func1 = (void*)(base + 0x5e274 + 1); // 代替方法的offset addr
//        LOGD("FUNC = %x", func);
//		hookTargetFunc(); // 进行register 注入
//    }

//  return JNI_VERSION_1_6;
//}