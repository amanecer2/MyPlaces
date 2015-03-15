package com.amanecer.myplaces.ui;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by amanecer on 18/02/2015.
 */
public class MyParcelable implements Parcelable {
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
