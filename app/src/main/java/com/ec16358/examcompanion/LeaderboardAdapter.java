package com.ec16358.examcompanion;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URI;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/*
*
* This class populates a listview of users in the Leaderboard.java class. It adds their image to
* an image view using the uri provided by firebaseAuth in homescreen. Get an image from the uri provided
* this class uses the com.squareup.picasso dependency.
*
* Othewise, this ListView adapter is similar to all the others. It inflates xml layouts for each
* row of the listview and uses the ArrayList of UserObject's provided by Leaderboard.java to fill those
* rows with data/images.
*
* */

public class LeaderboardAdapter extends ArrayAdapter<UserObject> {

    //constructor: pass in list of ModuleObjects and bind constructor to xml layout 'custom_row_module'
    LeaderboardAdapter(@NonNull Context context, List<UserObject> users) {
        super(context, R.layout.custom_row_leaderboard, users);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //inflate custom row for listView using xml layout
        LayoutInflater inflaterA = LayoutInflater.from(getContext());
        View customView = inflaterA.inflate(R.layout.custom_row_leaderboard, parent, false);

        //get reference to each moduleObject item using position parameter
        UserObject userObject = getItem(position);

        //get reference to textViews in layout
        TextView userNameText = customView.findViewById(R.id.userName);
        TextView userPointsText = customView.findViewById(R.id.userPoints);
        ImageView userImageView = customView.findViewById(R.id.userImage);

        //set information into its respective textView
        userNameText.setText(userObject.getUsername());
        String points = "Points : " + userObject.getPoints();
        userPointsText.setText(points);

        Picasso.with(getContext()).load(Uri.parse(userObject.getPhotoURL())).into(userImageView);

        //return listView row
        return customView;
    }
}
