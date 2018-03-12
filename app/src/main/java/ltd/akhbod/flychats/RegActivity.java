package ltd.akhbod.flychats;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegActivity extends AppCompatActivity {

    TextInputLayout mUsername;
    TextInputLayout mGmail;
    TextInputLayout mPassword;
    Button mReg;
    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        //Firebase//

        mAuth=FirebaseAuth.getInstance();

        //Firebase//

        mReg=findViewById(R.id.id_reg);
        mUsername=findViewById(R.id.id_username);
        mGmail=findViewById(R.id.id_gmail);
        mPassword=findViewById(R.id.id_password);

        android.support.v7.widget.Toolbar toolbar=(android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_id);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("create account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog=new ProgressDialog(this);


        mReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username=mUsername.getEditText().getText().toString();
                final String gmail=mGmail.getEditText().getText().toString();
                final String password=mPassword.getEditText().getText().toString();




                if(!TextUtils.isEmpty(username) || !TextUtils.isEmpty(gmail) ||!TextUtils.isEmpty(password)){
                    progressDialog.setTitle("creating account");
                    progressDialog.setMessage("just a moment...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                regUser(username,gmail,password);}

                else {

                    Toast.makeText(getApplicationContext(), "Fill All Fields!!", Toast.LENGTH_SHORT).show();
                }
            }
        });




    }


    private void regUser(final String username, String gmail, String password ) {

        mAuth.createUserWithEmailAndPassword(gmail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                    String uid=firebaseUser.getUid();

                    myRef=FirebaseDatabase.getInstance().getReference().child("newuser").child(uid);

                    HashMap<String,String> hashMap=new HashMap<>();
                    hashMap.put("name",username);
                    hashMap.put("status","hey..there im using fly chats");
                    hashMap.put("image","default");
                    hashMap.put("thumbimage","default_thumbimage");
                    hashMap.put("online","true");


                    myRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
                                progressDialog.dismiss();
                                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            }
                        }
                    });
                }
                else
                {
                    progressDialog.hide();
                    Toast.makeText(getApplicationContext(),"Something Wents Wrong!!!!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
