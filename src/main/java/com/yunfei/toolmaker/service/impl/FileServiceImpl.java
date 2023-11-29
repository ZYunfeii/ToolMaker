package com.yunfei.toolmaker.service.impl;

import com.yunfei.toolmaker.dao.FileDao;
import com.yunfei.toolmaker.po.FileDo;
import com.yunfei.toolmaker.service.FileService;
import com.yunfei.toolmaker.service.param.FileSaveParam;
import com.yunfei.toolmaker.service.response.FilesInfoResponse;
import com.yunfei.toolmaker.service.response.FileResponse;
import com.yunfei.toolmaker.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
public class FileServiceImpl implements FileService {
    @Autowired
    private FileDao fileDao;
    @Override
    public void saveFile(FileSaveParam fileSaveParam) throws IllegalAccessException {
        if (CommonUtils.checkEntityExistNullField(fileSaveParam)) {
            throw new IllegalArgumentException("FileSaveParam's field should not be null!");
        }
        FileDo fileDo = new FileDo();
        BeanUtils.copyProperties(fileSaveParam, fileDo);
        fileDao.insert(fileDo);
    }

    @Override
    public FilesInfoResponse getFilesInfoWithUserName(String userName) {
        FileDo fileDo = new FileDo();
        fileDo.setUserName(userName);
        List<FileDo> querys = fileDao.queryWithoutPayload(fileDo);
        FilesInfoResponse filesInfoResponse = new FilesInfoResponse();
        for (FileDo q : querys) {
            FilesInfoResponse.FileInfo fileInfo = new FilesInfoResponse.FileInfo();
            fileInfo.setType(q.getType());
            double size = q.getSize().doubleValue() / 1024 / 1024;
            size = Double.parseDouble(String.format("%.2f", size));
            fileInfo.setSize(size);
            fileInfo.setFileName(q.getFileName());
            filesInfoResponse.getFileInfoList().add(fileInfo);
        }
        return filesInfoResponse;
    }

    @Override
    public FileResponse getFileBytesWithFileName(String filename) {
        FileDo fileDo = new FileDo();
        fileDo.setFileName(filename);
        List<FileDo> querys = fileDao.query(fileDo);
        if (querys.isEmpty()) {
            log.info("{} doesn't exist.", filename);
            return null;
        }
        FileResponse fileResponse = new FileResponse();
        FileDo entity = querys.get(0);
        fileResponse.setFileName(entity.getFileName());
        fileResponse.setPayload(entity.getPayload());
        fileResponse.setUserName(entity.getUserName());
        fileResponse.setSize(entity.getSize());

        return fileResponse;
    }

    @Override
    public String getUserNameFromFileName(String filename) {
        FileDo fileDo = new FileDo();
        fileDo.setFileName(filename);
        List<FileDo> querys = fileDao.queryWithoutPayload(fileDo);
        if (querys.isEmpty()) {
            log.info("{} doesn't exist.", filename);
            return null;
        }
        return querys.get(0).getUserName();
    }

    @Override
    public void deleteFile(String filename) {
        FileDo fileDo = new FileDo();
        fileDo.setFileName(filename);
        fileDao.delete(fileDo);
    }
}
