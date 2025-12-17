package org.apache.james.mime4j.field.structured.parser;

/* loaded from: classes.dex */
public class TokenMgrError extends Error {
    static final int INVALID_LEXICAL_STATE = 2;
    static final int LEXICAL_ERROR = 0;
    static final int LOOP_DETECTED = 3;
    static final int STATIC_LEXER_ERROR = 1;
    int errorCode;

    public TokenMgrError() {
    }

    public TokenMgrError(String str, int i) {
        super(str);
        this.errorCode = i;
    }

    public TokenMgrError(boolean z, int i, int i2, int i3, String str, char c, int i4) {
        this(LexicalError(z, i, i2, i3, str, c), i4);
    }

    protected static String LexicalError(boolean z, int i, int i2, int i3, String str, char c) {
        String str2;
        StringBuilder sb = new StringBuilder();
        sb.append("Lexical error at line ");
        sb.append(i2);
        sb.append(", column ");
        sb.append(i3);
        sb.append(".  Encountered: ");
        if (z) {
            str2 = "<EOF> ";
        } else {
            str2 = "\"" + addEscapes(String.valueOf(c)) + "\" (" + ((int) c) + "), ";
        }
        sb.append(str2);
        sb.append("after : \"");
        sb.append(addEscapes(str));
        sb.append("\"");
        return sb.toString();
    }

    protected static final String addEscapes(String str) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            if (charAt != 0) {
                if (charAt == '\"') {
                    stringBuffer.append("\\\"");
                } else if (charAt == '\'') {
                    stringBuffer.append("\\'");
                } else if (charAt == '\\') {
                    stringBuffer.append("\\\\");
                } else if (charAt == '\f') {
                    stringBuffer.append("\\f");
                } else if (charAt != '\r') {
                    switch (charAt) {
                        case '\b':
                            stringBuffer.append("\\b");
                            continue;
                        case '\t':
                            stringBuffer.append("\\t");
                            continue;
                        case '\n':
                            stringBuffer.append("\\n");
                            continue;
                        default:
                            char charAt2 = str.charAt(i);
                            if (charAt2 < ' ' || charAt2 > '~') {
                                String str2 = "0000" + Integer.toString(charAt2, 16);
                                stringBuffer.append("\\u" + str2.substring(str2.length() - 4, str2.length()));
                                break;
                            } else {
                                stringBuffer.append(charAt2);
                                continue;
                            }
                    }
                } else {
                    stringBuffer.append("\\r");
                }
            }
        }
        return stringBuffer.toString();
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return super.getMessage();
    }
}
