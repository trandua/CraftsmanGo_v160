package org.apache.james.mime4j.field.structured.parser;

import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Vector;
import org.apache.james.mime4j.util.CharsetUtil;

/* loaded from: classes.dex */
public class StructuredFieldParser implements StructuredFieldParserConstants {
    private static int[] jj_la1_0;
    private Vector<int[]> jj_expentries;
    private int[] jj_expentry;
    private int jj_gen;
    SimpleCharStream jj_input_stream;
    private int jj_kind;
    private final int[] jj_la1;
    public Token jj_nt;
    private int jj_ntk;
    private boolean preserveFolding;
    public Token token;
    public StructuredFieldParserTokenManager token_source;

    static {
        jj_la1_0();
    }

    public StructuredFieldParser(InputStream inputStream) {
        this(inputStream, null);
    }

    public StructuredFieldParser(InputStream inputStream, String str) {
        this.preserveFolding = false;
        this.jj_la1 = new int[2];
        this.jj_expentries = new Vector<>();
        this.jj_kind = -1;
        try {
            this.jj_input_stream = new SimpleCharStream(inputStream, str, 1, 1);
            this.token_source = new StructuredFieldParserTokenManager(this.jj_input_stream);
            this.token = new Token();
            this.jj_ntk = -1;
            this.jj_gen = 0;
            for (int i = 0; i < 2; i++) {
                this.jj_la1[i] = -1;
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public StructuredFieldParser(Reader reader) {
        this.preserveFolding = false;
        this.jj_la1 = new int[2];
        this.jj_expentries = new Vector<>();
        this.jj_kind = -1;
        this.jj_input_stream = new SimpleCharStream(reader, 1, 1);
        this.token_source = new StructuredFieldParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 2; i++) {
            this.jj_la1[i] = -1;
        }
    }

    public StructuredFieldParser(StructuredFieldParserTokenManager structuredFieldParserTokenManager) {
        this.preserveFolding = false;
        this.jj_la1 = new int[2];
        this.jj_expentries = new Vector<>();
        this.jj_kind = -1;
        this.token_source = structuredFieldParserTokenManager;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 2; i++) {
            this.jj_la1[i] = -1;
        }
    }

    private final String doParse() throws ParseException {
        StringBuffer stringBuffer = new StringBuffer(50);
        boolean z = true;
        boolean z2 = false;
        while (true) {
            int i = this.jj_ntk;
            if (i == -1) {
                i = jj_ntk();
            }
            switch (i) {
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                    int i2 = this.jj_ntk;
                    if (i2 == -1) {
                        i2 = jj_ntk();
                    }
                    switch (i2) {
                        case 11:
                            stringBuffer.append(jj_consume_token(11).image);
                            break;
                        case 12:
                            jj_consume_token(12);
                            if (!this.preserveFolding) {
                                break;
                            } else {
                                stringBuffer.append(CharsetUtil.CRLF);
                                break;
                            }
                        case 13:
                            Token jj_consume_token = jj_consume_token(13);
                            if (z) {
                                z = false;
                            } else if (z2) {
                                stringBuffer.append(" ");
                                z2 = false;
                            }
                            stringBuffer.append(jj_consume_token.image);
                            break;
                        case 14:
                            jj_consume_token(14);
                            z2 = true;
                            break;
                        case 15:
                            Token jj_consume_token2 = jj_consume_token(15);
                            if (z) {
                                z = false;
                            } else if (z2) {
                                stringBuffer.append(" ");
                                z2 = false;
                            }
                            stringBuffer.append(jj_consume_token2.image);
                            break;
                        default:
                            this.jj_la1[1] = this.jj_gen;
                            jj_consume_token(-1);
                            throw new ParseException();
                    }
                default:
                    this.jj_la1[0] = this.jj_gen;
                    return stringBuffer.toString();
            }
        }
    }

    private final Token jj_consume_token(int i) throws ParseException {
        Token token = this.token;
        if (token.next != null) {
            this.token = this.token.next;
        } else {
            Token token2 = this.token;
            Token nextToken = this.token_source.getNextToken();
            token2.next = nextToken;
            this.token = nextToken;
        }
        this.jj_ntk = -1;
        if (this.token.kind == i) {
            this.jj_gen++;
            return this.token;
        }
        this.token = token;
        this.jj_kind = i;
        throw generateParseException();
    }

    private static void jj_la1_0() {
        jj_la1_0 = new int[]{63488, 63488};
    }

    private final int jj_ntk() {
        Token token = this.token.next;
        this.jj_nt = token;
        if (token == null) {
            Token token2 = this.token;
            Token nextToken = this.token_source.getNextToken();
            token2.next = nextToken;
            int i = nextToken.kind;
            this.jj_ntk = i;
            return i;
        }
        int i2 = token.kind;
        this.jj_ntk = i2;
        return i2;
    }

    public void ReInit(InputStream inputStream) {
        ReInit(inputStream, null);
    }

    public void ReInit(InputStream inputStream, String str) {
        try {
            this.jj_input_stream.ReInit(inputStream, str, 1, 1);
            this.token_source.ReInit(this.jj_input_stream);
            this.token = new Token();
            this.jj_ntk = -1;
            this.jj_gen = 0;
            for (int i = 0; i < 2; i++) {
                this.jj_la1[i] = -1;
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void ReInit(Reader reader) {
        this.jj_input_stream.ReInit(reader, 1, 1);
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 2; i++) {
            this.jj_la1[i] = -1;
        }
    }

    public void ReInit(StructuredFieldParserTokenManager structuredFieldParserTokenManager) {
        this.token_source = structuredFieldParserTokenManager;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 2; i++) {
            this.jj_la1[i] = -1;
        }
    }

    public final void disable_tracing() {
    }

    public final void enable_tracing() {
    }

    public ParseException generateParseException() {
//        this.jj_expentries.removeAllElements();
//        boolean[] zArr = new boolean[18];
//        for (int i = 0; i < 18; i++) {
//            zArr[i] = false;
//        }
//        int i2 = this.jj_kind;
//        if (i2 >= 0) {
//            zArr[i2] = true;
//            this.jj_kind = -1;
//        }
//        for (int i3 = 0; i3 < 2; i3++) {
//            if (this.jj_la1[i3] == this.jj_gen) {
//                for (int i4 = 0; i4 < 32; i4++) {
//                    if ((jj_la1_0[i3] & (1 << i4)) != 0) {
//                        zArr[i4] = true;
//                    }
//                }
//            }
//        }
//        for (int i5 = 0; i5 < 18; i5++) {
//            if (zArr[i5]) {
//                this.jj_expentry = r5;
//                int[] iArr = {i5};
//                this.jj_expentries.addElement(iArr);
//            }
//        }
//        int[][] iArr2 = new int[this.jj_expentries.size()];
//        for (int i6 = 0; i6 < this.jj_expentries.size(); i6++) {
//            iArr2[i6] = this.jj_expentries.elementAt(i6);
//        }
        return null;// new ParseException(this.token, iArr2, tokenImage);
    }

    public final Token getNextToken() {
        if (this.token.next != null) {
            this.token = this.token.next;
        } else {
            Token token = this.token;
            Token nextToken = this.token_source.getNextToken();
            token.next = nextToken;
            this.token = nextToken;
        }
        this.jj_ntk = -1;
        this.jj_gen++;
        return this.token;
    }

    public final Token getToken(int i) {
        Token token = this.token;
        for (int i2 = 0; i2 < i; i2++) {
            if (token.next != null) {
                token = token.next;
            } else {
                Token nextToken = this.token_source.getNextToken();
                token.next = nextToken;
                token = nextToken;
            }
        }
        return token;
    }

    public boolean isFoldingPreserved() {
        return this.preserveFolding;
    }

    public String parse() throws ParseException {
        try {
            return doParse();
        } catch (TokenMgrError e) {
            throw new ParseException(e);
        }
    }

    public void setFoldingPreserved(boolean z) {
        this.preserveFolding = z;
    }
}
