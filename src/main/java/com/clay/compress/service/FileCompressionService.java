package com.clay.compress.service;

import com.clay.compress.model.CompressionAlgorithm;
import com.clay.compress.model.FileVo;

import java.io.IOException;

public interface FileCompressionService {

    void uploadFile(FileVo fileVo) throws IOException;

    FileVo downloadFile(Long fileId) throws IOException;

    FileVo compressFile(CompressionAlgorithm compressionAlgorithm, FileVo fileVo) throws IOException;

    FileVo decompressFile(FileVo compressedFileVo) throws IOException;
}
