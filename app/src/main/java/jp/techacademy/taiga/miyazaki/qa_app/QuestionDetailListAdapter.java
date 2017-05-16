package jp.techacademy.taiga.miyazaki.qa_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.R.attr.key;
import static android.R.attr.keySet;

public class QuestionDetailListAdapter extends BaseAdapter {

    private final static int TYPE_QUESTION = 0;
    private final static int TYPE_ANSWER = 1;

    private LayoutInflater mLayoutInflater = null;
    private Question mQuestion;

    //ボタンの宣言
    private Favorite mFavorite;
    private String favoriteUid;
    private Button favoriteButton;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference favoriteRef;
    private String uid;
    private String name;
    private String body;
    private boolean flag = false;
    byte[] bytes;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    //ここまで

    public QuestionDetailListAdapter(Context context, Question question) {
        mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mQuestion = question;
    }

    @Override
    public int getCount() {
        return 1 + mQuestion.getAnswers().size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_QUESTION;
        } else {
            return TYPE_ANSWER;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }


    @Override
    public Object getItem(int position) {
        return mQuestion;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) == TYPE_QUESTION) {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.list_question_detail, parent, false);
            }

            bytes = mQuestion.getImageBytes();
            body = mQuestion.getBody();
            name = mQuestion.getName();

            TextView bodyTextView = (TextView) convertView.findViewById(R.id.bodyTextView);
            bodyTextView.setText(body);
            TextView nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
            nameTextView.setText(name);
            if (bytes.length != 0) {
                Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length).copy(Bitmap.Config.ARGB_8888, true);
                ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
                imageView.setImageBitmap(image);
            }

            //ここから
            uid = user.getUid();
            mDatabaseReference = FirebaseDatabase.getInstance().getReference();
            favoriteButton = (Button) convertView.findViewById(R.id.favoriteButton);
            if (user == null) {
                favoriteButton.setVisibility(View.GONE);
            }else {
                favoriteRef = mDatabaseReference.child(Const.FavoritePATH).child(uid);
                favoriteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // ログイン済みのユーザーを収録する
                        if (flag == false) {
                            favoriteAdd();
                        }else {
                            favoriteRemove();
                        }
                    }
                });
                favoriteRef.addChildEventListener(mEventListener);
            }
            //ここまで

        } else {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.list_answer, parent, false);
            }

            Answer answer = mQuestion.getAnswers().get(position - 1);
            String body = answer.getBody();
            String name = answer.getName();

            TextView bodyTextView = (TextView) convertView.findViewById(R.id.bodyTextView);
            bodyTextView.setText(body);

            TextView nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
            nameTextView.setText(name);
        }

        return convertView;
    }

    public void favoriteAdd() {
        favoriteRef = mDatabaseReference.child(Const.FavoritePATH).child(uid);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("questionUid", mQuestion.getQuestionUid());
        favoriteRef.push().setValue(data);
    }

    public void favoriteRemove() {
        favoriteRef = mDatabaseReference.child(Const.FavoritePATH).child(uid).child(mFavorite.getFavoriteUid());
        favoriteRef.removeValue();
        favoriteButton.setText("いいね");
        flag = true;
    }

    private ChildEventListener mEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            HashMap map = (HashMap) dataSnapshot.getValue();
            favoriteUid = dataSnapshot.getKey();

            String uid2 = uid;
            String questionUid2 = (String) map.get("questionUid");

            if (questionUid2.equals(mQuestion.getQuestionUid())) {
                flag = true;
                favoriteButton.setText("いいね済み");
            } else {
                flag = false;
            }

            mFavorite = new Favorite(uid2, questionUid2, favoriteUid);

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

}
