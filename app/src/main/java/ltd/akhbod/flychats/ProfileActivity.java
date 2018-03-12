package ltd.akhbod.flychats;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView mImageView;
    TextView mName,mStatus;
    Button mBtn,mDeclineBtn;

    String frnd_status="not_frnd";
            FirebaseUser mcurrent_user;
    DatabaseReference mref,nref,frndRef,mNotificationDatabase;

    String userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        mcurrent_user= FirebaseAuth.getInstance().getCurrentUser();


        mImageView=findViewById(R.id.profile_image);
        mName=findViewById(R.id.profile_name);
        mStatus=findViewById(R.id.profile_status);
        mBtn=findViewById(R.id.profile_sendreqbtn);
        mDeclineBtn=(Button) findViewById(R.id.profile_declinerqebtn);
        mDeclineBtn.setVisibility(View.INVISIBLE);
        mDeclineBtn.setClickable(false);




        nref= FirebaseDatabase.getInstance().getReference().child("frnd_request");
        frndRef=FirebaseDatabase.getInstance().getReference().child("friend");
        mNotificationDatabase=FirebaseDatabase.getInstance().getReference().child("notification");
        



    }

    @Override
    protected void onStart() {
        super.onStart();

        userid=getIntent().getStringExtra("user_id");
        mref= FirebaseDatabase.getInstance().getReference().child("newuser").child(userid);
        if(mcurrent_user!=null) {
            mref.child("online").setValue("true");
        }
        mref.keepSynced(true);

        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {


                Picasso.with(getApplicationContext()).load(dataSnapshot.child("image").getKey().toString()).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.default_avatar).into(mImageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                        Picasso.with(getApplicationContext()).load(dataSnapshot.child("image").getKey().toString())
                                .placeholder(R.drawable.default_avatar).into(mImageView);

                    }
                });


                mName.setText(dataSnapshot.child("name").getValue().toString());
                mStatus.setText(dataSnapshot.child("status").getValue().toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        nref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(mcurrent_user.getUid()).hasChild(userid)){

                    String req_status =dataSnapshot.child(mcurrent_user.getUid()).child(userid).child("request_type").getValue().toString();
                    if(req_status.equals("recieved")){
                        mBtn.setText("accept friend request");
                        frnd_status="recieved";

                        mDeclineBtn.setVisibility(View.VISIBLE);
                        mDeclineBtn.setClickable(true);



                    }
                    else if(req_status.equals("sent")){
                        mBtn.setText("cancel friend request");
                        frnd_status="frnd";

                        mDeclineBtn.setVisibility(View.INVISIBLE);
                        mDeclineBtn.setClickable(false);

                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        frndRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(mcurrent_user.getUid()).hasChild(userid)){
                    mBtn.setText("unfriend person");
                    frnd_status="frnd";

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mDeclineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                nref.child(mcurrent_user.getUid()).child(userid).child("request_type").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mBtn.setText("send friend request");
                        frnd_status="not_frnd";
                        mDeclineBtn.setVisibility(View.INVISIBLE);
                        mDeclineBtn.setClickable(false);

                        nref.child(userid).child(mcurrent_user.getUid()).child("request_type").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(),"friend request",Toast.LENGTH_SHORT).show();

                            }
                        });


                    }
                });

            }
        });


        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //-------------------------NOT FRIEND-----------------------

                if(frnd_status=="not_frnd"){

                    mBtn.setClickable(false);
                    nref.child(mcurrent_user.getUid()).child(userid).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(),"sent",Toast.LENGTH_SHORT).show();
                                nref.child(userid).child(mcurrent_user.getUid()).child("request_type").setValue("recieved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(!task.isSuccessful()){
                                            Toast.makeText(getApplicationContext(),"not Recieved",Toast.LENGTH_SHORT).show();   }
                                        else{

                                            HashMap<String,String> hashMap=new HashMap<>();
                                            hashMap.put("from",mcurrent_user.getUid());
                                            hashMap.put("type","request");

                                            mNotificationDatabase.child(userid).push().setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    mBtn.setText("Cancel friend request");
                                                    mBtn.setClickable(true);
                                                    frnd_status="frnd";


                                                }
                                            });

                                        }
                                    }
                                }) ;
                            }
                            else
                                Toast.makeText(getApplicationContext(),"not send",Toast.LENGTH_SHORT).show();
                        }
                    }) ;
                }


                //------------------------- IS FRIEND-----------------------
                else if(frnd_status=="frnd"){

                    nref.child(mcurrent_user.getUid()).child(userid).child("request_type").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mBtn.setText("send friend request");
                            frnd_status="not_frnd";

                            nref.child(userid).child(mcurrent_user.getUid()).child("request_type").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(),"friend request",Toast.LENGTH_SHORT).show();

                                }
                            });


                        }
                    });

                    frndRef.child(mcurrent_user.getUid()).child(userid).child("date").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            frndRef.child(userid).child(mcurrent_user.getUid()).child("date").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(),"You unfriend this person!!!",Toast.LENGTH_SHORT).show();

                                }
                            });


                        }
                    });


                }

                else if(frnd_status=="recieved"){

                    final String currentDate= DateFormat.getDateTimeInstance().format(new Date()).toString();
                    frndRef.child(mcurrent_user.getUid()).child(userid).child("date").setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            frndRef.child(userid).child(mcurrent_user.getUid()).child("date").setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {


                                    nref.child(mcurrent_user.getUid()).child(userid).child("request_type").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mBtn.setText("unfriend person");
                                            frnd_status="frnd";
                                            mDeclineBtn.setVisibility(View.INVISIBLE);
                                            mDeclineBtn.setClickable(false);

                                            nref.child(userid).child(mcurrent_user.getUid()).child("request_type").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getApplicationContext(),"Congratss...u r friend now!!",Toast.LENGTH_SHORT).show();
                                                }
                                            });


                                        }
                                    });//------------------data in cild friend request deleted--------------------------//


                                }
                            });
                        }
                    });

                }


            }
        });










    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mcurrent_user!=null) {
            mref.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }
}
