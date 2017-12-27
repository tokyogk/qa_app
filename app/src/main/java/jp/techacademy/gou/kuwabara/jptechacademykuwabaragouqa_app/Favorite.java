//★★★　↓↓　QAアプリ課題で追加部分　↓↓　★★★//

package jp.techacademy.gou.kuwabara.jptechacademykuwabaragouqa_app;

import java.io.Serializable;

public class Favorite implements Serializable {
    private String mUid;
    private String mQuestionUid;

    public String getUid() {
        return mUid;
    }

    public String getQuestionUid() { return mQuestionUid; }
    public Favorite( String uid, String questionUid, String favoriteid) {
        mUid = uid;
        mQuestionUid = questionUid;
    }
}
//★★★　↑↑　QAアプリ課題で追加部分　↑↑　★★★//