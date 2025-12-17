package org.simpleframework.xml.stream;

/* loaded from: classes.dex */
final class ProviderFactory {
    ProviderFactory() {
    }

    public static Provider getInstance() {
        try {
            return new StreamProvider();
        } catch (Throwable th) {
            try {
                return new PullProvider();
            } catch (Throwable th2) {
                return new DocumentProvider();
            }
        }
    }
}
