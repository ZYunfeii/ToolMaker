package com.yunfei.toolmaker.service.impl;

import com.yunfei.toolmaker.dao.FileDao;
import com.yunfei.toolmaker.po.FileDo;
import com.yunfei.toolmaker.service.FileService;
import com.yunfei.toolmaker.service.param.FileSaveParam;
import com.yunfei.toolmaker.util.CommonUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
