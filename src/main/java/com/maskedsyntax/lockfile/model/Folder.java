package com.maskedsyntax.lockfile.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Folder {
    private String id;
    private String name;
    private List<Folder> subFolders = new ArrayList<>();
    private List<Entry> entries = new ArrayList<>();

    public Folder() {
        this.id = UUID.randomUUID().toString();
    }

    public Folder(String name) {
        this();
        this.name = name;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return name != null ? name : "Unnamed Folder";
    }

    public List<Folder> getSubFolders() { return subFolders; }
    public void setSubFolders(List<Folder> subFolders) { this.subFolders = subFolders; }

    public List<Entry> getEntries() { return entries; }
    public void setEntries(List<Entry> entries) { this.entries = entries; }
}