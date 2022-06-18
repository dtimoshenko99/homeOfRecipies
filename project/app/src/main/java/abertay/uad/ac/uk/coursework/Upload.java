package abertay.uad.ac.uk.coursework;

public class Upload {
    private String mName;
    private String mImageUrl;
    private String mUserName;

    public Upload(){

    }

    public Upload(String recipeName, String pictureUrl, String username){
        mUserName = username;
        mName = recipeName;
        mImageUrl = pictureUrl;
    }

    public String getName(){
        return mName;
    }

    public void setName(String name){
        mName = name;
    }

    public String getImageUrl(){
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl){
        mImageUrl = imageUrl;
    }

    public String getUserName(){
        return mUserName;
    }
}
