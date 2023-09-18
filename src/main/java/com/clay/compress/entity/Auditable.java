package com.clay.compress.entity;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable<U, ID extends Serializable, T> implements Serializable {

    @CreatedBy
    @Column(name = "created_by")
    protected U createdBy;

    @CreatedDate
    @Column(name = "created_at")
    protected T createdAt;

    @LastModifiedBy
    @Column(name = "modified_by")
    protected U modifiedBy;

    @LastModifiedDate
    @Column(name = "modified_at")
    protected T modifiedAt;

    public abstract ID getId();

    public Optional<U> getCreatedBy() {
        return Optional.ofNullable(createdBy);
    }

    public void setCreatedBy(U createdBy) {
        this.createdBy = createdBy;
    }

    public Optional<T> getCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public void setCreatedAt(T createdAt) {
        this.createdAt = createdAt;
    }

    public Optional<U> getModifiedBy() {
        return Optional.ofNullable(modifiedBy);
    }

    public void setModifiedBy(U modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Optional<T> getModifiedAt() {
        return Optional.ofNullable(modifiedAt);
    }

    public void setModifiedAt(T modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = (T) now;
        modifiedAt = (T) now;
    }

    @PreUpdate
    protected void onUpdate() {
        modifiedAt = (T) LocalDateTime.now();
    }
}
