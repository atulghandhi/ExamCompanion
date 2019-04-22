package com.ec16358.examcompanion;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class CardRecyclerViewAdapter extends RecyclerView.Adapter<CardRecyclerViewAdapter.MyViewHolder> {

    //create context and list variables
    private Context context;
    private List<FlashCardObject> list;
    String userID = Home.getCurrentUser().getUserId();

    //constructor to initialise variables created above
    public CardRecyclerViewAdapter(Context context, List<FlashCardObject> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate cardlayout
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.cardview_item, parent, false);

        //return inflated layout to view holder to setText in TextView
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //set prompt text in card textView by getting it from list
        holder.textView.setText(list.get(position).getPrompt());

        //set onClickListener so that card can be clicked to expand
        holder.cardView.setOnClickListener(v -> {
            //onClickListener
            Intent intent = new Intent(context, ViewCardItem.class);
            //pass card data to view card activity
            intent.putExtra("CARD_FRONT", list.get(position).getPrompt());
            intent.putExtra("CARD_BACK", list.get(position).getAnswer());
            context.startActivity(intent);
        });

        holder.cardView.setOnLongClickListener(v -> {
            showDeleteCardDialog(position);
            return true;
        });

    }

    @Override
    public int getItemCount() {
        //return number of cards in list
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //initialise textView in card item
            textView = itemView.findViewById(R.id.idCardItemPrompt);
            cardView = itemView.findViewById(R.id.idCardItem);
        }
    }

    public void showDeleteCardDialog(int pos){
        //method that creates a dialog box that allows user to delete a module clicked on.
        //first get a reference to the moduleObject to be deleted
        FlashCardObject f1 = list.get(pos);

        new AlertDialog.Builder(context)
                .setTitle("Delete card")
                .setMessage("Are you sure you want to delete this card?")

                // delete button
                .setPositiveButton("           Delete", (dialog1, which) -> {
                    // Continue with delete operation; get database reference
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference cardsDatabaseReference = firebaseDatabase.getReference().child(userID).child("cards").child(f1.getDeck());
                    //remove card from database
                    cardsDatabaseReference.child(f1.getId()).removeValue();
                    //notify adapter that item was removed
                    list.remove(pos);
                    notifyItemRemoved(pos);
                    notifyItemRangeChanged(pos, list.size());
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
