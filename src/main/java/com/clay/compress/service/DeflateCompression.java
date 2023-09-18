package com.clay.compress.service;

import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Service
class DeflateCompression implements Compression {

    @Override
    public byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream compressedStream = new ByteArrayOutputStream();
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();

        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            compressedStream.write(buffer, 0, count);
        }

        deflater.end();
        compressedStream.close();
        return compressedStream.toByteArray();
    }

    @Override
    public byte[] decompress(byte[] compressedData) throws IOException {
        ByteArrayOutputStream decompressedStream = new ByteArrayOutputStream();
        Inflater inflater = new Inflater();
        inflater.setInput(compressedData);

        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
            try {
                int count = inflater.inflate(buffer);
                decompressedStream.write(buffer, 0, count);
            } catch (Exception e) {
                throw new IOException("Error decompressing the data.", e);
            }
        }

        inflater.end();
        decompressedStream.close();
        return decompressedStream.toByteArray();
    }
}
