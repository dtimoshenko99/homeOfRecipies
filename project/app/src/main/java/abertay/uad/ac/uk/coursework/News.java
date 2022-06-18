package abertay.uad.ac.uk.coursework;

import com.google.firebase.Timestamp;

public class News {
    private String pictureUrl;
    private com.google.firebase.Timestamp timestamp;
    private String newsDesc;
    private String username;
    private String newsName;

    public News(){

    }

    public News(String newsName, String mPictureUrl, String newsDescription, com.google.firebase.Timestamp mTimestamp, String mUsername){

        this.newsName = newsName;
        this.pictureUrl = mPictureUrl;
        this.newsDesc = newsDescription;
        this.timestamp = mTimestamp;
        this.username = mUsername;

    }

    public String getNewsName(){
        return newsName;
    }

    public String getPictureUrl(){
        return pictureUrl;
    }

    public String getNewsDesc(){
        return newsDesc;
    }

    public String getUsername(){
        return username;
    }

    public Timestamp getTimestamp(){
        return timestamp;
    }
}
