package ltd.akhbod.flychats;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    RecyclerView recyclerView;
    View mView;

    String cuurent_user;


    DatabaseReference databaseReference,detailsRef;
    FirebaseUser firebaseUser;
    @Override


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         mView=inflater.inflate(R.layout.fragment_friends, container, false);

        recyclerView=mView.findViewById(R.id.friend_list);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mView.getContext()));
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();


        if(firebaseUser!=null) {
            cuurent_user =firebaseUser.getUid().toString();
        }


        databaseReference= FirebaseDatabase.getInstance().getReference().child("friend").child(cuurent_user);
        detailsRef=FirebaseDatabase.getInstance().getReference().child("newuser");



        return mView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<friendsList,FriendListHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<friendsList, FriendListHolder>(
                friendsList.class,
                R.layout.alluser_each_user_layout,
                FriendListHolder.class,
                databaseReference


        ) {
            @Override
            protected void populateViewHolder(final FriendListHolder viewHolder, friendsList model, int position) {
                viewHolder.setStatus(model.getDate());


                final String user_id=getRef(position).getKey();
                Log.i("vow",user_id);



                detailsRef.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String name = dataSnapshot.child("name").getValue().toString();
                        String thumbimage =dataSnapshot.child("thumbimage").getValue().toString();

                        if(dataSnapshot.hasChild("online")){
                            String is_online =  dataSnapshot.child("online").getValue().toString();
                            viewHolder.setOnline(is_online);
                        }

                        viewHolder.setData(name, thumbimage,getContext());

                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        CharSequence[] options={"Open Profile","Send massage"};
                        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                        builder.setTitle("Select Option");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if(i==0){
                                    Intent intent=new Intent(getContext(),ProfileActivity.class);
                                    intent.putExtra("user_id",user_id);
                                    startActivity(intent);
                                }
                                else{

                                     Intent intent=new Intent(getContext(),ChatActivity.class);

                                     intent.putExtra("user_id",user_id);
                                     startActivity(intent);
                                }
                            }
                        });

                        builder.show();
                    }
                });



            }


        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FriendListHolder extends RecyclerView.ViewHolder{

        View view;
        public FriendListHolder(View itemView) {
            super(itemView);
            view=itemView;
        }
        public void setStatus(String status){

            TextView statusid=(TextView) view.findViewById(R.id.alluser_status); statusid.setText(status);

        }

        public void setData(String displayname, String thumb_image, Context context) {

            TextView name_=view.findViewById(R.id.alluser_displyname); name_.setText(displayname);

            CircleImageView circleImageView=(CircleImageView) view.findViewById(R.id.circleImageView);

            Picasso.with(context).load(thumb_image).placeholder(R.drawable.default_avatar).into(circleImageView);
        }


        public void setOnline(String is_online) {

            ImageView imageView=view.findViewById(R.id.eachuser_onlineid);
            imageView.setImageResource(R.drawable.online_status);

            if (is_online.equals("true")) {
                imageView.setVisibility(View.VISIBLE);
            }

            else
                imageView.setVisibility(View.INVISIBLE);

        }

        public void setName(String name) {

            TextView namee=view.findViewById(R.id.alluser_displyname); namee.setText(name);

        }
    }

}
