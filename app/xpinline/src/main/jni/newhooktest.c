//
//
//

#include "newhooktest.h"
#include <jni.h>
#include <stdio.h>
#include "include/inlineHook.h"
#include "hooktest.h"


JNIEXPORT jint JNICALL Java_com_px_xpinline_hook_HookEntity_hookMain
  (JNIEnv* env, jclass jc){
    int res = main(); // hook开启
    return res;
  }


JNIEXPORT jstring JNICALL Java_com_px_xpinline_hook_HookEntity_testFunc
  (JNIEnv* env, jclass jc){
    const int *key = NULL;
    key = get_global_var(); // 获得目标地址
    char buff[1288] = {0}; // 初始化一个string buffer
    if(key == NULL){
        return NULL;
    }
    sprintf(buff, "%X/%X/%X/%X", *key, *(key+1), *(key+2), *(key+3)); // 隔断写进buff 留给java层处理
    return (*env)->NewStringUTF(env, buff);
  }