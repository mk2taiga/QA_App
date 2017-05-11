package jp.techacademy.taiga.miyazaki.qa_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class QuestionDetailListAdapter extends BaseAdapter {

    private final static int TYPE_QUESTION = 0;
    private final static int TYPE_ANSWER = 1;

    private LayoutInflater mLayoutInflater = null;
    private Question mQustion;

    //ボタンの宣言
    Button likeButton;
    DatabaseReference mDatabaseReference;
    DatabaseReference likeRef;
    DatabaseReference mLikeRef;
    String uid;
    String name;
    String body;
    byte[] bytes;
    //ここまで

    public QuestionDetailListAdapter(Context context, Question question) {
        mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mQustion = question;
    }

    @Override
    public int getCount() {
        return 1 + mQustion.getAnswers().size();
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
        return mQustion;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) == TYPE_QUESTION) {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.list_question_detail, parent, false);
            }
            bytes = mQustion.getImageBytes();
            body = mQustion.getBody();
            name = mQustion.getName();

            TextView bodyTextView = (TextView) convertView.findViewById(R.id.bodyTextView);
            bodyTextView.setText(body);

            TextView nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
            nameTextView.setText(name);

            // ボタンの追加
            likeButton = (Button) convertView.findViewById(R.id.likeButton);
            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  // 保存のイメージは、Likesテーブルに各ユーザーごとのお気に入りcontentsを保存するイメージです。
                    uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    mDatabaseReference = FirebaseDatabase.getInstance().getReference();
                    likeRef = mDatabaseReference.child(Const.LikesPATH).child(uid).child(mQustion.getQuestionUid());
                    mLikeRef = mDatabaseReference.child(Const.LikesPATH).child(uid);
                    if (likeRef == null) {
//                        Firebaseに保存する
                        //一つずつデータを取り出すという方法もやってはみましたが、answerまでを保存することはできなかった。
                        // ユーザーid直下にコンテンツidを保存することができず、現在に至っています。
                        mLikeRef.push(mQustion);
                        likeButton.setText("いいね!済み");
                    }else  {
                        likeRef.removeValue();
                        likeButton.setText("いいね!");
                    }
                }
            });
            //ここまで

            if (bytes.length != 0) {
                Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length).copy(Bitmap.Config.ARGB_8888, true);
                ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
                imageView.setImageBitmap(image);
            }
        } else {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.list_answer, parent, false);
            }

            Answer answer = mQustion.getAnswers().get(position - 1);
            String body = answer.getBody();
            String name = answer.getName();

            TextView bodyTextView = (TextView) convertView.findViewById(R.id.bodyTextView);
            bodyTextView.setText(body);

            TextView nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
            nameTextView.setText(name);
        }

        return convertView;
    }
}
