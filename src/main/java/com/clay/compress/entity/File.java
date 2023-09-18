package com.clay.compress.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


@Entity
@Data
@Table(name = "uploaded_file")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class File extends Auditable<String, Long, LocalDateTime> implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "original_file_name", nullable=false)
    private String originalFilename;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false, columnDefinition = "LONGBLOB")
    private byte[] data;    //for example: 0x00 0x01 0x02

    @Column(name = "size")
    private Long size;

    @Column(name = "extension")
    private String extension;   //for example: .jpg

    @Column(name = "is_compressed")
    private boolean isCompressed;

    @Column(name = "is_deleted")
    private boolean isDeleted;
}
