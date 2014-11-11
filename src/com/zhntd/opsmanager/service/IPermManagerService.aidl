package com.zhntd.opsmanager.service;

interface IPermManagerService {
    void notifyOperation(int mode, int uid, String packageName, boolean remember, int type);
    void showFibDialog(int uid, String packageName, String permName, int type);
}