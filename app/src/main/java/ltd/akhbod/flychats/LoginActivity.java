package ltd.akhbod.flychats;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    TextInputLayout email,password;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar=findViewById(R.id.toolbar_login_id);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog=new ProgressDialog(this);


        //Firebase

        mAuth=FirebaseAuth.getInstance();

        //Firebase

        email=findViewById(R.id.login_email_id);
        password=findViewById(R.id.login_pass_id);

        Button button=findViewById(R.id.login_burrin_id);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email_=email.getEditText().getText().toString();
                String password_=password.getEditText().getText().toString();

                if(!TextUtils.isEmpty(email_) || !TextUtils.isEmpty(password_)) {

                    progressDialog.setTitle("Logging In");
                    progressDialog.setMessage("just a moment....");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    login(email_,password_);
                }
                else

                    Toast.makeText(getApplicationContext(),"Fill All Fields!!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void login(String email,String password) {

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
                else
                {
                    progressDialog.hide();
                    Toast.makeText(getApplicationContext(),"Cannot Log in!!!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
