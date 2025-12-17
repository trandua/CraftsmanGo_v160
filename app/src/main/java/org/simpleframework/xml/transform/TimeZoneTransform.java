package org.simpleframework.xml.transform;

import java.util.TimeZone;

/* loaded from: classes.dex */
class TimeZoneTransform implements Transform<TimeZone> {
    @Override // org.simpleframework.xml.transform.Transform
    public TimeZone read(String zone) {
        return TimeZone.getTimeZone(zone);
    }

    public String write(TimeZone zone) {
        return zone.getID();
    }
}
