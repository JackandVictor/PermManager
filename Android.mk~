LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_SRC_FILES += \
    src/com/zhntd/opsmanager/service/IPermManagerService


LOCAL_STATIC_JAVA_LIBRARIES := \
    android-common \
    guava \
    android-support-v13 \
    android-support-v4 \
    android-ex-variablespeed \


LOCAL_PACKAGE_NAME := PermManager
LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)

include $(call all-makefiles-under,$(LOCAL_PATH))
