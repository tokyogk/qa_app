package jp.techacademy.gou.kuwabara.jptechacademykuwabaragouqa_app;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class QuestionDetailActivity extends AppCompatActivity {

    private ListView mListView;
    private Question mQuestion;
    private QuestionDetailListAdapter mAdapter;
    //★★★　↓↓　QAアプリ課題で追加部分　↓↓　★★★//
    private Button mfab2Button;
    //★★★　↑↑　QAアプリ課題で追加部分　↑↑　★★★//
    private DatabaseReference mAnswerRef;

    private ChildEventListener mEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            HashMap map = (HashMap) dataSnapshot.getValue();

            String answerUid = dataSnapshot.getKey();

            for(Answer answer : mQuestion.getAnswers()) {
                // 同じAnswerUidのものが存在しているときは何もしない
                if (answerUid.equals(answer.getAnswerUid())) {
                    return;
                }
            }

            String body = (String) map.get("body");
            String name = (String) map.get("name");
            String uid = (String) map.get("uid");

            Answer answer = new Answer(body, name, uid, answerUid);
            mQuestion.getAnswers().add(answer);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);

        //★★★　↓↓　QAアプリ課題で追加部分　↓↓　★★★//
        mfab2Button = (Button) findViewById(R.id.fab2);
        mfab2Button.setOnClickListener(this);
        //★★★　↑↑　QAアプリ課題で追加部分　↑↑　★★★//

        //★★★　↓↓　QAアプリ課題で追加部分　お気に入りボタンの表示　↓↓　★★★//
        final FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
                // ボタンを非表示
                fab2.setVisibility(View.GONE);

                // ログイン済みのユーザーを取得する
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // ボタンを表示
            fab2.setVisibility(View.VISIBLE);
        }
        //★★★　↑↑　QAアプリ課題で追加部分　　お気に入りボタンの表示　　↑↑　★★★//
        //★★★　↓↓　QAアプリ課題で追加部分　お気に入り追加・削除　↓↓　★★★//
        fab2.setOnClickListener(new View.OnClickListener(new View.OnClickListener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference favRef = dataBaseReference.child(Const.FavoritesPATH).child(user.getUid());
            data.put("questionUid", mQuestion.getUid()); // 開いている質問のqquestionオブジェクト
            favRef.push().setValue(data);
        }
        });
        //★★★　↑↑　QAアプリ課題で追加部分　↑↑　★★★//

        // 渡ってきたQuestionのオブジェクトを保持する
        Bundle extras = getIntent().getExtras();
        mQuestion = (Question) extras.get("question");

        setTitle(mQuestion.getTitle());

        // ListViewの準備
        mListView = (ListView) findViewById(R.id.listView);
        mAdapter = new QuestionDetailListAdapter(this, mQuestion);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ログイン済みのユーザーを取得する
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user == null) {
                    // ログインしていなければログイン画面に遷移させる
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    // Questionを渡して回答作成画面を起動する
                    // --- ここから ---
                    Intent intent = new Intent(getApplicationContext(), AnswerSendActivity.class);
                    intent.putExtra("question", mQuestion);
                    startActivity(intent);
                    // --- ここまで ---
                }
            }
        });

        DatabaseReference dataBaseReference = FirebaseDatabase.getInstance().getReference();
        mAnswerRef = dataBaseReference.child(Const.ContentsPATH).child(String.valueOf(mQuestion.getGenre())).child(mQuestion.getQuestionUid()).child(Const.AnswersPATH);
        mAnswerRef.addChildEventListener(mEventListener);

        //★★★　↓↓　QAアプリ課題で追加部分　↓↓　★★★//
        favRef = dataBaseReference
        //★★★　↑↑　QAアプリ課題で追加部分　↑↑　★★★//
    }
}