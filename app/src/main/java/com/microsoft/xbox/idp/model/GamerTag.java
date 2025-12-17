package com.microsoft.xbox.idp.model;

/* loaded from: classes3.dex */
public class GamerTag {

    /* loaded from: classes3.dex */
    public static class Request {
        public String gamertag;
        public boolean preview;
        public String reservationId;
    }

    /* loaded from: classes3.dex */
    public static class Response {
        public boolean hasFree;
    }

    /* loaded from: classes3.dex */
    public static class ReservationRequest {
        public String Gamertag;
        public String ReservationId;

        public ReservationRequest() {
        }

        public ReservationRequest(String str, String str2) {
            this.Gamertag = str;
            this.ReservationId = str2;
        }
    }
}
