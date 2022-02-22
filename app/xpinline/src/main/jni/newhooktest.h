//
//
//
#include <jni.h>

#ifndef _Included_com_px_xpinline_hook_HookEntity
#define _Included_com_px_xpinline_hook_HookEntity
#ifdef __cplusplus
extern "C" {
#endif
// hook 开关
JNIEXPORT jint JNICALL Java_com_px_xpinline_hook_HookEntity_hookMain
  (JNIEnv *, jclass);

// 返回指定位置的值
JNIEXPORT jstring JNICALL Java_com_px_xpinline_hook_HookEntity_testFunc
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
