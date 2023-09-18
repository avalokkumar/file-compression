package com.clay.compress.service;

import java.io.IOException;

public interface Compression {

    byte[] compress(byte[] inputFile) throws IOException;

    byte[] decompress(byte[] compressedData) throws IOException;
}
