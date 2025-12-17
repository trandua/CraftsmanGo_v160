package com.microsoft.xbox.idp.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

/* loaded from: classes3.dex */
public class Suggestions {

    /* loaded from: classes3.dex */
    public static class Request {
        public int Algorithm;
        public int Count;
        public String Locale;
        public String Seed;
    }

    /* loaded from: classes3.dex */
    public static class Response implements Parcelable {
        public static final Parcelable.Creator<Response> CREATOR = new Parcelable.Creator<Response>() { // from class: com.microsoft.xbox.idp.model.Suggestions.Response.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public Response createFromParcel(Parcel parcel) {
                return new Response(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public Response[] newArray(int i) {
                return new Response[i];
            }
        };
        public ArrayList<String> Gamertags;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        protected Response(Parcel parcel) {
            this.Gamertags = parcel.createStringArrayList();
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeStringList(this.Gamertags);
        }
    }
}
