package ltd.akhbod.flychats;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class StatusActivity extends AppCompatActivity {

    DatabaseReference databaseReference;

    ProgressDialog progressDialog;

    TextInputLayout textInputLayout;
    Button button;

    FirebaseUser current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        Toolbar toolbar=findViewById(R.id.staus_appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        progressDialog=new ProgressDialog(this);




        Intent intent=getIntent();
        final String[] status = {intent.getStringExtra("status")};
        String name=intent.getStringExtra("name");


        //firebase

        current_user= FirebaseAuth.getInstance().getCurrentUser();
        String uid=current_user.getUid().toString();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("newuser").child(uid);


        //firebase


        textInputLayout=findViewById(R.id.status_status);
        button=findViewById(R.id.status_savechange_btn);


        textInputLayout.getEditText().setText(status[0]);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setTitle("Saving Status");
                progressDialog.setMessage("just a moment...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                String status_=textInputLayout.getEditText().getText().toString();
                status[0] =status_;


                databaseReference.child("status").setValue(status[0]).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();

                            finish();
                        }
                        else
                            progressDialog.hide();
                    }
                });

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        if(current_user!=null) {
            databaseReference.child("online").setValue("true");
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        if(current_user!=null) {
            databaseReference.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }
}
