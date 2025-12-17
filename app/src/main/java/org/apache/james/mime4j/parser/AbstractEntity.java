package org.apache.james.mime4j.parser;

import java.io.IOException;
import java.util.BitSet;
import kotlin.UByte;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.descriptor.BodyDescriptor;
import org.apache.james.mime4j.descriptor.DefaultBodyDescriptor;
import org.apache.james.mime4j.descriptor.MaximalBodyDescriptor;
import org.apache.james.mime4j.descriptor.MutableBodyDescriptor;
import org.apache.james.mime4j.io.LineReaderInputStream;
import org.apache.james.mime4j.io.MaxHeaderLimitException;
import org.apache.james.mime4j.io.MaxLineLimitException;
import org.apache.james.mime4j.util.ByteArrayBuffer;

/* loaded from: classes.dex */
public abstract class AbstractEntity implements EntityStateMachine {
    private static final int T_IN_BODYPART = -2;
    private static final int T_IN_MESSAGE = -3;
    private static final BitSet fieldChars = new BitSet();
    protected final MutableBodyDescriptor body;
    protected final MimeEntityConfig config;
    protected final int endState;
    private Field field;
    protected final BodyDescriptor parent;
    protected final int startState;
    protected int state;
    protected final Log log = LogFactory.getLog(getClass());
    private final ByteArrayBuffer linebuf = new ByteArrayBuffer(64);
    private int lineCount = 0;
    private boolean endOfHeader = false;
    private int headerCount = 0;

    static {
        for (int i = 33; i <= 57; i++) {
            fieldChars.set(i);
        }
        for (int i2 = 59; i2 <= 126; i2++) {
            fieldChars.set(i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractEntity(BodyDescriptor bodyDescriptor, int i, int i2, MimeEntityConfig mimeEntityConfig) {
        this.parent = bodyDescriptor;
        this.state = i;
        this.startState = i;
        this.endState = i2;
        this.config = mimeEntityConfig;
        this.body = newBodyDescriptor(bodyDescriptor);
    }

    private ByteArrayBuffer fillFieldBuffer() throws IOException, MimeException {
        byte byteAt;
        if (!this.endOfHeader) {
            int maxLineLen = this.config.getMaxLineLen();
            LineReaderInputStream dataStream = getDataStream();
            ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer(64);
            while (true) {
                int length = this.linebuf.length();
                if (maxLineLen <= 0 || byteArrayBuffer.length() + length < maxLineLen) {
                    if (length > 0) {
                        byteArrayBuffer.append(this.linebuf.buffer(), 0, length);
                    }
                    this.linebuf.clear();
                    if (dataStream.readLine(this.linebuf) != -1) {
                        int length2 = this.linebuf.length();
                        if (length2 > 0 && this.linebuf.byteAt(length2 - 1) == 10) {
                            length2--;
                        }
                        if (length2 > 0 && this.linebuf.byteAt(length2 - 1) == 13) {
                            length2--;
                        }
                        if (length2 != 0) {
                            int i = this.lineCount + 1;
                            this.lineCount = i;
                            if (i > 1 && (byteAt = this.linebuf.byteAt(0)) != 32 && byteAt != 9) {
                                break;
                            }
                        } else {
                            this.endOfHeader = true;
                            break;
                        }
                    } else {
                        monitor(Event.HEADERS_PREMATURE_END);
                        this.endOfHeader = true;
                        break;
                    }
                } else {
                    throw new MaxLineLimitException("Maximum line length limit exceeded");
                }
            }
            return byteArrayBuffer;
        }
        throw new IllegalStateException();
    }

    public static final String stateToString(int i) {
        switch (i) {
            case -3:
                return "In message";
            case -2:
                return "Bodypart";
            case -1:
                return "End of stream";
            case 0:
                return "Start message";
            case 1:
                return "End message";
            case 2:
                return "Raw entity";
            case 3:
                return "Start header";
            case 4:
                return "Field";
            case 5:
                return "End header";
            case 6:
                return "Start multipart";
            case 7:
                return "End multipart";
            case 8:
                return "Preamble";
            case 9:
                return "Epilogue";
            case 10:
                return "Start bodypart";
            case 11:
                return "End bodypart";
            case 12:
                return "Body";
            default:
                return "Unknown";
        }
    }

    protected void debug(Event event) {
        if (this.log.isDebugEnabled()) {
            this.log.debug(message(event));
        }
    }

    @Override // org.apache.james.mime4j.parser.EntityStateMachine
    public BodyDescriptor getBodyDescriptor() {
        int state = getState();
        if (state == -1 || state == 6 || state == 12 || state == 8 || state == 9) {
            return this.body;
        }
        throw new IllegalStateException("Invalid state :" + stateToString(this.state));
    }

    protected abstract LineReaderInputStream getDataStream();

    @Override // org.apache.james.mime4j.parser.EntityStateMachine
    public Field getField() {
        if (getState() == 4) {
            return this.field;
        }
        throw new IllegalStateException("Invalid state :" + stateToString(this.state));
    }

    protected abstract int getLineNumber();

    @Override // org.apache.james.mime4j.parser.EntityStateMachine
    public int getState() {
        return this.state;
    }

    protected String message(Event event) {
        String event2 = event == null ? "Event is unexpectedly null." : event.toString();
        int lineNumber = getLineNumber();
        if (lineNumber <= 0) {
            return event2;
        }
        return "Line " + lineNumber + ": " + event2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void monitor(Event event) throws MimeException, IOException {
        if (!this.config.isStrictParsing()) {
            warn(event);
            return;
        }
        throw new MimeParseEventException(event);
    }

    protected MutableBodyDescriptor newBodyDescriptor(BodyDescriptor bodyDescriptor) {
        return this.config.isMaximalBodyDescriptor() ? new MaximalBodyDescriptor(bodyDescriptor) : new DefaultBodyDescriptor(bodyDescriptor);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean parseField() throws MimeException, IOException {
//        boolean z;
//        ByteArrayBuffer fillFieldBuffer;
//        int indexOf;
//        int maxHeaderCount = this.config.getMaxHeaderCount();
//        do {
//            z = false;
//            if (this.endOfHeader) {
//                return false;
//            }
//            if (this.headerCount < maxHeaderCount) {
//                fillFieldBuffer = fillFieldBuffer();
//                this.headerCount++;
//                int length = fillFieldBuffer.length();
//                if (length > 0 && fillFieldBuffer.byteAt(length - 1) == 10) {
//                    length--;
//                }
//                if (length > 0 && fillFieldBuffer.byteAt(length - 1) == 13) {
//                    length--;
//                }
//                fillFieldBuffer.setLength(length);
//                indexOf = fillFieldBuffer.indexOf((byte) 58);
//                if (indexOf > 0) {
//                    int i = 0;
//                    while (true) {
//                        if (i >= indexOf) {
//                            z = true;
//                            continue;
//                            break;
//                        } else if (!fieldChars.get(fillFieldBuffer.byteAt(i) & UByte.MAX_VALUE)) {
//                            monitor(Event.INALID_HEADER);
//                            continue;
//                            break;
//                        } else {
//                            i++;
//                        }
//                    }
//                } else {
//                    monitor(Event.INALID_HEADER);
//                    continue;
//                }
//            } else {
//                throw new MaxHeaderLimitException("Maximum header limit exceeded");
//            }
//        } while (!z);
//        RawField rawField = new RawField(fillFieldBuffer, indexOf);
//        this.field = rawField;
//        this.body.addField(rawField);
        return false;
    }

    public String toString() {
        return getClass().getName() + " [" + stateToString(this.state) + "][" + this.body.getMimeType() + "][" + this.body.getBoundary() + "]";
    }

    protected void warn(Event event) {
        if (this.log.isWarnEnabled()) {
            this.log.warn(message(event));
        }
    }
}
