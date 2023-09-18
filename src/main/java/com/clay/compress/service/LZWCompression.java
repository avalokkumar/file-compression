package com.clay.compress.service;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
class LZWCompression implements Compression {

    /**
     * Lempel-Ziv-Welch (LZW): LZW is another popular algorithm for lossless data compression.
     * It is used in formats like GIF and the UNIX compress command.
     * LZW works by replacing repeated sequences of characters with shorter codes.
     *
     * @param data
     * @return
     * @throws IOException
     */
    @Override
    public byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream compressedStream = new ByteArrayOutputStream();
        Map<String, Integer> dictionary = new HashMap<>();

        // Initialize the dictionary with ASCII characters (codes 0 to 255)
        for (int i = 0; i < 256; i++) {
            dictionary.put(String.valueOf((char) i), i);
        }

        int nextCode = 256; // Next available code
        int codeSize = 9;   // Initial code size

        StringBuilder currentString = new StringBuilder();

        for (byte b : data) {
            currentString.append((char) (b & 0xFF)); // Convert to 8-bit character representation.
            if (!dictionary.containsKey(currentString.toString())) {
                int code = dictionary.get(currentString.substring(0, currentString.length() - 1));
                writeCodeToStream(code, codeSize, compressedStream);
                if (nextCode < (1 << codeSize)) {
                    dictionary.put(currentString.toString(), nextCode++);
                }
                currentString = new StringBuilder(String.valueOf((char) (b & 0xFF)));
            }
        }

        if (currentString.length() > 0) {
            int code = dictionary.get(currentString.toString());
            writeCodeToStream(code, codeSize, compressedStream);
        }

        compressedStream.close();
        return compressedStream.toByteArray();
    }

    /**
     * This method is responsible for decompressing the data.
     * It does so by reading the compressed data byte by byte and then reconstructing the dictionary.
     *
     * @param compressedData
     * @return
     * @throws IOException
     */
    @Override
    public byte[] decompress(byte[] compressedData) throws IOException {
        ByteArrayOutputStream decompressedStream = new ByteArrayOutputStream();
        Map<Integer, String> dictionary = new HashMap<>();
        int nextCode = 256; // Initial dictionary size for 8-bit characters
        int codeSize = 9;  // Initial code size

        int currentCode = 0;
        StringBuilder currentString = new StringBuilder();

        for (int i = 0; i < compressedData.length; i++) {
            int code = 0;
            for (int j = 0; j < codeSize; j++) {
                code |= ((compressedData[i] >> j) & 0x01) << j;
            }

            String entry = dictionary.get(code);
            if (entry == null) {
                entry = currentString.toString();
            }

            for (char character : entry.toCharArray()) {
                decompressedStream.write(character);
            }

            if (!dictionary.containsKey(currentString.toString())) {
                dictionary.put(nextCode++, currentString.toString());
            }

            if (nextCode >= (1 << codeSize)) {
                codeSize++;
            }

            currentString = new StringBuilder(entry);
        }

        decompressedStream.close();
        return decompressedStream.toByteArray();
    }

    private static void writeCodeToStream(int code, int codeSize, ByteArrayOutputStream stream) throws IOException {
        // Write a variable-length code to the stream
        for (int i = codeSize - 1; i >= 0; i--) {
            stream.write((code >> i) & 0x01);
        }
    }
}