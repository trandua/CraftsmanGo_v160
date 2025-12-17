package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.service.model.serialization.UTCDateConverterGson;
import com.microsoft.xbox.toolkit.GsonUtil;
import com.microsoft.xbox.toolkit.XLEConstants;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

/* loaded from: classes3.dex */
public interface IFollowerPresenceResult {

    /* loaded from: classes3.dex */
    public static class ActivityRecord {
        public BroadcastRecord broadcast;
        public String richPresence;
    }

    /* loaded from: classes3.dex */
    public static class BroadcastRecord {
        public String f12609id;
        public String provider;
        public String session;
        public int viewers;
    }

    /* loaded from: classes3.dex */
    public static class LastSeenRecord {
        public String deviceType;
        public String titleName;
    }

    /* loaded from: classes3.dex */
    public static class DeviceRecord {
        public ArrayList<TitleRecord> titles;
        public String type;

        public boolean isXbox360() {
            return "Xbox360".equalsIgnoreCase(this.type);
        }

        public boolean isXboxOne() {
            return "XboxOne".equalsIgnoreCase(this.type);
        }
    }

    /* loaded from: classes3.dex */
    public static class FollowersPresenceResult {
        public ArrayList<UserPresence> userPresence;

        public static FollowersPresenceResult deserialize(InputStream inputStream) {
            UserPresence[] userPresenceArr = (UserPresence[]) GsonUtil.deserializeJson(inputStream, UserPresence[].class, Date.class, new UTCDateConverterGson.UTCDateConverterJSONDeserializer());
            if (userPresenceArr == null) {
                return null;
            }
            FollowersPresenceResult followersPresenceResult = new FollowersPresenceResult();
            followersPresenceResult.userPresence = new ArrayList<>(Arrays.asList(userPresenceArr));
            return followersPresenceResult;
        }
    }

    /* loaded from: classes3.dex */
    public static class TitleRecord {
        public ActivityRecord activity;
        public long f12610id;
        public Date lastModified;
        public String name;
        public String placement;

        public boolean isDash() {
            return this.f12610id == XLEConstants.DASH_TITLE_ID;
        }

        public boolean isRunningInFullOrFill() {
            return "Full".equalsIgnoreCase(this.placement) || "Fill".equalsIgnoreCase(this.placement);
        }
    }

    /* loaded from: classes3.dex */
    public static class UserPresence {
        private BroadcastRecord broadcastRecord;
        private boolean broadcastRecordSet;
        public ArrayList<DeviceRecord> devices;
        public LastSeenRecord lastSeen;
        public String state;
        public String xuid;

        public BroadcastRecord getBroadcastRecord(long j) {
            if (!this.broadcastRecordSet) {
                if ("Online".equalsIgnoreCase(this.state)) {
                    Iterator<DeviceRecord> it = this.devices.iterator();
                    loop0: while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        DeviceRecord next = it.next();
                        if (next.isXboxOne()) {
                            Iterator<TitleRecord> it2 = next.titles.iterator();
                            while (it2.hasNext()) {
                                TitleRecord next2 = it2.next();
                                if (next2.f12610id == j && next2.isRunningInFullOrFill() && next2.activity != null && next2.activity.broadcast != null) {
                                    this.broadcastRecord = next2.activity.broadcast;
                                    break loop0;
                                }
                            }
                            continue;
                        }
                    }
                }
                this.broadcastRecordSet = true;
            }
            return this.broadcastRecord;
        }

        public int getBroadcastingViewerCount(long j) {
            BroadcastRecord broadcastRecord = getBroadcastRecord(j);
            if (broadcastRecord == null) {
                return 0;
            }
            return broadcastRecord.viewers;
        }

        public Date getXboxOneNowPlayingDate() {
            Date date = null;
            if ("Online".equalsIgnoreCase(this.state)) {
                Iterator<DeviceRecord> it = this.devices.iterator();
                while (it.hasNext()) {
                    DeviceRecord next = it.next();
                    if (next.isXboxOne()) {
                        Iterator<TitleRecord> it2 = next.titles.iterator();
                        while (true) {
                            if (!it2.hasNext()) {
                                break;
                            }
                            TitleRecord next2 = it2.next();
                            if (next2.isRunningInFullOrFill()) {
                                date = next2.lastModified;
                                break;
                            }
                        }
                    }
                }
            }
            return date;
        }

        public long getXboxOneNowPlayingTitleId() {
            long j = -1;
            if ("Online".equalsIgnoreCase(this.state)) {
                Iterator<DeviceRecord> it = this.devices.iterator();
                while (it.hasNext()) {
                    DeviceRecord next = it.next();
                    if (next.isXboxOne()) {
                        Iterator<TitleRecord> it2 = next.titles.iterator();
                        while (true) {
                            if (!it2.hasNext()) {
                                break;
                            }
                            TitleRecord next2 = it2.next();
                            if (next2.isRunningInFullOrFill()) {
                                j = next2.f12610id;
                                break;
                            }
                        }
                    }
                }
            }
            return j;
        }
    }
}
