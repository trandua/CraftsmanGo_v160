package org.apache.james.mime4j.parser;

/* loaded from: classes.dex */
public final class MimeEntityConfig implements Cloneable {
    private boolean maximalBodyDescriptor = false;
    private boolean strictParsing = false;
    private int maxLineLen = 1000;
    private int maxHeaderCount = 1000;
    private long maxContentLen = -1;
    private boolean countLineNumbers = false;

    public MimeEntityConfig clone() {
        try {
            return (MimeEntityConfig) super.clone();
        } catch (CloneNotSupportedException unused) {
            throw new InternalError();
        }
    }

    public long getMaxContentLen() {
        return this.maxContentLen;
    }

    public int getMaxHeaderCount() {
        return this.maxHeaderCount;
    }

    public int getMaxLineLen() {
        return this.maxLineLen;
    }

    public boolean isCountLineNumbers() {
        return this.countLineNumbers;
    }

    public boolean isMaximalBodyDescriptor() {
        return this.maximalBodyDescriptor;
    }

    public boolean isStrictParsing() {
        return this.strictParsing;
    }

    public void setCountLineNumbers(boolean z) {
        this.countLineNumbers = z;
    }

    public void setMaxContentLen(long j) {
        this.maxContentLen = j;
    }

    public void setMaxHeaderCount(int i) {
        this.maxHeaderCount = i;
    }

    public void setMaxLineLen(int i) {
        this.maxLineLen = i;
    }

    public void setMaximalBodyDescriptor(boolean z) {
        this.maximalBodyDescriptor = z;
    }

    public void setStrictParsing(boolean z) {
        this.strictParsing = z;
    }

    public String toString() {
        return "[max body descriptor: " + this.maximalBodyDescriptor + ", strict parsing: " + this.strictParsing + ", max line length: " + this.maxLineLen + ", max header count: " + this.maxHeaderCount + ", max content length: " + this.maxContentLen + ", count line numbers: " + this.countLineNumbers + "]";
    }
}
