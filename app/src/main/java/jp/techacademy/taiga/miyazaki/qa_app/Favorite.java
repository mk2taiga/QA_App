package jp.techacademy.taiga.miyazaki.qa_app;


import java.io.Serializable;

public class Favorite implements Serializable{
    private String mUid;
    private String mQuestionUid;
    private String mFavoriteUid;

    public Favorite(String uid, String  questionUid, String favoriteUid) {
        mUid = uid;
        mQuestionUid = questionUid;
        mFavoriteUid = favoriteUid;
    }

    public String getUid() {
        return mUid;
    }
    public String getQuestionUid() {
        return mQuestionUid;
    }
    public String getFavoriteUid() {
        return mFavoriteUid;
    }

}
