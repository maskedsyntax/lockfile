package com.maskedsyntax.lockfile.model;

import java.util.UUID;

public class Attachment {
    private String id;
    private String fileName;
    private byte[] data; // This will be encrypted within the vault JSON
    private long size;

    public Attachment() {
        this.id = UUID.randomUUID().toString();
    }

    public Attachment(String fileName, byte[] data) {
        this();
        this.fileName = fileName;
        this.data = data;
        this.size = data.length;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public byte[] getData() { return data; }
    public void setData(byte[] data) { this.data = data; }

    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }
}
