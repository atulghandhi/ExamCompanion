package com.ec16358.examcompanion;

import java.util.Comparator;

/*
* I DID NOT WRITE THIS CLASS
*
* PURPOSE: Comparator class sorts users in leaderboard by their points.
*
* SOURCE: https://stackoverflow.com/questions/2784514/sort-arraylist-of-custom-objects-by-property?rq=1
*
* */

public class UserComparator implements Comparator<UserObject> {
    @Override
    public int compare(UserObject u1, UserObject u2) {
        return Integer.compare(u2.getPoints(), u1.getPoints());
    }
}