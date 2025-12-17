package org.apache.james.mime4j.field.language.parser;

import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/* loaded from: classes.dex */
public class ContentLanguageParser implements ContentLanguageParserConstants {
    private static int[] jj_la1_0;
    private Vector<int[]> jj_expentries;
    private int[] jj_expentry;
    private int jj_gen;
    SimpleCharStream jj_input_stream;
    private int jj_kind;
    private final int[] jj_la1;
    public Token jj_nt;
    private int jj_ntk;
    private List<String> languages;
    public Token token;
    public ContentLanguageParserTokenManager token_source;

    static {
        jj_la1_0();
    }

    public ContentLanguageParser(InputStream inputStream) {
        this(inputStream, null);
    }

    public ContentLanguageParser(InputStream inputStream, String str) {
        this.languages = new ArrayList();
        this.jj_la1 = new int[3];
        this.jj_expentries = new Vector<>();
        this.jj_kind = -1;
        try {
            this.jj_input_stream = new SimpleCharStream(inputStream, str, 1, 1);
            this.token_source = new ContentLanguageParserTokenManager(this.jj_input_stream);
            this.token = new Token();
            this.jj_ntk = -1;
            this.jj_gen = 0;
            for (int i = 0; i < 3; i++) {
                this.jj_la1[i] = -1;
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public ContentLanguageParser(Reader reader) {
        this.languages = new ArrayList();
        this.jj_la1 = new int[3];
        this.jj_expentries = new Vector<>();
        this.jj_kind = -1;
        this.jj_input_stream = new SimpleCharStream(reader, 1, 1);
        this.token_source = new ContentLanguageParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 3; i++) {
            this.jj_la1[i] = -1;
        }
    }

    public ContentLanguageParser(ContentLanguageParserTokenManager contentLanguageParserTokenManager) {
        this.languages = new ArrayList();
        this.jj_la1 = new int[3];
        this.jj_expentries = new Vector<>();
        this.jj_kind = -1;
        this.token_source = contentLanguageParserTokenManager;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 3; i++) {
            this.jj_la1[i] = -1;
        }
    }

    private final List<String> doParse() throws ParseException {
        language();
        while (true) {
            int i = this.jj_ntk;
            if (i == -1) {
                i = jj_ntk();
            }
            if (i != 1) {
                this.jj_la1[0] = this.jj_gen;
                return this.languages;
            }
            jj_consume_token(1);
            language();
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
        jj_la1_0 = new int[]{2, 524292, 524292};
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
            for (int i = 0; i < 3; i++) {
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
        for (int i = 0; i < 3; i++) {
            this.jj_la1[i] = -1;
        }
    }

    public void ReInit(ContentLanguageParserTokenManager contentLanguageParserTokenManager) {
        this.token_source = contentLanguageParserTokenManager;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 3; i++) {
            this.jj_la1[i] = -1;
        }
    }

    public final void disable_tracing() {
    }

    public final void enable_tracing() {
    }

    public ParseException generateParseException() {
//        this.jj_expentries.removeAllElements();
//        boolean[] zArr = new boolean[23];
//        for (int i = 0; i < 23; i++) {
//            zArr[i] = false;
//        }
//        int i2 = this.jj_kind;
//        if (i2 >= 0) {
//            zArr[i2] = true;
//            this.jj_kind = -1;
//        }
//        for (int i3 = 0; i3 < 3; i3++) {
//            if (this.jj_la1[i3] == this.jj_gen) {
//                for (int i4 = 0; i4 < 32; i4++) {
//                    if ((jj_la1_0[i3] & (1 << i4)) != 0) {
//                        zArr[i4] = true;
//                    }
//                }
//            }
//        }
//        for (int i5 = 0; i5 < 23; i5++) {
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
        return null;//new ParseException(this.token, iArr2, tokenImage);
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

    public final String language() throws ParseException {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(jj_consume_token(18).image);
        while (true) {
            int i = this.jj_ntk;
            if (i == -1) {
                i = jj_ntk();
            }
            if (i == 2 || i == 19) {
                int i2 = this.jj_ntk;
                if (i2 == -1) {
                    i2 = jj_ntk();
                }
                if (i2 == 2) {
                    jj_consume_token(2);
                    Token jj_consume_token = jj_consume_token(18);
                    stringBuffer.append('-');
                    stringBuffer.append(jj_consume_token.image);
                } else if (i2 == 19) {
                    Token jj_consume_token2 = jj_consume_token(19);
                    stringBuffer.append('-');
                    stringBuffer.append(jj_consume_token2.image);
                } else {
                    this.jj_la1[2] = this.jj_gen;
                    jj_consume_token(-1);
                    throw new ParseException();
                }
            } else {
                this.jj_la1[1] = this.jj_gen;
                String stringBuffer2 = stringBuffer.toString();
                this.languages.add(stringBuffer2);
                return stringBuffer2;
            }
        }
    }

    public List<String> parse() throws ParseException {
        try {
            return doParse();
        } catch (TokenMgrError e) {
            throw new ParseException(e);
        }
    }
}
