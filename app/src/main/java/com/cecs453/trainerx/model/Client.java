package com.cecs453.trainerx.model;

public class Client {
    private String fName;
    private String lName;
    private String imageURL;
    private String docId;

    Client() {
    }

    public Client(String id,String fName, String lName, String imageURL) {
        this.docId = id;
        this.fName = fName;
        this.lName = lName;
        this.imageURL = imageURL;
    }

    public  String getDocId(){
        return  docId;
    }
    public  void setDocId(String id){
        docId = id;
    }
    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}