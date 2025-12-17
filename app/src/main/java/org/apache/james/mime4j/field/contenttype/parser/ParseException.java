package org.apache.james.mime4j.field.contenttype.parser;

/* loaded from: classes.dex */
public class ParseException extends org.apache.james.mime4j.field.ParseException {
    private static final long serialVersionUID = 1;
    public Token currentToken;
    public int[][] expectedTokenSequences;
    public String[] tokenImage;
    protected String eol = System.getProperty("line.separator", "\n");
    protected boolean specialConstructor = false;

    public ParseException() {
        super("Cannot parse field");
    }

    public ParseException(String str) {
        super(str);
    }

    public ParseException(Throwable th) {
        super(th);
    }

    public ParseException(Token token, int[][] iArr, String[] strArr) {
        super("");
        this.currentToken = token;
        this.expectedTokenSequences = iArr;
        this.tokenImage = strArr;
    }

    protected String add_escapes(String str) {
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
        String str;
        int[][] iArr;
        if (!this.specialConstructor) {
            return super.getMessage();
        }
        StringBuffer stringBuffer = new StringBuffer();
        int i = 0;
        int i2 = 0;
        while (true) {
            int[][] iArr2 = this.expectedTokenSequences;
            if (i >= iArr2.length) {
                break;
            }
            if (i2 < iArr2[i].length) {
                i2 = iArr2[i].length;
            }
            int i3 = 0;
            while (true) {
                iArr = this.expectedTokenSequences;
                if (i3 >= iArr[i].length) {
                    break;
                }
                stringBuffer.append(this.tokenImage[iArr[i][i3]]);
                stringBuffer.append(" ");
                i3++;
            }
            if (iArr[i][iArr[i].length - 1] != 0) {
                stringBuffer.append("...");
            }
            stringBuffer.append(this.eol);
            stringBuffer.append("    ");
            i++;
        }
        Token token = this.currentToken.next;
        String str2 = "Encountered \"";
        int i4 = 0;
        while (true) {
            if (i4 >= i2) {
                break;
            }
            if (i4 != 0) {
                str2 = str2 + " ";
            }
            if (token.kind == 0) {
                str2 = str2 + this.tokenImage[0];
                break;
            }
            str2 = str2 + add_escapes(token.image);
            token = token.next;
            i4++;
        }
        String str3 = (str2 + "\" at line " + this.currentToken.next.beginLine + ", column " + this.currentToken.next.beginColumn) + "." + this.eol;
        if (this.expectedTokenSequences.length == 1) {
            str = str3 + "Was expecting:" + this.eol + "    ";
        } else {
            str = str3 + "Was expecting one of:" + this.eol + "    ";
        }
        return str + stringBuffer.toString();
    }
}
