package com.zhntd.opsmanager.service;

import com.zhntd.opsmanager.net.DataType;

interface IPermManagerService {
    void notifyOperation(int mode, int uid, String packageName, boolean remember, DataType dataType);
}