package com.clay.compress.model;

import lombok.Getter;

@Getter
public enum CompressionAlgorithm {

    LZW("lzw"),
    DEFLATE("deflate"),
    RLE("rle"),
    BWT("bwt"),
    GZIP("gzip"),
    LZ4("lz4"),
    LZMA("lzma"),
    SNAPPY("snappy"),
    ZSTD("zstd");

    private final String algorithm;

    CompressionAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

}
