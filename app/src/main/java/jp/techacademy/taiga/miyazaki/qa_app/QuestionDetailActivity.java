package jp.techacademy.taiga.miyazaki.qa_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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

public class QuestionDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private ListView mListView;
    private Question mQuestion;
    private QuestionDetailListAdapter mAdapter;

    private DatabaseReference mAnswerRef;

    //ボタンの宣言
    Button likeButton;
    Like mLike;
    DatabaseReference mDatabaseReference;
    DatabaseReference likeRef;

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
        fab.setOnClickListener(this);

        likeButton = (Button) findViewById(R.id.likeButton);
        likeButton.setOnClickListener(this);

        DatabaseReference dataBaseReference = FirebaseDatabase.getInstance().getReference();
        mAnswerRef = dataBaseReference.child(Const.ContentsPATH).child(String.valueOf(mQuestion.getGenre())).child(mQuestion.getQuestionUid()).child(Const.AnswersPATH);
        mAnswerRef.addChildEventListener(mEventListener);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            showFab();
        }else if (v.getId() == R.id.likeButton) {
            saveLike();
        }
    }

    public void showFab(){
        // ログイン済みのユーザーを収録する
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            // ログインしていなければログイン画面に遷移させる
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), AnswerSendActivity.class);
            intent.putExtra("question", mQuestion);
            startActivity(intent);

        }
    }

    public void saveLike(){
        //Likesというテーブルを使って、いいねを押した、クエスチョンのデータを丸々保存する
        //保存のイメージは、Likesというテーブルの中にユーザーidカラムを配置、その下にそのユーザーが、お気に入りしたcontentsを保存していくようにと考えました。
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        likeRef = mDatabaseReference.child(Const.LikesPATH).child(uid);

        Map<String, String> data = new HashMap<String, String>();

        int genre = mQuestion.getGenre();
        String title = mQuestion.getTitle();
        String body = mQuestion.getBody();
        String name = mQuestion.getName();
        String questionUid = mQuestion.getQuestionUid();

        data.put("genre", String.valueOf(genre));
        data.put("question", questionUid);
        data.put("title", title);
        data.put("body", body);
        data.put("name", name);

        byte[] bytes = mQuestion.getImageBytes();
        if (bytes == null) {
            data.put("image", String.valueOf(bytes));
        }

        likeRef.push().setValue(data, this);

        likeButton.setText("いいね済み");

    }
}
