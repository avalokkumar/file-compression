package com.clay.compress.service;

import com.clay.compress.entity.File;
import com.clay.compress.mapper.FileMapper;
import com.clay.compress.model.CompressionAlgorithm;
import com.clay.compress.model.FileVo;
import com.clay.compress.repository.FileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Service
public class FileCompressionServiceImpl implements FileCompressionService {

    private FileRepository fileRepository;


    private Map<CompressionAlgorithm, Compression> compressionMap;
    public FileCompressionServiceImpl(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @PostConstruct
    public void init() {
        compressionMap = new HashMap<>();
        compressionMap.put(CompressionAlgorithm.LZW, new LZWCompression());
        compressionMap.put(CompressionAlgorithm.DEFLATE, new DeflateCompression());
    }

    @Override
    public void uploadFile(FileVo fileVo) throws IOException {
        fileRepository.save(FileMapper.map(fileVo));
    }

    @Override
    @Transactional(readOnly = true)
    public FileVo downloadFile(Long fileId) throws IOException {
        // Fetch the file data from the repository based on the fileId
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found with ID: " + fileId));

        return FileMapper.map(file);
    }

    @Override
    public FileVo compressFile(CompressionAlgorithm compressionAlgorithm, FileVo fileVo) throws IOException {

        Compression compression = compressionMap.get(compressionAlgorithm);
        byte[] compressedData = compression.compress(fileVo.getFileData());
        FileVo.FileVoBuilder compressedFileVo = FileVo.builder();

        compressedFileVo.originalFileName(fileVo.getOriginalFileName())
                .fileData(compressedData)
                .size((long) compressedData.length)
                .fileExtension(fileVo.getFileExtension())
                .fileContentType(fileVo.getFileContentType())
                .isCompressed(true)
                .isDeleted(false);

        FileVo response = compressedFileVo.build();
        uploadFile(response);

        return response;
    }

    @Override
    public FileVo decompressFile(FileVo compressedFileVo) throws IOException {
        Inflater inflater = new Inflater();
        inflater.setInput(compressedFileVo.getFileData());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];

        while (!inflater.finished()) {
            try {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            } catch (Exception e) {
                throw new IOException("Error decompressing the file data.", e);
            }
        }

        inflater.end();
        outputStream.close();

        FileVo.FileVoBuilder decompressedFileVo = FileVo.builder();
        decompressedFileVo.originalFileName(compressedFileVo.getOriginalFileName())
                .fileData(outputStream.toByteArray())
                .size((long) outputStream.size())
                .fileExtension(compressedFileVo.getFileExtension())
                .fileContentType(compressedFileVo.getFileContentType())
                .isCompressed(false)
                .isDeleted(false);

        FileVo response = decompressedFileVo.build();
        uploadFile(response);
        return response;
    }
}
