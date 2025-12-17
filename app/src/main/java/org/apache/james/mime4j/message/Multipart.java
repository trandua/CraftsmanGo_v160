package org.apache.james.mime4j.message;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.james.mime4j.util.ByteSequence;
import org.apache.james.mime4j.util.ContentUtil;

/* loaded from: classes.dex */
public class Multipart implements Body {
    private List<BodyPart> bodyParts;
    private ByteSequence epilogue;
    private transient String epilogueStrCache;
    private Entity parent;
    private ByteSequence preamble;
    private transient String preambleStrCache;
    private String subType;

    public Multipart(String str) {
        this.bodyParts = new LinkedList();
        this.parent = null;
        this.preamble = ByteSequence.EMPTY;
        this.preambleStrCache = "";
        this.epilogue = ByteSequence.EMPTY;
        this.epilogueStrCache = "";
        this.subType = str;
    }

    public Multipart(Multipart multipart) {
        this.bodyParts = new LinkedList();
        this.parent = null;
        this.preamble = multipart.preamble;
        this.preambleStrCache = multipart.preambleStrCache;
        this.epilogue = multipart.epilogue;
        this.epilogueStrCache = multipart.epilogueStrCache;
        for (BodyPart bodyPart : multipart.bodyParts) {
            addBodyPart(new BodyPart(bodyPart));
        }
        this.subType = multipart.subType;
    }

    public void addBodyPart(BodyPart bodyPart) {
        if (bodyPart != null) {
            this.bodyParts.add(bodyPart);
            bodyPart.setParent(this.parent);
            return;
        }
        throw new IllegalArgumentException();
    }

    public void addBodyPart(BodyPart bodyPart, int i) {
        if (bodyPart != null) {
            this.bodyParts.add(i, bodyPart);
            bodyPart.setParent(this.parent);
            return;
        }
        throw new IllegalArgumentException();
    }

    @Override // org.apache.james.mime4j.message.Disposable
    public void dispose() {
        for (BodyPart bodyPart : this.bodyParts) {
            bodyPart.dispose();
        }
    }

    public List<BodyPart> getBodyParts() {
        return Collections.unmodifiableList(this.bodyParts);
    }

    public int getCount() {
        return this.bodyParts.size();
    }

    public String getEpilogue() {
        if (this.epilogueStrCache == null) {
            this.epilogueStrCache = ContentUtil.decode(this.epilogue);
        }
        return this.epilogueStrCache;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ByteSequence getEpilogueRaw() {
        return this.epilogue;
    }

    @Override // org.apache.james.mime4j.message.Body
    public Entity getParent() {
        return this.parent;
    }

    public String getPreamble() {
        if (this.preambleStrCache == null) {
            this.preambleStrCache = ContentUtil.decode(this.preamble);
        }
        return this.preambleStrCache;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ByteSequence getPreambleRaw() {
        return this.preamble;
    }

    public String getSubType() {
        return this.subType;
    }

    public BodyPart removeBodyPart(int i) {
        BodyPart remove = this.bodyParts.remove(i);
        remove.setParent(null);
        return remove;
    }

    public BodyPart replaceBodyPart(BodyPart bodyPart, int i) {
        if (bodyPart != null) {
            BodyPart bodyPart2 = this.bodyParts.set(i, bodyPart);
            if (bodyPart != bodyPart2) {
                bodyPart.setParent(this.parent);
                bodyPart2.setParent(null);
                return bodyPart2;
            }
            throw new IllegalArgumentException("Cannot replace body part with itself");
        }
        throw new IllegalArgumentException();
    }

    public void setBodyParts(List<BodyPart> list) {
        this.bodyParts = list;
        for (BodyPart bodyPart : list) {
            bodyPart.setParent(this.parent);
        }
    }

    public void setEpilogue(String str) {
        this.epilogue = ContentUtil.encode(str);
        this.epilogueStrCache = str;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setEpilogueRaw(ByteSequence byteSequence) {
        this.epilogue = byteSequence;
        this.epilogueStrCache = null;
    }

    @Override // org.apache.james.mime4j.message.Body
    public void setParent(Entity entity) {
        this.parent = entity;
        for (BodyPart bodyPart : this.bodyParts) {
            bodyPart.setParent(entity);
        }
    }

    public void setPreamble(String str) {
        this.preamble = ContentUtil.encode(str);
        this.preambleStrCache = str;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPreambleRaw(ByteSequence byteSequence) {
        this.preamble = byteSequence;
        this.preambleStrCache = null;
    }

    public void setSubType(String str) {
        this.subType = str;
    }
}
