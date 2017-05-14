package jp.techacademy.taiga.miyazaki.qa_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class QuestionDetailListAdapter extends BaseAdapter {

    private final static int TYPE_QUESTION = 0;
    private final static int TYPE_ANSWER = 1;

    private LayoutInflater mLayoutInflater = null;
    private Question mQuestion;

    //ボタンの宣言
    Button likeButton;
    DatabaseReference mDatabaseReference;
    DatabaseReference likeRef;
    DatabaseReference likeRef2;
    String uid;
    String name;
    String body;
    byte[] bytes;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private boolean flag = false;
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

            uid = user.getUid();
            mDatabaseReference = FirebaseDatabase.getInstance().getReference();
            likeRef = mDatabaseReference.child(Const.LikesPATH).child(uid).child(mQuestion.getQuestionUid());
            likeRef2 = mDatabaseReference.child(Const.ContentsPATH).child(String.valueOf(mQuestion.getGenre())).child(mQuestion.getQuestionUid()).child(Const.LikesPATH);

            // ボタンの追加
            if (user != null) {
                likeButton = (Button) convertView.findViewById(R.id.likeButton);
                likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (user != null) {
                            if (flag == false) {
//                        Firebaseに保存する
                                Map<String, Object> data = new HashMap<String, Object>();
                                data.put("body", body);
                                data.put("title", mQuestion.getTitle());
                                data.put("name", name);
                                data.put("uid", uid);

                                if (bytes.length != 0) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length).copy(Bitmap.Config.ARGB_8888, true);
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                                    String bitmapString = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
                                    data.put("image", bitmapString);

                                }

//                                if (mQustion.getAnswers() != null) {
//                                    data.put("answer", mQustion.getAnswers());
//                                }
                                Map<String, String> data2 = new HashMap<String, String>();
                                data2.put("uid", uid);

                                likeRef.setValue(data);
                                likeRef2.setValue(data2);
                                likeButton.setText("いいね!済み");
                            } else {
                                likeRef.removeValue();
                                likeButton.setText("いいね");
                            }
                        }
                    }
                });
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
}
