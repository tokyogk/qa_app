package jp.techacademy.gou.kuwabara.jptechacademykuwabaragouqa_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private int mGenre = 0;

    private DatabaseReference mDatabaseReference;
    private DatabaseReference mGenreRef;
    private DatabaseReference mContentsRef;
    private DatabaseReference mFavoriteRef;
    private ListView mListView;
    private ArrayList<Question> mQuestionArrayList;
    //★★★　↓↓　 お気に入り一覧用リスト　QAアプリ課題で追加部分　↓↓　★★★//
    private ArrayList<String> mFavoriteQuestionUidList = new ArrayList<>();
    private QuestionsListAdapter mAdapter;

    //★★★　↓↓　 HashMapのデータをQuestionオブジェクトに変換　QAアプリ課題で追加部分　↓↓　★★★//
    private Question convertMapToQuestion(String questionUid, HashMap map) {
        String title = (String) map.get("title");
        String body = (String) map.get("body");
        String name = (String) map.get("name");
        String uid = (String) map.get("uid");
        String imageString = (String) map.get("image");
        byte[] bytes;
        if (imageString != null) {
            bytes = Base64.decode(imageString, Base64.DEFAULT);
        } else {
            bytes = new byte[0];
        }

        ArrayList<Answer> answerArrayList = new ArrayList<Answer>();
        HashMap answerMap = (HashMap) map.get("answers");
        if (answerMap != null) {
            for (Object key : answerMap.keySet()) {
                HashMap temp = (HashMap) answerMap.get((String) key);
                String answerBody = (String) temp.get("body");
                String answerName = (String) temp.get("name");
                String answerUid = (String) temp.get("uid");
                Answer answer = new Answer(answerBody, answerName, answerUid, (String) key);
                answerArrayList.add(answer);
            }
        }

        return new Question(title, body, name, uid, questionUid, mGenre, bytes, answerArrayList);
    }

    private ChildEventListener mEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            // お気に入りの場合
            if (mGenre == 5) {
                //★★★　↓↓　 ジャンルごとに取り、それを１つずつ見て中身を抽出　QAアプリ課題で追加部分　↓↓　★★★//
                HashMap<String, HashMap<String, String>> map = (HashMap) dataSnapshot.getValue();
                for (Map.Entry<String, HashMap<String, String>> questionData : map.entrySet()) {
                    Question question = MainActivity.this.convertMapToQuestion(questionData.getKey(), questionData.getValue());
                    if (mFavoriteQuestionUidList.contains(question.getQuestionUid())) {
                        mQuestionArrayList.add(question);
                    }
                }
            } else {
                Question question = MainActivity.this.convertMapToQuestion(dataSnapshot.getKey(), (HashMap) dataSnapshot.getValue());
                mQuestionArrayList.add(question);
            }
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
    //★★★　↓↓　 お気に入り用イベントリスナー uid　QAアプリ課題で追加部分　↓↓　★★★//
    private ChildEventListener mFavoriteEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            String uid = dataSnapshot.getKey();

            mFavoriteQuestionUidList.add(uid);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            HashMap map = (HashMap) dataSnapshot.getValue();

            // 変更があったQuestionを探す
            for (Question question : mQuestionArrayList) {
                if (dataSnapshot.getKey().equals(question.getQuestionUid())) {
                    question.getAnswers().clear();
                    HashMap answerMap = (HashMap) map.get("answers");
                    if (answerMap != null) {
                        for (Object key : answerMap.keySet()) {
                            HashMap temp = (HashMap) answerMap.get(key);
                            String answerBody = (String) temp.get("body");
                            String answerName = (String) temp.get("name");
                            String answerUid = (String) temp.get("uid");
                            Answer answer = new Answer(answerBody, answerName, answerUid, (String) key);
                            question.getAnswers().add(answer);
                        }
                    }

                    mAdapter.notifyDataSetChanged();
                }
            }
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
    // --- ここまで追加する ---

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ジャンルを選択していない場合（mGenre == 0）はエラーを表示するだけ
                if (mGenre == 0) {
                    Snackbar.make(view, "ジャンルを選択して下さい", Snackbar.LENGTH_LONG).show();
                    return;
                }

                // ログイン済みのユーザーを取得する
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user == null) {
                    // ログインしていなければログイン画面に遷移させる
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    // ジャンルを渡して質問作成画面を起動する
                    Intent intent = new Intent(getApplicationContext(), QuestionSendActivity.class);
                    intent.putExtra("genre", mGenre);
                    startActivity(intent);
                }
            }
        });

        // ナビゲーションドロワーの設定
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_hobby) {
                    mToolbar.setTitle("趣味");
                    mGenre = 1;
                } else if (id == R.id.nav_life) {
                    mToolbar.setTitle("生活");
                    mGenre = 2;
                } else if (id == R.id.nav_health) {
                    mToolbar.setTitle("健康");
                    mGenre = 3;
                } else if (id == R.id.nav_compter) {
                    mToolbar.setTitle("コンピューター");
                    mGenre = 4;
                    //★★★　↓↓　 お気に入り一ドロワー用の設定　QAアプリ課題で追加部分　↓↓　★★★//
                } else if (id == R.id.nav_favorite) {
                    mToolbar.setTitle("お気に入り");
                    mGenre = 5;
                }

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

                // 質問のリストをクリアしてから再度Adapterにセットし、AdapterをListViewにセットし直す
                mQuestionArrayList.clear();
                mAdapter.setQuestionArrayList(mQuestionArrayList);
                mListView.setAdapter(mAdapter);

                // 選択したジャンルにリスナーを登録する
                if (mGenreRef != null) {
                    mGenreRef.removeEventListener(mEventListener);
                }

                mGenreRef = mDatabaseReference.child(Const.ContentsPATH).child(String.valueOf(mGenre));
                mGenreRef.addChildEventListener(mEventListener);
                //★★★　↓↓　 お気に入り用の質問のリストをクリアしてから再度Adapterにセット、AdapterをListViewにセットし直す　QAアプリ課題で追加部分　↓↓　★★★//
                mAdapter.setQuestionArrayList(mQuestionArrayList);
                mListView.setAdapter(mAdapter);
                //★★★　↓↓　 お気に入りを選択した時、お気に入り一覧と質問一覧を取得　QAアプリ課題で追加部分　↓↓　★★★//
                if (mGenre == 5) {
                    //★★★　↓↓　 mFavoriteQuestionUidListをクリアして条件分岐　QAアプリ課題で追加部分　↓↓　★★★//
                    mFavoriteQuestionUidList.clear();
                    if (mContentsRef != null) {
                        mContentsRef.removeEventListener(mEventListener);
                    }
                    if (mFavoriteRef != null) {
                        mFavoriteRef.removeEventListener(mFavoriteEventListener);
                    }

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        mFavoriteRef = mDatabaseReference.child(Const.FavoritePATH).child(user.getUid());
                        mFavoriteRef.addChildEventListener(mFavoriteEventListener);
                        mContentsRef = mDatabaseReference.child(Const.ContentsPATH);
                        mContentsRef.addChildEventListener(mEventListener);
                    }
                }

                return true;

            }
        });

        // Firebase
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mContentsRef = FirebaseDatabase.getInstance().getReference().child("contents");

        // ListViewの準備
        mListView = findViewById(R.id.listView);
        mAdapter = new QuestionsListAdapter(this);
        mQuestionArrayList = new ArrayList<>();
        mAdapter.notifyDataSetChanged();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Questionのインスタンスを渡して質問詳細画面を起動する
                Intent intent = new Intent(getApplicationContext(), QuestionDetailActivity.class);
                intent.putExtra("question", mQuestionArrayList.get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // ナビゲーションドロワーでお気に入りリストの表示・非表示
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem favoMenuItem = menu.findItem(R.id.nav_favorite);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            favoMenuItem.setVisible(false);
        } else {
            favoMenuItem.setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}