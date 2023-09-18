package com.clay.compress.mapper;

import com.clay.compress.entity.File;
import com.clay.compress.model.FileVo;

public class FileMapper {

    public static File map(FileVo fileVo) {
        return File.builder()
                .originalFilename(fileVo.getOriginalFileName())
                .data(fileVo.getFileData())
                .extension(fileVo.getFileExtension())
                .size(fileVo.getSize())
                .contentType(fileVo.getFileContentType())
                .isCompressed(fileVo.isCompressed())
                .isDeleted(fileVo.isDeleted())
                .build();
    }

    public static FileVo map(File file) {
        return FileVo.builder()
                .originalFileName(file.getOriginalFilename())
                .fileData(file.getData())
                .fileExtension(file.getExtension())
                .fileContentType(file.getContentType())
                .isCompressed(file.isCompressed())
                .isDeleted(file.isDeleted())
                .build();
    }
}
