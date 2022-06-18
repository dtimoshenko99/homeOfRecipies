package abertay.uad.ac.uk.coursework;


public class Post {

    private String recipeName;
    private String pictureUrl;
    private com.google.firebase.Timestamp timestamp;
    private String recipeDesc;
    private String username;
    private String email;
    private Double latitude;
    private Double longtitude;

    public Post(){

    }

    public Post(String mRecipeName, String mPictureUrl, String mDescription, String mEmail, com.google.firebase.Timestamp mTimestamp, String mUsername,
                Double mLatitude,
                Double mLongtitude){

        this.recipeName = mRecipeName;
        this.pictureUrl = mPictureUrl;
        this.email = mEmail;
        this.recipeDesc = mDescription;
        this.timestamp = mTimestamp;
        this.username = mUsername;
        this.latitude = mLatitude;
        this.longtitude = mLongtitude;

    }


    public String getEmail(){return email;}

    public String getRecipeName(){
        return recipeName;
    }

    public String getPictureUrl(){
        return pictureUrl;
    }

    public String getRecipeDesc(){
        return recipeDesc;
    }
    
    public Double getLatitude(){return latitude;}
    
    public Double getLongtitude(){return longtitude;}

    public String getUsername(){
        return username;
    }

    public com.google.firebase.Timestamp getTimestamp(){
        return timestamp;
    }
}
