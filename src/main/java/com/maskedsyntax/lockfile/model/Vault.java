package com.maskedsyntax.lockfile.model;

import java.util.ArrayList;
import java.util.List;

public class Vault {
    private String version = "1.0";
    private List<Folder> rootFolders = new ArrayList<>();
    private List<Entry> rootEntries = new ArrayList<>();

    public Vault() {
    }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public List<Folder> getRootFolders() { return rootFolders; }
    public void setRootFolders(List<Folder> rootFolders) { this.rootFolders = rootFolders; }

    public List<Entry> getRootEntries() { return rootEntries; }
    public void setRootEntries(List<Entry> rootEntries) { this.rootEntries = rootEntries; }
}