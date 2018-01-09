package jp.techacademy.gou.kuwabara.jptechacademykuwabaragouqa_app;

import java.io.Serializable;


public class Favorite implements Serializable {
    private String mTitle;
    private String mUid;
    private int mGenre;

    public String getTitle() {
        return mTitle;
    }

    public String getUid() {return mUid;}

    public int getGenre() { return mGenre; }


    public Favorite( String title,  String uid, int genre) {
        mTitle = title;
        mUid = uid;
        mGenre = genre;
    }


}
//★★★　↑↑　QAアプリ課題で追加部分　↑↑　★★★//