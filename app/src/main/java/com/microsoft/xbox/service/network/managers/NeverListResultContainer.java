package com.microsoft.xbox.service.network.managers;

import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes3.dex */
public final class NeverListResultContainer {

    /* loaded from: classes3.dex */
    public static class NeverListResult {
        public ArrayList<NeverUser> users = new ArrayList<>();

        public void add(String str) {
            this.users.add(new NeverUser(str));
        }

        public boolean contains(String str) {
            Iterator<NeverUser> it = this.users.iterator();
            while (it.hasNext()) {
                if (it.next().xuid.equalsIgnoreCase(str)) {
                    return true;
                }
            }
            return false;
        }

        public NeverUser remove(String str) {
            Iterator<NeverUser> it = this.users.iterator();
            while (it.hasNext()) {
                NeverUser next = it.next();
                if (next.xuid.equalsIgnoreCase(str)) {
                    this.users.remove(next);
                    return next;
                }
            }
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static class NeverUser {
        public String xuid;

        public NeverUser(String str) {
            this.xuid = str;
        }
    }
}
