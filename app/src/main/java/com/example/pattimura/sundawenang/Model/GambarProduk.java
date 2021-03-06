package com.example.pattimura.sundawenang.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by wildan on 19/03/17.
 */

public class GambarProduk implements Parcelable{
    private String nama;
    private String url;

    public GambarProduk(String nama, String url) {
        this.nama = nama;
        this.url = url;
    }

    protected GambarProduk(Parcel in) {
        nama = in.readString();
        url = in.readString();
    }

    public static final Creator<GambarProduk> CREATOR = new Creator<GambarProduk>() {
        @Override
        public GambarProduk createFromParcel(Parcel in) {
            return new GambarProduk(in);
        }

        @Override
        public GambarProduk[] newArray(int size) {
            return new GambarProduk[size];
        }
    };

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nama);
        dest.writeString(url);
    }
}
