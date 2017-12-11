package com.example.seongje.l_recorder;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by SEONGJE on 2017-12-04.
 */

public class TranslateItems implements Parcelable {

    private String item;
    private String time;

    public TranslateItems() {
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public TranslateItems(String item, String time) {

        this.item = item;
        this.time = time;

    }

    public TranslateItems(Parcel in) {

        this.item = in.readString();
        this.time = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(this.item);
        dest.writeString(this.time);

    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public TranslateItems createFromParcel(Parcel in) {
            return new TranslateItems(in);
        }

        @Override
        public TranslateItems[] newArray(int size) {
            return new TranslateItems[size];
        }

    };


}
