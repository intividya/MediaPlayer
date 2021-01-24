package com.mediaplayer.Model;

import java.util.ArrayList;

public class FolderModel {
    String docId;
    String folderName;
    String email;
    ArrayList<String> arrayList;

    public FolderModel() {
    }

    public FolderModel(String docId, String folderName, String email, ArrayList<String> arrayList) {
        this.docId = docId;
        this.folderName = folderName;
        this.email = email;
        this.arrayList = arrayList;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<String> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<String> arrayList) {
        this.arrayList = arrayList;
    }
}
