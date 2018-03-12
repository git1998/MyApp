package ltd.akhbod.flychats;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    TextView displayName, status;
    CircleImageView circleImageView;

    DatabaseReference online_ref;

    FirebaseUser firebaseUser;
    DatabaseReference mRef;

    StorageReference mStorage;

    private static final int GALLARY_PIC = 1;
    int myRequestCode=3;
    String uid=new String();

    //progress dialogue//
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser!=null) {
            uid =firebaseUser.getUid().toString() ;
        }



         //progressbar
        progressDialog=new ProgressDialog(this);

        //progressbar

        displayName = findViewById(R.id.setting_displayname);
        status = findViewById(R.id.setting_status_id);
        circleImageView = findViewById(R.id.setting_displayimage);


        Button status_btn = findViewById(R.id.setting_sttaus_btn);
        Button profile_btn = findViewById(R.id.setting_profie_btn);

        //firebase

        mStorage = FirebaseStorage.getInstance().getReference();
        online_ref=FirebaseDatabase.getInstance().getReference().child("newuser").child(uid).child("online");

        //firebase


        status_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), StatusActivity.class);
                intent.putExtra("status", status.getText().toString());
                intent.putExtra("name", displayName.getText().toString());
                startActivity(intent);
            }
        });


        mRef = FirebaseDatabase.getInstance().getReference().child("newuser").child(uid);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                displayName.setText(dataSnapshot.child("name").getValue().toString());
                status.setText(dataSnapshot.child("status").getValue().toString());

                String image_=dataSnapshot.child("image").getValue().toString();

                if(!image_.equals("default")) {
                    Picasso.with(SettingsActivity.this).load(image_).placeholder(R.drawable.default_avatar).into(circleImageView);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent gallaryIntent = new Intent();
                gallaryIntent.setType("image/*");
                gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallaryIntent, "SELECT IMAGE"), GALLARY_PIC);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLARY_PIC && resultCode==RESULT_OK ) {

            Uri imageUri = data.getData();
            Toast.makeText(getApplicationContext(),imageUri.toString(),Toast.LENGTH_LONG).show();
            CropImage.activity(imageUri).setAspectRatio(1, 1).start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

                CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();
                File thumb_path=new File(resultUri.getPath());

                Bitmap thumb_bit = new Compressor(this).setMaxHeight(200).
                                                                setMaxWidth(200).setQuality(50)
                                                                .compressToBitmap(thumb_path);


                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bit.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                StorageReference filePath=mStorage.child("profile_images").child( uid+".jpg");
                final StorageReference thumb_file_path=mStorage.child("profile_images").child("thumbs").child(uid+".jpg");


                progressDialog.setTitle("Uploading Profile...");
                progressDialog.setMessage("Please wait while we upload and process the image");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                            if(task.isSuccessful()){

                                              final  String imageurl=task.getResult().getDownloadUrl().toString();
                                                UploadTask uploadTask = thumb_file_path.putBytes(thumb_byte);

                                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                                        if(task.isSuccessful()){

                                                            mRef.child("image").setValue(imageurl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()) {
                                                                        progressDialog.dismiss();
                                                                        Toast.makeText(getApplicationContext(), "uploaded Successfully!!", Toast.LENGTH_LONG).show();
                                                                    }
                                                                    else {
                                                                        progressDialog.hide();
                                                                        Toast.makeText(getApplicationContext(),"failed uploading",Toast.LENGTH_LONG).show();
                                                                    }
                                                                }
                                                            });

                                                            mRef.child("thumbimage").setValue(imageurl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(!task.isSuccessful()){
                                                                        Toast.makeText(getApplicationContext(),"failed uploading Thumb_image",Toast.LENGTH_LONG).show();

                                                                    }
                                                                }
                                                            });




                                                        }
                                                        else {
                                                            progressDialog.hide();
                                                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });


                                                            } else {
                                                progressDialog.hide();
                                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                                            }



                                                                                        }
                                                                                                                      });


                                        }

            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                                                Exception error = result.getError();
                                                                                    }
                                                                     }
    }



    @Override
    protected void onStart() {
        super.onStart();

        if(firebaseUser!=null) {

            online_ref.setValue("true");
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseUser!=null) {
            online_ref.setValue(ServerValue.TIMESTAMP);
        }
    }



}
//end of the class////
