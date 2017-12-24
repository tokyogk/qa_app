package jp.techacademy.gou.kuwabara.jptechacademykuwabaragouqa_app;


import java.io.Serializable;
import java.util.ArrayList;

public class Question implements Serializable {
    private String mTitle;
    private String mBody;
    private String mName;
    private String mUid;
    private String mQuestionUid;
    //★★★　↓↓　QAアプリ課題で追加部分　↓↓　★★★//
    private String mFavoriteAnswer;
    //★★★　↑↑　QAアプリ課題で追加部分　↑↑　★★★//
    private int mGenre;
    private byte[] mBitmapArray;
    private ArrayList<Answer> mAnswerArrayList;

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

    public String getQuestionUid() { return mQuestionUid; }
    //★★★　↓↓　QAアプリ課題で追加部分　↓↓　★★★//
    public String getmFavoriteAnswer() { return mFavoriteAnswer; }
    //★★★　↑↑　QAアプリ課題で追加部分　↑↑　★★★//
    public int getGenre() {
        return mGenre;
    }

    public byte[] getImageBytes() {
        return mBitmapArray;
    }

    public ArrayList<Answer> getAnswers() {
        return mAnswerArrayList;
    }

    public Question(String title, String body, String name, String uid, String questionUid, String favoriteAnswe , int genre, byte[] bytes, ArrayList<Answer> answers) {
        mTitle = title;
        mBody = body;
        mName = name;
        mUid = uid;
        mQuestionUid = questionUid;
        //★★★　↓↓　QAアプリ課題で追加部分　↓↓　★★★//
        mFavoriteAnswer = favoriteAnswe;
        //★★★　↑↑　QAアプリ課題で追加部分　↑↑　★★★//
        mGenre = genre;
        mBitmapArray = bytes.clone();
        mAnswerArrayList = answers;
    }

}