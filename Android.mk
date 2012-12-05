
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

#LOCAL_STATIC_JAVA_LIBRARIES := libarity android-support-v4

LOCAL_JAVA_LIBRARIES := core framework

LOCAL_SRC_FILES := $(call all-java-files-under, src)
LOCAL_SRC_FILES += \
	src/com/example/usermanagement/IUpdateListener.aidl \
	src/com/example/usermanagement/IUserSwitchService.aidl

LOCAL_SDK_VERSION := current

LOCAL_PACKAGE_NAME := UserManagement

LOCAL_DEX_PREOPT := false

LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)
##################################################
include $(CLEAR_VARS)

include $(BUILD_MULTI_PREBUILT)

# Use the folloing include to make our test apk.
include $(call all-makefiles-under,$(LOCAL_PATH))
