package com.yunfei.toolmaker.service;

import com.yunfei.toolmaker.service.param.FileSaveParam;

public interface FileService {
    void saveFile(FileSaveParam fileSaveParam) throws IllegalAccessException;
}
