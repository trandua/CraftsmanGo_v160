package org.simpleframework.xml.strategy;

import org.simpleframework.xml.core.PersistenceException;

/* loaded from: classes.dex */
public class CycleException extends PersistenceException {
    public CycleException(String text, Object... list) {
        super(text, list);
    }

    public CycleException(Throwable cause, String text, Object... list) {
        super(cause, text, list);
    }
}
