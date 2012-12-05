This must be pre-installed application.

This must signing with platform key, because ActivityManagerNative#swichUser needs 
"android.permission.INTERACT_ACROSS_USERS_FULL", it's protection level is "signature".
Easy way to platform signing is this build under AOSP's /packages/apps directory.
