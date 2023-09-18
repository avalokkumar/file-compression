package com.clay.compress.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileVo {
    private String originalFileName;        //for example: test.jpg

    @JsonIgnore
    private byte[] fileData;    //for example: 0x00 0x01 0x02
    private Long size;          // for example: 1024
    private String fileExtension;   //for example: .jpg
    private String fileContentType; //for example: image/jpeg
    private boolean isCompressed;   //for example: true
    private boolean isDeleted;

}
