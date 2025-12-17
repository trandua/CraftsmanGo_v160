package org.apache.james.mime4j.field.structured.parser;

import java.io.IOException;
import java.io.PrintStream;

/* loaded from: classes.dex */
public class StructuredFieldParserTokenManager implements StructuredFieldParserConstants {
    int commentNest;
    protected char curChar;
    int curLexState;
    public PrintStream debugStream;
    int defaultLexState;
    StringBuffer image;
    protected SimpleCharStream input_stream;
    int jjimageLen;
    int jjmatchedKind;
    int jjmatchedPos;
    int jjnewStateCnt;
    int jjround;
    private final int[] jjrounds;
    private final int[] jjstateSet;
    int lengthOfMatch;
    static final long[] jjbitVec0 = {0, 0, -1, -1};
    static final int[] jjnextStates = new int[0];
    public static final String[] jjstrLiteralImages = {"", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null};
    public static final String[] lexStateNames = {"DEFAULT", "INCOMMENT", "NESTED_COMMENT", "INQUOTEDSTRING"};
    public static final int[] jjnewLexState = {-1, 1, 0, 2, -1, -1, -1, -1, -1, 3, -1, -1, -1, 0, -1, -1, -1, -1};
    static final long[] jjtoToken = {63489};
    static final long[] jjtoSkip = {1022};
    static final long[] jjtoMore = {1024};

    public StructuredFieldParserTokenManager(SimpleCharStream simpleCharStream) {
        this.debugStream = System.out;
        this.jjrounds = new int[6];
        this.jjstateSet = new int[12];
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.input_stream = simpleCharStream;
    }

    public StructuredFieldParserTokenManager(SimpleCharStream simpleCharStream, int i) {
        this(simpleCharStream);
        SwitchTo(i);
    }

    private final void ReInitRounds() {
        this.jjround = -2147483647;
        int i = 6;
        while (true) {
            int i2 = i - 1;
            if (i > 0) {
                this.jjrounds[i2] = Integer.MIN_VALUE;
                i = i2;
            } else {
                return;
            }
        }
    }

    private final void jjAddStates(int i, int i2) {
        while (true) {
            int[] iArr = this.jjstateSet;
            int i3 = this.jjnewStateCnt;
            this.jjnewStateCnt = i3 + 1;
            iArr[i3] = jjnextStates[i];
            i++;
            if (i == i2) {
                return;
            }
        }
    }

    private final void jjCheckNAdd(int i) {
        int[] iArr = this.jjrounds;
        int i2 = iArr[i];
        int i3 = this.jjround;
        if (i2 != i3) {
            int[] iArr2 = this.jjstateSet;
            int i4 = this.jjnewStateCnt;
            this.jjnewStateCnt = i4 + 1;
            iArr2[i4] = i;
            iArr[i] = i3;
        }
    }

    private final void jjCheckNAddStates(int i) {
        int[] iArr = jjnextStates;
        jjCheckNAdd(iArr[i]);
        jjCheckNAdd(iArr[i + 1]);
    }

    private final void jjCheckNAddStates(int i, int i2) {
        while (true) {
            jjCheckNAdd(jjnextStates[i]);
            i++;
            if (i == i2) {
                return;
            }
        }
    }

    private final void jjCheckNAddTwoStates(int i, int i2) {
        jjCheckNAdd(i);
        jjCheckNAdd(i2);
    }

    private final int jjMoveNfa_0(int i, int i2) {
        this.jjnewStateCnt = 2;
        this.jjstateSet[0] = i;
        int i3 = i2;
        int i4 = 1;
        int i5 = Integer.MAX_VALUE;
        int i6 = 0;
        while (true) {
            int i7 = this.jjround + 1;
            this.jjround = i7;
            if (i7 == Integer.MAX_VALUE) {
                ReInitRounds();
            }
            char c = this.curChar;
            if (c < '@') {
                long j = 1 << c;
                do {
                    i4--;
                    int i8 = this.jjstateSet[i4];
                    if (i8 != 0) {
                        if (i8 != 1) {
                            if (i8 != 2) {
                                continue;
                            } else if ((j & (-1120986473985L)) != 0) {
                                if (i5 > 15) {
                                    i5 = 15;
                                }
                                jjCheckNAdd(1);
                                continue;
                            } else if ((j & 4294977024L) == 0) {
                                continue;
                            } else {
                                if (i5 > 14) {
                                    i5 = 14;
                                }
                                jjCheckNAdd(0);
                                continue;
                            }
                        } else if ((j & (-1120986473985L)) == 0) {
                            continue;
                        } else {
                            jjCheckNAdd(1);
                            i5 = 15;
                            continue;
                        }
                    } else if ((j & 4294977024L) == 0) {
                        continue;
                    } else {
                        jjCheckNAdd(0);
                        i5 = 14;
                        continue;
                    }
                } while (i4 != i6);
            } else if (c < 128) {
                do {
                    i4--;
                    int i9 = this.jjstateSet[i4];
                    if (i9 == 1 || i9 == 2) {
                        jjCheckNAdd(1);
                        i5 = 15;
                        continue;
                    }
                } while (i4 != i6);
            } else {
                int i10 = (c & 255) >> 6;
                long j2 = 1 << (c & '?');
                do {
                    i4--;
                    int i11 = this.jjstateSet[i4];
                    if ((i11 == 1 || i11 == 2) && (jjbitVec0[i10] & j2) != 0) {
                        if (i5 > 15) {
                            i5 = 15;
                        }
                        jjCheckNAdd(1);
                        continue;
                    }
                } while (i4 != i6);
            }
            if (i5 != Integer.MAX_VALUE) {
                this.jjmatchedKind = i5;
                this.jjmatchedPos = i3;
                i5 = Integer.MAX_VALUE;
            }
            i3++;
            i4 = this.jjnewStateCnt;
            this.jjnewStateCnt = i6;
            i6 = 2 - i6;
            if (i4 == i6) {
                return i3;
            }
            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException unused) {
                return i3;
            }
        }
    }

    private final int jjMoveNfa_1(int i, int i2) {
        this.jjnewStateCnt = 1;
        int i3 = 0;
        this.jjstateSet[0] = i;
        int i4 = 1;
        int i5 = Integer.MAX_VALUE;
        while (true) {
            int i6 = this.jjround + 1;
            this.jjround = i6;
            if (i6 == Integer.MAX_VALUE) {
                ReInitRounds();
            }
            char c = this.curChar;
            if (c < '@') {
                long j = 1 << c;
                do {
                    i4--;
                    if (this.jjstateSet[i4] == 0 && ((-3298534883329L) & j) != 0) {
                        i5 = 4;
                        continue;
                    }
                } while (i4 != i3);
            } else if (c < 128) {
                do {
                    i4--;
                    if (this.jjstateSet[i4] == 0) {
                        i5 = 4;
                        continue;
                    }
                } while (i4 != i3);
            } else {
                int i7 = (c & 255) >> 6;
                long j2 = 1 << (c & '?');
                do {
                    i4--;
                    if (this.jjstateSet[i4] == 0 && (jjbitVec0[i7] & j2) != 0 && i5 > 4) {
                        i5 = 4;
                        continue;
                    }
                } while (i4 != i3);
            }
            if (i5 != Integer.MAX_VALUE) {
                this.jjmatchedKind = i5;
                this.jjmatchedPos = i2;
                i5 = Integer.MAX_VALUE;
            }
            i2++;
            i4 = this.jjnewStateCnt;
            this.jjnewStateCnt = i3;
            i3 = 1 - i3;
            if (i4 == i3) {
                return i2;
            }
            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException unused) {
                return i2;
            }
        }
    }

    private final int jjMoveNfa_2(int i, int i2) {
        this.jjnewStateCnt = 3;
        this.jjstateSet[0] = i;
        int i3 = i2;
        int i4 = 0;
        int i5 = 1;
        int i6 = Integer.MAX_VALUE;
        while (true) {
            int i7 = this.jjround + 1;
            this.jjround = i7;
            if (i7 == Integer.MAX_VALUE) {
                ReInitRounds();
            }
            char c = this.curChar;
            int i8 = 7;
            if (c < '@') {
                long j = 1 << c;
                do {
                    int[] iArr = this.jjstateSet;
                    i5--;
                    int i9 = iArr[i5];
                    if (i9 != 0) {
                        if (i9 == 1) {
                            if (i6 > 7) {
                                i6 = 7;
                            }
                            int i10 = this.jjnewStateCnt;
                            this.jjnewStateCnt = i10 + 1;
                            iArr[i10] = 1;
                            continue;
                        } else {
                            continue;
                        }
                    } else if (((-3298534883329L) & j) != 0 && i6 > 8) {
                        i6 = 8;
                        continue;
                    }
                } while (i5 != i4);
            } else if (c < 128) {
                do {
                    i5--;
                    int i11 = this.jjstateSet[i5];
                    if (i11 == 0) {
                        if (i6 > 8) {
                            i6 = 8;
                        }
                        if (this.curChar == '\\') {
                            jjCheckNAdd(1);
                            continue;
                        } else {
                            continue;
                        }
                    } else if (i11 == 1) {
                        if (i6 > 7) {
                            i6 = 7;
                        }
                        jjCheckNAdd(1);
                        continue;
                    } else if (i11 == 2 && i6 > 8) {
                        i6 = 8;
                        continue;
                    }
                } while (i5 != i4);
            } else {
                int i12 = (c & 255) >> 6;
                long j2 = 1 << (c & '?');
                while (true) {
                    int[] iArr2 = this.jjstateSet;
                    i5--;
                    int i13 = iArr2[i5];
                    if (i13 != 0) {
                        if (i13 == 1 && (jjbitVec0[i12] & j2) != 0) {
                            if (i6 > i8) {
                                i6 = 7;
                            }
                            int i14 = this.jjnewStateCnt;
                            this.jjnewStateCnt = i14 + 1;
                            iArr2[i14] = 1;
                        }
                    } else if ((jjbitVec0[i12] & j2) != 0 && i6 > 8) {
                        i6 = 8;
                    }
                    if (i5 == i4) {
                        break;
                    }
                    i8 = 7;
                }
            }
            if (i6 != Integer.MAX_VALUE) {
                this.jjmatchedKind = i6;
                this.jjmatchedPos = i3;
                i6 = Integer.MAX_VALUE;
            }
            i3++;
            i5 = this.jjnewStateCnt;
            this.jjnewStateCnt = i4;
            i4 = 3 - i4;
            if (i5 == i4) {
                return i3;
            }
            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException unused) {
                return i3;
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:81:0x0120, code lost:
        if (r11 != 2) goto L_0x0122;
     */
    /* JADX WARN: Removed duplicated region for block: B:104:0x017a A[LOOP:3: B:77:0x0115->B:104:0x017a, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:105:0x016a A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:107:0x0169 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:117:0x0153 A[EDGE_INSN: B:117:0x0153->B:95:0x0153 ?: BREAK  , SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:97:0x0158  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final int jjMoveNfa_3(int r21, int r22) {
        /*
            Method dump skipped, instructions count: 380
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.james.mime4j.field.structured.parser.StructuredFieldParserTokenManager.jjMoveNfa_3(int, int):int");
    }

    private final int jjMoveStringLiteralDfa0_0() {
        char c = this.curChar;
        return c != '\"' ? c != '(' ? jjMoveNfa_0(2, 0) : jjStopAtPos(0, 1) : jjStopAtPos(0, 9);
    }

    private final int jjMoveStringLiteralDfa0_1() {
        char c = this.curChar;
        return c != '(' ? c != ')' ? jjMoveNfa_1(0, 0) : jjStopAtPos(0, 2) : jjStopAtPos(0, 3);
    }

    private final int jjMoveStringLiteralDfa0_2() {
        char c = this.curChar;
        return c != '(' ? c != ')' ? jjMoveNfa_2(0, 0) : jjStopAtPos(0, 6) : jjStopAtPos(0, 5);
    }

    private final int jjMoveStringLiteralDfa0_3() {
        return this.curChar != '\"' ? jjMoveNfa_3(0, 0) : jjStopAtPos(0, 13);
    }

    private final int jjStartNfaWithStates_0(int i, int i2, int i3) {
        this.jjmatchedKind = i2;
        this.jjmatchedPos = i;
        try {
            this.curChar = this.input_stream.readChar();
            return jjMoveNfa_0(i3, i + 1);
        } catch (IOException unused) {
            return i + 1;
        }
    }

    private final int jjStartNfaWithStates_1(int i, int i2, int i3) {
        this.jjmatchedKind = i2;
        this.jjmatchedPos = i;
        try {
            this.curChar = this.input_stream.readChar();
            return jjMoveNfa_1(i3, i + 1);
        } catch (IOException unused) {
            return i + 1;
        }
    }

    private final int jjStartNfaWithStates_2(int i, int i2, int i3) {
        this.jjmatchedKind = i2;
        this.jjmatchedPos = i;
        try {
            this.curChar = this.input_stream.readChar();
            return jjMoveNfa_2(i3, i + 1);
        } catch (IOException unused) {
            return i + 1;
        }
    }

    private final int jjStartNfaWithStates_3(int i, int i2, int i3) {
        this.jjmatchedKind = i2;
        this.jjmatchedPos = i;
        try {
            this.curChar = this.input_stream.readChar();
            return jjMoveNfa_3(i3, i + 1);
        } catch (IOException unused) {
            return i + 1;
        }
    }

    private final int jjStartNfa_0(int i, long j) {
        return jjMoveNfa_0(jjStopStringLiteralDfa_0(i, j), i + 1);
    }

    private final int jjStartNfa_1(int i, long j) {
        return jjMoveNfa_1(jjStopStringLiteralDfa_1(i, j), i + 1);
    }

    private final int jjStartNfa_2(int i, long j) {
        return jjMoveNfa_2(jjStopStringLiteralDfa_2(i, j), i + 1);
    }

    private final int jjStartNfa_3(int i, long j) {
        return jjMoveNfa_3(jjStopStringLiteralDfa_3(i, j), i + 1);
    }

    private final int jjStopAtPos(int i, int i2) {
        this.jjmatchedKind = i2;
        this.jjmatchedPos = i;
        return i + 1;
    }

    private final int jjStopStringLiteralDfa_0(int i, long j) {
        return -1;
    }

    private final int jjStopStringLiteralDfa_1(int i, long j) {
        return -1;
    }

    private final int jjStopStringLiteralDfa_2(int i, long j) {
        return -1;
    }

    private final int jjStopStringLiteralDfa_3(int i, long j) {
        return -1;
    }

    void MoreLexicalActions() {
        int i = this.jjimageLen;
        int i2 = this.jjmatchedPos + 1;
        this.lengthOfMatch = i2;
        this.jjimageLen = i + i2;
        if (this.jjmatchedKind == 10) {
            if (this.image == null) {
                this.image = new StringBuffer();
            }
            this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
            this.jjimageLen = 0;
            StringBuffer stringBuffer = this.image;
            stringBuffer.deleteCharAt(stringBuffer.length() - 2);
        }
    }

    public void ReInit(SimpleCharStream simpleCharStream) {
        this.jjnewStateCnt = 0;
        this.jjmatchedPos = 0;
        this.curLexState = this.defaultLexState;
        this.input_stream = simpleCharStream;
        ReInitRounds();
    }

    public void ReInit(SimpleCharStream simpleCharStream, int i) {
        ReInit(simpleCharStream);
        SwitchTo(i);
    }

    void SkipLexicalActions(Token token) {
        StringBuffer stringBuffer = null;
        int i = this.jjmatchedKind;
        if (i == 3) {
            if (this.image == null) {
                this.image = new StringBuffer();
            }
            StringBuffer stringBuffer2 = this.image;
            SimpleCharStream simpleCharStream = this.input_stream;
            int i2 = this.jjimageLen;
            int i3 = this.jjmatchedPos + 1;
            this.lengthOfMatch = i3;
            stringBuffer2.append(simpleCharStream.GetSuffix(i2 + i3));
            this.commentNest = 1;
        } else if (i == 5) {
            if (this.image == null) {
                this.image = new StringBuffer();
            }
            StringBuffer stringBuffer3 = this.image;
            SimpleCharStream simpleCharStream2 = this.input_stream;
            int i4 = this.jjimageLen;
            int i5 = this.jjmatchedPos + 1;
            this.lengthOfMatch = i5;
            stringBuffer3.append(simpleCharStream2.GetSuffix(i4 + i5));
            this.commentNest++;
            System.out.println("+++ COMMENT NEST=" + this.commentNest);
        } else if (i == 6) {
            if (this.image == null) {
                this.image = new StringBuffer();
            }
            StringBuffer stringBuffer4 = this.image;
            SimpleCharStream simpleCharStream3 = this.input_stream;
            int i6 = this.jjimageLen;
            int i7 = this.jjmatchedPos + 1;
            this.lengthOfMatch = i7;
            stringBuffer4.append(simpleCharStream3.GetSuffix(i6 + i7));
            this.commentNest--;
            System.out.println("+++ COMMENT NEST=" + this.commentNest);
            if (this.commentNest == 0) {
                SwitchTo(1);
            }
        } else if (i == 7) {
            if (this.image == null) {
                this.image = new StringBuffer();
            }
            StringBuffer stringBuffer5 = this.image;
            SimpleCharStream simpleCharStream4 = this.input_stream;
            int i8 = this.jjimageLen;
            int i9 = this.jjmatchedPos + 1;
            this.lengthOfMatch = i9;
            stringBuffer5.append(simpleCharStream4.GetSuffix(i8 + i9));
            this.image.deleteCharAt(stringBuffer.length() - 2);
        }
    }

    public void SwitchTo(int i) {
        if (i >= 4 || i < 0) {
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " + i + ". State unchanged.", 2);
        }
        this.curLexState = i;
    }

    void TokenLexicalActions(Token token) {
        if (this.jjmatchedKind == 13) {
            if (this.image == null) {
                this.image = new StringBuffer();
            }
            StringBuffer stringBuffer = this.image;
            SimpleCharStream simpleCharStream = this.input_stream;
            int i = this.jjimageLen;
            int i2 = this.jjmatchedPos + 1;
            this.lengthOfMatch = i2;
            stringBuffer.append(simpleCharStream.GetSuffix(i + i2));
            StringBuffer stringBuffer2 = this.image;
            token.image = stringBuffer2.substring(0, stringBuffer2.length() - 1);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:29:0x0092, code lost:
        SkipLexicalActions(null);
        r4 = org.apache.james.mime4j.field.structured.parser.StructuredFieldParserTokenManager.jjnewLexState;
        r6 = r17.jjmatchedKind;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x009b, code lost:
        if (r4[r6] == (-1)) goto L_0x0006;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x009d, code lost:
        r17.curLexState = r4[r6];
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public org.apache.james.mime4j.field.structured.parser.Token getNextToken() {
        /*
            Method dump skipped, instructions count: 289
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.james.mime4j.field.structured.parser.StructuredFieldParserTokenManager.getNextToken():org.apache.james.mime4j.field.structured.parser.Token");
    }

    protected Token jjFillToken() {
        Token newToken = Token.newToken(this.jjmatchedKind);
        newToken.kind = this.jjmatchedKind;
        String str = jjstrLiteralImages[this.jjmatchedKind];
        if (str == null) {
            str = this.input_stream.GetImage();
        }
        newToken.image = str;
        newToken.beginLine = this.input_stream.getBeginLine();
        newToken.beginColumn = this.input_stream.getBeginColumn();
        newToken.endLine = this.input_stream.getEndLine();
        newToken.endColumn = this.input_stream.getEndColumn();
        return newToken;
    }

    public void setDebugStream(PrintStream printStream) {
        this.debugStream = printStream;
    }
}
