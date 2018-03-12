package ltd.akhbod.flychats;

import android.app.Application;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by ibm on 29-01-2018.
 */

public class FlyChats extends Application{

    DatabaseReference muserdatabase;
    FirebaseAuth mauth;
    @Override
    public void onCreate() {
        super.onCreate();

        mauth = FirebaseAuth.getInstance();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);


        FirebaseUser firebaseUser = mauth.getCurrentUser();

        if (firebaseUser != null) {

            String current_user = firebaseUser.getUid().toString();
            muserdatabase = FirebaseDatabase.getInstance().getReference().child("newuser").child(current_user);
            muserdatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot != null) {

                        muserdatabase.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }


    }
}
