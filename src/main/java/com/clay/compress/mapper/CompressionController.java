package com.clay.compress.mapper;

import com.clay.compress.model.CompressionAlgorithm;
import com.clay.compress.model.FileVo;
import com.clay.compress.service.FileCompressionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Lempel-Ziv-Welch (LZW): LZW is another popular algorithm for lossless data compression.
 * It is used in formats like GIF and the UNIX compress command.
 * LZW works by replacing repeated sequences of characters with shorter codes.
 *
 * This Controller is responsible for handling the LZW Compression and Decompression.
 * It will
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class CompressionController {

    private final FileCompressionService compressionService;

    @Autowired
    public CompressionController(FileCompressionService compressionService) {
        this.compressionService = compressionService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty() || file.getOriginalFilename() == null) {
            return ResponseEntity.badRequest().body("Please select a valid file to upload.");
        }

        try {
            FileVo.FileVoBuilder fileVoBuilder = getFileVoBuilder(file);

            compressionService.uploadFile(fileVoBuilder.build());

            return ResponseEntity.status(204).body("File uploaded successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed.");
        }
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long fileId, HttpServletResponse response) {
        try {
            FileVo fileVo = compressionService.downloadFile(fileId);

            // Set response headers for file download
//            response.setHeader("Content-Disposition", "attachment; filename=\"downloaded-file\"");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileVo.getOriginalFileName()+"\"");
            response.setContentType(fileVo.getFileContentType()); // Set the correct content type
            response.setContentType("application/octet-stream");
            response.setContentLength(fileVo.getFileData().length);
            response.getOutputStream().write(fileVo.getFileData());

            return ResponseEntity.ok().body(fileVo.getFileData());
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/compress/{algorithm}")
    public ResponseEntity<FileVo> compressFile(@PathVariable("algorithm") String algorithm, @RequestParam("file") MultipartFile file) {

        try {
            if (file.isEmpty() || file.getOriginalFilename() == null) {
                return ResponseEntity.badRequest().body(null);
            }

            CompressionAlgorithm compressionAlgorithm = CompressionAlgorithm.valueOf(algorithm);

            FileVo.FileVoBuilder fileVoBuilder = getFileVoBuilder(file);

            FileVo compressedFileVo = compressionService.compressFile(compressionAlgorithm, fileVoBuilder.build());

            return ResponseEntity.ok(compressedFileVo);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/decompress")
    public ResponseEntity<FileVo> decompressFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty() || file.getOriginalFilename() == null) {
                return ResponseEntity.badRequest().body(null);
            }

            FileVo.FileVoBuilder fileVoBuilder = getFileVoBuilder(file);

            FileVo decompressedFileVo = compressionService.decompressFile(fileVoBuilder.build());

            return ResponseEntity.ok(decompressedFileVo);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private FileVo.FileVoBuilder getFileVoBuilder(MultipartFile file) throws IOException {
        FileVo.FileVoBuilder fileVoBuilder = FileVo.builder();
        fileVoBuilder.originalFileName(file.getOriginalFilename())
                .fileData(file.getBytes())
                .size(file.getSize())
                .fileExtension(getFileExtension(Objects.requireNonNull(file.getOriginalFilename())))
                .fileContentType(file.getContentType())
                .isCompressed(false)
                .isDeleted(false);
        return fileVoBuilder;
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex >= 0) {
            return fileName.substring(lastDotIndex);
        }
        return "";
    }
}
