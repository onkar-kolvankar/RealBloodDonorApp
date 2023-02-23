package com.example.blooddonorapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonorapp.R;
import com.example.blooddonorapp.model.BloodRequests;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class RecyclerViewTransactionPageAdapter extends RecyclerView.Adapter<RecyclerViewTransactionPageAdapter.ViewHolder>{
    private Context context;
    private List<BloodRequests> bloodRequestsList;
    private String currentUserPhone;

    private FirebaseDatabase myFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference myDatabaseReference = myFirebaseDatabase.getReference();

    public RecyclerViewTransactionPageAdapter(Context context, List<BloodRequests> bloodRequestsList,String currentUserPhone) {
        this.context = context;
        this.bloodRequestsList = bloodRequestsList;
        this.currentUserPhone = currentUserPhone;
    }

    @NonNull
    @Override
    public RecyclerViewTransactionPageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);
        // The view we have created of linear layout will be passed in the ViewHolder() class.
        // This ViewHolder(view) is going to use the class we created with the parameter view
        // It gives the empty card.
        // In this you make ViewHolder object which is then passed to onBindViewHolder which populates the entries.
        return new ViewHolder(view);
    }

    // It tells what to do after we create the viewHolder ojbect
    // Which data goes where in the cardview
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewTransactionPageAdapter.ViewHolder holder, int position) {
        BloodRequests myBloodRequest = bloodRequestsList.get(position);
        holder.txtHospital.setText(myBloodRequest.getHospital());
        holder.txtLatitude.setText(myBloodRequest.getLatitude());
        holder.txtLongitude.setText(myBloodRequest.getLongitude());
        holder.lblBloodGroupDisplayCardView.setText(myBloodRequest.getBloodGroup());
        holder.progressBarCardView.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return bloodRequestsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtHospital,lblBloodGroupDisplayCardView;
        TextView txtLatitude;
        TextView txtLongitude;
        ImageButton btnDeleteRequest;
        CardView cardView;
        ProgressBar progressBarCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtHospital = itemView.findViewById(R.id.txtHospital);
            txtLatitude = itemView.findViewById(R.id.txtLatitude);
            txtLongitude = itemView.findViewById(R.id.txtLongitude);
            lblBloodGroupDisplayCardView = itemView.findViewById(R.id.lblBloodGroupDisplayCardView);
            btnDeleteRequest = itemView.findViewById(R.id.btnDeleteRequest);
            cardView = itemView.findViewById(R.id.cardView);
            progressBarCardView = itemView.findViewById(R.id.progressBarCardView);

            // We will also set onClick listener on the delete button on the row
            btnDeleteRequest.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            progressBarCardView.setVisibility(View.VISIBLE);
            // This method is used for the onClickListeners of the cardviews
            // in ViewHolder() we set onClickListener to the buttons.
            // Now here we will write code for detecting which elemetn is clicked in the cardveiw and what happens then.
            // We will use if condition of v.getId()==btn_name.getId() to check which element is clicked.
            if(v.getId() == btnDeleteRequest.getId()){
                // here we will get the position of the cardview delete button clicked.
                // Then we will use that position to get the BloodRequest object stored in the list.
                // Then we will get the push key of that BloodRequest object.
                // Then we will delete BloodRequest of that push key from the DB

                // Then we need to refresh the recycler view to get new contactList.
                int cardviewPosition = getAdapterPosition();
                BloodRequests bloodRequestToBeDeleted = bloodRequestsList.get(cardviewPosition);
                String pushKeyOfBloodRequestToBeDeleted = bloodRequestToBeDeleted.getPushKey();

                myDatabaseReference.child("BloodRequests").child(currentUserPhone).child(pushKeyOfBloodRequestToBeDeleted).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(context, "Your Blood Request is deleted.", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context, "Sorry met some error. Please try again.", Toast.LENGTH_SHORT).show();
                            Log.d("TAG",task.getException().getLocalizedMessage());
                        }
                    }
                });
            }

    }
    }
}
