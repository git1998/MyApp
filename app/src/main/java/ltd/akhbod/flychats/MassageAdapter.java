package ltd.akhbod.flychats;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ibm on 01-02-2018.
 */

public class MassageAdapter extends RecyclerView.Adapter<MassageAdapter.MassageViewHolder> {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private List<Massage> mMassageList;

    public MassageAdapter(List<Massage> mMassageList) {

        this.mMassageList = mMassageList;
    }


    @Override
    public MassageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_single_text_view, parent, false);
        return new MassageViewHolder(v);
    }


    public class MassageViewHolder extends RecyclerView.ViewHolder {

        CircleImageView massgaeProfile;
        TextView massagetext;


        public MassageViewHolder(View itemView) {
            super(itemView);

            massgaeProfile = itemView.findViewById(R.id.massage_circleimage_layout);
            massagetext = itemView.findViewById(R.id.massage_text_layout);

        }
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(MassageViewHolder holder, int position) {

        String user_id = mAuth.getCurrentUser().getUid().toString();

        Massage c = mMassageList.get(position);


            holder.massagetext.setText(c.getMassage());


    }




    @Override
    public int getItemCount() {
        return mMassageList.size();
    }
}
