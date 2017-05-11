package jp.techacademy.taiga.miyazaki.qa_app;

import java.io.Serializable;
import java.util.ArrayList;

public class Like implements Serializable{
    private String mLikeUid;
    private String mTitle;
    private String mBody;
    private String mName;
    private String mUid;
    private String mQuestionUid;
    private int mGenre;
    private byte[] mBitmapArray;
    private ArrayList<Answer> mAnswerArrayList;

    public Like(String title, String body, String name, String uid, byte[] bytes) {
        mTitle = title;
        mBody = body;
        mName = name;
        mUid = uid;
        mBitmapArray = bytes.clone();
    }

    public String getTitle() {
        return mTitle;
    }

    public String getBody() {
        return mBody;
    }

    public String getName() {
        return mName;
    }

    public String getUid() {
        return mUid;
    }

    public byte[] getImageBytes() {
        return mBitmapArray;
    }


}
