package ltd.akhbod.flychats;

import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    android.support.v7.widget.Toolbar toolbar;
    FirebaseUser mcurrent_user;
    DatabaseReference mref;
    DatabaseReference mRootRef;

    CircleImageView image;
    TextView displayname, online;

    EditText massageText;
    ImageButton addbtn, sendbtn;
    String user_id;

    RecyclerView mMassageList;
    SwipeRefreshLayout mSwpieRefresh;
    List<Massage> massageList = new ArrayList<>();
    LinearLayoutManager mLinearlayout;
    MassageAdapter adapter;

    final int MASSAGE_AT_A_TIME = 6, GALLARY_PIC = 1;
    int massage_pos = 0, i = 1;
    String mLastKey, mPreviousKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mcurrent_user = FirebaseAuth.getInstance().getCurrentUser();


        Log.i("abhi", mcurrent_user.getUid().toString() + "ok");


        toolbar = findViewById(R.id.chat_tooolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(null);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);


        user_id = getIntent().getStringExtra("user_id");


        mref = FirebaseDatabase.getInstance().getReference().child("newuser").child(user_id);
        mRootRef = FirebaseDatabase.getInstance().getReference();


        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        View action_barview = inflater.inflate(R.layout.chat_appbar, null);
        actionBar.setCustomView(action_barview);

        image = findViewById(R.id.chat_image);
        displayname = findViewById(R.id.chat_name);
        online = findViewById(R.id.chat_online);
        massageText = findViewById(R.id.chat_massageimg);
        addbtn = findViewById(R.id.chat_addbtn);
        sendbtn = findViewById(R.id.chat_sendbtn);


        ///chat massages-------------------------------------------------------

        adapter = new MassageAdapter(massageList);

        mMassageList = findViewById(R.id.massage_recycler);
        mSwpieRefresh = findViewById(R.id.massage_swipelayout);
        mLinearlayout = new LinearLayoutManager(this);
        mMassageList.setHasFixedSize(true);
        mMassageList.setLayoutManager(mLinearlayout);


        mMassageList.setAdapter(adapter);

        loadMassages();
        massageList.clear();

        ///chat massages-------------------------------------------------------


        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d("wow", "kk");
                String name_ = dataSnapshot.child("name").getValue().toString();
                String is_online = dataSnapshot.child("online").getValue().toString();
                String thumbimage = dataSnapshot.child("thumbimage").getValue().toString();

                if (is_online.equals("true")) online.setText("online");
                else
                    online.setText(is_online);

                displayname.setText(name_);
                Picasso.with(getApplicationContext()).load(thumbimage).placeholder(R.drawable.default_avatar).into(image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                snedMassage();
            }
        });


        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Unavailable", Toast.LENGTH_SHORT).show();
            }
        });


    }


    public void loadMoreMassages(){


        DatabaseReference databaseReference = mRootRef.child("massages").child(mcurrent_user.getUid().toString()).child(user_id);
        Query databaseQuery = databaseReference.orderByKey().endAt(mLastKey).limitToLast(6);

        databaseQuery.addChildEventListener(new ChildEventListener() {


            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Massage c = dataSnapshot.getValue(Massage.class);

                String massageKey=dataSnapshot.getKey().toString();



                    Log.i("massage",massageKey);

                    if (!mPreviousKey.equals(massageKey)) {
                        massageList.add(massage_pos, c);
                    }

                    else {
                        mPreviousKey = mLastKey;
                    }

                    if (massage_pos == 0) {
                        mLastKey = massageKey;
                    }


                    adapter.notifyDataSetChanged();


                    mMassageList.scrollToPosition(massage_pos);

                    massage_pos++;




                mSwpieRefresh.setRefreshing(false);

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
        });

        mSwpieRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                massage_pos=0;
               loadMoreMassages();

            }
        });




    }

    private void loadMassages() {

        DatabaseReference databaseReference = mRootRef.child("massages").child(mcurrent_user.getUid().toString()).child(user_id);
        Query databaseQuery = databaseReference.limitToLast(MASSAGE_AT_A_TIME);

        databaseQuery.addChildEventListener(new ChildEventListener() {


            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Massage c = dataSnapshot.getValue(Massage.class);
                String massageKey = dataSnapshot.getKey().toString();


                if (massage_pos == 0) {
                    mPreviousKey = massageKey;
                    mLastKey = massageKey;
                }

                massageList.add(massage_pos, c);
                adapter.notifyDataSetChanged();


                mMassageList.scrollToPosition(massage_pos);

                massage_pos++;

                mSwpieRefresh.setRefreshing(false);
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
        });

        mSwpieRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                massage_pos=0;
//                massageList.clear();
                loadMoreMassages();

            }
        });

    }


    private void snedMassage() {

        String massage = massageText.getText().toString();

        if (!TextUtils.isEmpty(massage)) {


            String current_user = "massages" + "/" + mcurrent_user.getUid().toString() + "/" + user_id;
            String chat_user = "massages" + "/" + user_id + "/" + mcurrent_user.getUid().toString();

            DatabaseReference pus_ref = FirebaseDatabase.getInstance().getReference().child(current_user).push();
            String pushString = pus_ref.getKey().toString();

            Map Massages = new HashMap();

            Massages.put("massage", massage);
            Massages.put("seen", false);
            Massages.put("type", "text");
            Massages.put("time", ServerValue.TIMESTAMP);
            Massages.put("from", mcurrent_user.getUid().toString());

            massageText.setText("");

            Map massageUserMap = new HashMap();

            massageUserMap.put(current_user + "/" + pushString, Massages);
            massageUserMap.put(chat_user + "/" + pushString, Massages);

            mRootRef.updateChildren(massageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if (databaseError != null) {
                        Log.i("ChatLog", databaseError.getMessage().toString());
                    }
                }
            });


        }


    }


    @Override
    protected void onStart() {
        super.onStart();
        if(mcurrent_user!=null) {
            mref.child(mcurrent_user.getUid().toString()).child("online").setValue("true");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mcurrent_user!=null) {
            mref.child(mcurrent_user.getUid().toString()).child("online").setValue(ServerValue.TIMESTAMP);
        }
    }
}
