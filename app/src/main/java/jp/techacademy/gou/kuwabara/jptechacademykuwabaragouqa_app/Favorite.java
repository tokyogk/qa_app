package jp.techacademy.gou.kuwabara.jptechacademykuwabaragouqa_app;

import java.io.Serializable;


public class Favorite implements Serializable {
    private String mUid;
    private String mQuestionUid;

    public Favorite( String uid, String questionUid) {
        mUid = uid;
        mQuestionUid = questionUid;
    }

    public String getUid() {return mUid;}

    public String getQuestionUid() { return mQuestionUid; }
}
//★★★　↑↑　QAアプリ課題で追加部分　↑↑　★★★//