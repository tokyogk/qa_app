package jp.techacademy.gou.kuwabara.jptechacademykuwabaragouqa_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class QuestionDetailActivity extends AppCompatActivity {

    private ListView mListView;
    private Question mQuestion;
    private QuestionDetailListAdapter mAdapter;
    //★★★　↓↓　 ボタンの変数設定　QAアプリ課題で追加部分　↓↓　★★★//
    private FloatingActionButton mfab2Button;
    private DatabaseReference mAnswerRef;

    //★★★　↓↓　 お気に入り判別用のboolean　QAアプリ課題で追加部分　↓↓　★★★//
    private boolean isFavorite = false;
    //★★★　↓↓　 ファイヤーベースでのユーザー変数設定　QAアプリ課題で追加部分　↓↓　★★★//
    private FirebaseUser user;

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
    //★★★　↓↓　お気に入りのリスナー登録　QAアプリ課題で追加部分　↓↓　★★★//

    private ChildEventListener mFavoriteEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            //データの登録解除のボタsetImageResourceンに切り替える処理をかく
            mfab2Button.setImageResource(R.drawable.unlike);
            //フラグをきりかえる。
            isFavorite = true;
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

        //★★★　↓↓　お気に入りボタンのデータベース取得　QAアプリ課題で追加部分　↓↓　★★★//
        DatabaseReference dataBaseReference = FirebaseDatabase.getInstance().getReference();

        //★★★　↓↓　お気に入りボタンの非表示　　QAアプリ課題で追加部分　↓↓　★★★//
        mfab2Button = (FloatingActionButton) findViewById(R.id.fab2);

        //★★★　↓↓　お気に入りボタンの表示　　QAアプリ課題で追加部分　↓↓　★★★//
        mfab2Button.setVisibility(View.GONE);

        //★★★　↓↓　ログイン済ユーザーの取得　QAアプリ課題で追加部分　↓↓　★★★//
        user = FirebaseAuth.getInstance().getCurrentUser();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // ボタンを表示
            mfab2Button.setVisibility(View.VISIBLE);
            DatabaseReference favRef = dataBaseReference.child(Const.FavoritePATH).child(user.getUid()).child(mQuestion.getQuestionUid());
            favRef.addChildEventListener(mFavoriteEventListener);

        }else{
            mfab2Button.setVisibility(View.GONE);
        }
        //★★★　↓↓　お気に入りボタンを押した時の分岐設定　QAアプリ課題で追加部分　↓↓★★★//

        mfab2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference dataBaseReference = FirebaseDatabase.getInstance().getReference();
                DatabaseReference genreRef = dataBaseReference.child(Const.FavoritePATH).child(user.getUid()).child(mQuestion.getQuestionUid());
                //↓↓お気にいり解除の処理
                if(isFavorite == true){//お気に入り解除の処理
                    //★★★　↓↓　お気に入りじゃないボタン表示　QAアプリ課題で追加部分　↓↓　★★★//
                    mfab2Button.setImageResource(R.drawable.unlike);
                    genreRef.removeValue();

                }else{//お気に入り登録の処理
                    //★★★　↓↓　お気に入りボタン表示　QAアプリ課題で追加部分　↓↓　★★★//
                    mfab2Button.setImageResource(R.drawable.like);

                Map<String, String> data = new HashMap<String, String>();
                data.put("genru", String.valueOf(mQuestion.getGenre()));
                genreRef.push().setValue(data, this);

                }
                //★★★　↓↓　お気に入りにしているフラグ　QAアプリ課題で追加部分　↓↓　★★★//
                isFavorite = !isFavorite;


            }
        });


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

        mAnswerRef = dataBaseReference.child(Const.ContentsPATH).child(String.valueOf(mQuestion.getGenre())).child(mQuestion.getQuestionUid()).child(Const.AnswersPATH);
        mAnswerRef.addChildEventListener(mEventListener);

    }
}