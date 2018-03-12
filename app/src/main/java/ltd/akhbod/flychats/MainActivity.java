package ltd.akhbod.flychats;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.Timer;

public class MainActivity extends AppCompatActivity {
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    private FirebaseAuth mAuth;

    DatabaseReference onlineref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null) {
            onlineref = FirebaseDatabase.getInstance().getReference().child("newuser").child(mAuth.getCurrentUser().getUid()).child("online");
        }

        Toolbar toolbar=findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("fly`chats");




        viewPager=findViewById(R.id.lauda);
        viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        TabLayout tabLayout=findViewById(R.id.TabLayout_id);
        tabLayout.setupWithViewPager(viewPager);



    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser()==null) {
            sendToStart();
        }

        else{
        onlineref.setValue("true");
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        if(mAuth.getCurrentUser()!=null) {
            onlineref.setValue(ServerValue.TIMESTAMP);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        Log.i("abhi","menu");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        Log.i("abhi","menuselecyt");
        if(item.getItemId()==R.id.signout_id)
        {

            FirebaseAuth.getInstance().signOut();

            sendToStart();
        }
        else if(item.getItemId()==R.id.account_setting_id)
        {
            Intent intent=new Intent(getApplicationContext(),SettingsActivity.class);
            Log.i("abhi","k0");
            startActivity(intent);


        }
        else if(item.getItemId()==R.id.allusers_btn){
            Intent intent=new Intent(getApplicationContext(),AllUersActivity.class);
            Log.i("abhi","k1");
            startActivity(intent);
        }

        return true;
    }

    public void sendToStart(){
        Intent intent=new Intent(getApplicationContext(),StartActivity.class);
        startActivity(intent);
        finish();


    }
}
