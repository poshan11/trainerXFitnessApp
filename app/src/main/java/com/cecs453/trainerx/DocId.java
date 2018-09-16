package com.cecs453.trainerx;

public class DocId {
    private static DocId d = new DocId();
    private String id;

    public String getId() {
        return id;
    }

    public  void setId(String s){
        id=s;
    }

    public  static  DocId getInstance(){
        return d;
    }


}
