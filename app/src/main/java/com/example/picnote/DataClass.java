package com.example.picnote;

public class DataClass {

    private String dataImage;
    private String dataTopic;
    private String dataDate;
    private String dataDesc;
    private String key;

    public DataClass(String dataImage, String dataTopic, String dataDate, String dataDesc) {
        this.dataImage = dataImage;
        this.dataTopic = dataTopic;
        this.dataDate = dataDate;
        this.dataDesc = dataDesc;
    }

    public DataClass() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDataImage() {
        return dataImage;
    }

    public String getDataTopic() {
        return dataTopic;
    }

    public String getDataDate() {
        return dataDate;
    }

    public String getDataDesc() {
        return dataDesc;
    }
}
