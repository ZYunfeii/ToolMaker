package com.yunfei.toolmaker.service;

import com.yunfei.toolmaker.service.param.FileSaveParam;
import com.yunfei.toolmaker.service.response.FilesInfoResponse;
import com.yunfei.toolmaker.service.response.FileResponse;

public interface FileService {
    void saveFile(FileSaveParam fileSaveParam) throws IllegalAccessException;
    FilesInfoResponse getFilesInfoWithUserName(String userName);

    FileResponse getFileBytesWithFileName(String filename);

    String getUserNameFromFileName(String filename);

    void deleteFile(String filename);
}
