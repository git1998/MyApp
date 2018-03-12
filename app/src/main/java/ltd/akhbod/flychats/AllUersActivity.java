package ltd.akhbod.flychats;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUersActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;


    DatabaseReference databaseReference;
    FirebaseUser current_user;

    int select_pos;
    String user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_uers);

        toolbar=findViewById(R.id.allusers_applayout);
        recyclerView=findViewById(R.id.allusers_recyclerview);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        current_user= FirebaseAuth.getInstance().getCurrentUser();

        databaseReference= FirebaseDatabase.getInstance().getReference().child("newuser");


        databaseReference.keepSynced(true);




    }

    @Override
    protected void onStart() {
        super.onStart();

        if(current_user!=null) {

            databaseReference.child(current_user.getUid().toString()).child("online").setValue("true");
            Log.i("online","true");
        }
        Log.i("abhi","k3");

        FirebaseRecyclerAdapter<Users,UserViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Users, UserViewHolder>(
                Users.class,
                R.layout.alluser_each_user_layout,
                UserViewHolder.class,
                databaseReference


        ) {
            @Override
            protected void populateViewHolder(UserViewHolder viewHolder, Users model, final int position) {
                viewHolder.setName(model.getName(),model.getStatus(),model.getThumbimage(),getApplicationContext());



                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        user_id=getRef(position).getKey().toString();
                        Log.i("vod",user_id);
                        Intent intent=new Intent(getApplicationContext(),ProfileActivity.class);
                        intent.putExtra("user_id",user_id);
                        startActivity(intent);
                    }
                });
            }


        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public UserViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setName(String name_,String status_,String thumb_image_,Context ctx){
            TextView nameid=(TextView) mView.findViewById(R.id.alluser_displyname);nameid.setText(name_);

            TextView statusid=(TextView) mView.findViewById(R.id.alluser_status); statusid.setText(status_);

            CircleImageView circleImageView=(CircleImageView) mView.findViewById(R.id.circleImageView);

            Picasso.with(ctx).load(thumb_image_).placeholder(R.drawable.default_avatar).into(circleImageView);

        }


    }


    @Override
    protected void onStop() {
        super.onStop();
        if(current_user!=null) {
            databaseReference.child(current_user.getUid().toString()).child("online").setValue(ServerValue.TIMESTAMP);

        }
    }
}


