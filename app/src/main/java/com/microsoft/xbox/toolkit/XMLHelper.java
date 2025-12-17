package com.microsoft.xbox.toolkit;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

/* loaded from: classes3.dex */
public class XMLHelper {
    private static final int XML_WAIT_TIMEOUT_MS = 1000;
    private static XMLHelper instance = new XMLHelper();
    private Serializer serializer;

    private XMLHelper() {
        this.serializer = null;
        this.serializer = new Persister(new AnnotationStrategy());
    }

    public static XMLHelper instance() {
        return instance;
    }

    public <T> T load(InputStream inputStream, Class<T> cls) throws XLEException {
        if (ThreadManager.UIThread != Thread.currentThread()) {
            BackgroundThreadWaitor.getInstance().waitForReady(1000);
        }
        new TimeMonitor();
        ClassLoader classLoader = null;
        try {
            classLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(cls.getClassLoader());
            T t = (T) this.serializer.read((Class<? extends Object>) cls, inputStream, false);
            Thread.currentThread().setContextClassLoader(classLoader);
            return t;
        } catch (Exception e) {
            throw new XLEException(9L, e.toString());
        } catch (Throwable th) {
            Thread.currentThread().setContextClassLoader(classLoader);
            throw th;
        }
    }

    public <T> String save(T t) throws XLEException {
        new TimeMonitor();
        StringWriter stringWriter = new StringWriter();
        try {
            this.serializer.write(t, stringWriter);
            return stringWriter.toString();
        } catch (Exception e) {
            throw new XLEException(9L, e.toString());
        }
    }

    public <T> void save(T t, OutputStream outputStream) throws XLEException {
        new TimeMonitor();
        try {
            this.serializer.write(t, outputStream);
        } catch (Exception e) {
            throw new XLEException(9L, e.toString());
        }
    }
}
