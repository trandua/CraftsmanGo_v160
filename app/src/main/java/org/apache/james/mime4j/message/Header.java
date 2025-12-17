package org.apache.james.mime4j.message;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.MimeIOException;
import org.apache.james.mime4j.parser.AbstractContentHandler;
import org.apache.james.mime4j.parser.Field;
import org.apache.james.mime4j.parser.MimeStreamParser;
import org.apache.james.mime4j.util.CharsetUtil;

/* loaded from: classes.dex */
public class Header implements Iterable<Field> {
    private List<Field> fields = new LinkedList();
    private Map<String, List<Field>> fieldMap = new HashMap();

    public Header() {
    }

    public Header(InputStream inputStream) throws IOException, MimeIOException {
        final MimeStreamParser mimeStreamParser = new MimeStreamParser();
        mimeStreamParser.setContentHandler(new AbstractContentHandler() { // from class: org.apache.james.mime4j.message.Header.1
            @Override // org.apache.james.mime4j.parser.AbstractContentHandler, org.apache.james.mime4j.parser.ContentHandler
            public void endHeader() {
                mimeStreamParser.stop();
            }

            @Override // org.apache.james.mime4j.parser.AbstractContentHandler, org.apache.james.mime4j.parser.ContentHandler
            public void field(Field field) throws MimeException {
                Header.this.addField(field);
            }
        });
        try {
            mimeStreamParser.parse(inputStream);
        } catch (MimeException e) {
            throw new MimeIOException(e);
        }
    }

    public Header(Header header) {
        for (Field field : header.fields) {
            addField(field);
        }
    }

    public void addField(Field field) {
        List<Field> list = this.fieldMap.get(field.getName().toLowerCase());
        if (list == null) {
            list = new LinkedList<>();
            this.fieldMap.put(field.getName().toLowerCase(), list);
        }
        list.add(field);
        this.fields.add(field);
    }

    public Field getField(String str) {
        List<Field> list = this.fieldMap.get(str.toLowerCase());
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public List<Field> getFields() {
        return Collections.unmodifiableList(this.fields);
    }

    public List<Field> getFields(String str) {
        List<Field> list = this.fieldMap.get(str.toLowerCase());
        return (list == null || list.isEmpty()) ? Collections.emptyList() : Collections.unmodifiableList(list);
    }

    @Override // java.lang.Iterable
    public Iterator<Field> iterator() {
        return Collections.unmodifiableList(this.fields).iterator();
    }

    public int removeFields(String str) {
        List<Field> remove = this.fieldMap.remove(str.toLowerCase());
        if (remove == null || remove.isEmpty()) {
            return 0;
        }
        Iterator<Field> it = this.fields.iterator();
        while (it.hasNext()) {
            if (it.next().getName().equalsIgnoreCase(str)) {
                it.remove();
            }
        }
        return remove.size();
    }

    public void setField(Field field) {
        List<Field> list = this.fieldMap.get(field.getName().toLowerCase());
        if (list == null || list.isEmpty()) {
            addField(field);
            return;
        }
        list.clear();
        list.add(field);
        int i = 0;
        Iterator<Field> it = this.fields.iterator();
        int i2 = -1;
        while (it.hasNext()) {
            if (it.next().getName().equalsIgnoreCase(field.getName())) {
                it.remove();
                if (i2 == -1) {
                    i2 = i;
                }
            }
            i++;
        }
        this.fields.add(i2, field);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        for (Field field : this.fields) {
            sb.append(field.toString());
            sb.append(CharsetUtil.CRLF);
        }
        return sb.toString();
    }
}
