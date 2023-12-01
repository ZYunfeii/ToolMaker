package com.yunfei.toolmaker.service;

import com.yunfei.toolmaker.service.param.FileSaveParam;
import com.yunfei.toolmaker.service.response.FilesInfoResponse;
import com.yunfei.toolmaker.service.response.FileResponse;

public interface FileService {
    void saveFile(FileSaveParam fileSaveParam) throws IllegalAccessException;
    FilesInfoResponse getFilesInfoWithUserName(String userName);

    FileResponse getFileBytesWithFileName(String filename, String userName);

    String getUserNameFromFileName(String filename);

    void deleteFile(String filename);

    void appendFileBytes(Long id, byte[] bytes);

    Boolean existByUploadId(Long id);

    Boolean existByFileName(String filename);
}
