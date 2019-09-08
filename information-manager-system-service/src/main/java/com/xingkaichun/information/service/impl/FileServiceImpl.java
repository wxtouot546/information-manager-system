package com.xingkaichun.information.service.impl;

import com.xingkaichun.information.dao.FileDao;
import com.xingkaichun.information.dto.file.FileDto;
import com.xingkaichun.information.model.FileDomain;
import com.xingkaichun.information.service.FileService;
import com.xingkaichun.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service(value = "fileService")
public class FileServiceImpl implements FileService {

    @Autowired
    private FileDao fileDao;

    private static String host;
    @Value("${application.host}")
    public void setHost(String host) {
        this.host = host;
    }
    private static String port;
    @Value("${server.port}")
    public void setPort(String port) {
        this.port = port;
    }

    @Override
    public int addFile(FileDomain fileDomain) {
        return fileDao.addFile(fileDomain);
    }

    @Override
    public List<FileDto> queryFile(FileDomain fileDomain) {
        List<FileDomain> fileDomainList = fileDao.queryFile(fileDomain);
        return classCast(fileDomainList);
    }

    public static List<FileDto> classCast(List<FileDomain> fileDomainList) {
        if(CommonUtils.isNUllOrEmpty(fileDomainList)){
            return null;
        }
        List<FileDto> fileDtoList = new ArrayList<>();
        fileDomainList.forEach(fileDomain -> fileDtoList.add(classCast(fileDomain)));
        return fileDtoList;
    }

    public static FileDto classCast(FileDomain fileDomain) {
        FileDto fileDto = new FileDto();
        fileDto.setFileId(fileDomain.getFileId());
        fileDto.setFileName(fileDomain.getFileName());
        fileDto.setFileDescrible(fileDomain.getFileDescrible());
        fileDto.setDownPath("http://" + host + ":"+ port +"/File/FileDownload?fileId=" + fileDomain.getFileId());
        fileDto.setFilePath("http://" + host + ":"+ port +"/File/FileDownload?fileId=" + fileDomain.getFileId());
        return fileDto;
    }

    @Override
    public FileDto queryFileByFileIdReturnDto(String fileId) {
        List<FileDto> fileDtoList = queryFile(new FileDomain(fileId,null,null,null,null));
        if(CommonUtils.isNUllOrEmpty(fileDtoList)){
            return null;
        }
        return fileDtoList.remove(0);
    }

    @Override
    public FileDomain queryFileByFileIdReturnDomain(String fileId) {
        List<FileDomain> fileDomainList = fileDao.queryFile(new FileDomain(fileId,null,null,null,null));
        if(CommonUtils.isNUllOrEmpty(fileDomainList)){
            return null;
        }
        return fileDomainList.get(0);
    }
}