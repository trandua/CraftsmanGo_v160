package org.apache.james.mime4j.field.mimeversion.parser;

import java.io.IOException;
import java.io.PrintStream;

/* loaded from: classes.dex */
public class MimeVersionParserTokenManager implements MimeVersionParserConstants {
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
    public static final String[] jjstrLiteralImages = {"", "\r", "\n", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, ".", null, null};
    public static final String[] lexStateNames = {"DEFAULT", "INCOMMENT", "NESTED_COMMENT", "INQUOTEDSTRING"};
    public static final int[] jjnewLexState = {-1, -1, -1, -1, 1, 0, -1, 2, -1, -1, -1, -1, -1, 3, -1, -1, 0, -1, -1, -1, -1};
    static final long[] jjtoToken = {458759};
    static final long[] jjtoSkip = {40};
    static final long[] jjtoSpecial = {8};
    static final long[] jjtoMore = {65488};

    public MimeVersionParserTokenManager(SimpleCharStream simpleCharStream) {
        this.debugStream = System.out;
        this.jjrounds = new int[3];
        this.jjstateSet = new int[6];
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.input_stream = simpleCharStream;
    }

    public MimeVersionParserTokenManager(SimpleCharStream simpleCharStream, int i) {
        this(simpleCharStream);
        SwitchTo(i);
    }

    private final void ReInitRounds() {
        this.jjround = -2147483647;
        int i = 3;
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
                            } else if ((j & 287948901175001088L) != 0) {
                                if (i5 > 17) {
                                    i5 = 17;
                                }
                                jjCheckNAdd(1);
                                continue;
                            } else if ((4294977024L & j) == 0) {
                                continue;
                            } else {
                                if (i5 > 3) {
                                    i5 = 3;
                                }
                                jjCheckNAdd(0);
                                continue;
                            }
                        } else if ((j & 287948901175001088L) == 0) {
                            continue;
                        } else {
                            jjCheckNAdd(1);
                            i5 = 17;
                            continue;
                        }
                    } else if ((4294977024L & j) == 0) {
                        continue;
                    } else {
                        jjCheckNAdd(0);
                        i5 = 3;
                        continue;
                    }
                } while (i4 != i6);
            } else if (c < 128) {
                do {
                    i4--;
                    int i9 = this.jjstateSet[i4];
                } while (i4 != i6);
            } else {
                do {
                    i4--;
                    int i10 = this.jjstateSet[i4];
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
            if (c < '@') {
                do {
                    i5--;
                    int i8 = this.jjstateSet[i5];
                    if (i8 != 0) {
                        if (i8 == 1 && i6 > 6) {
                            i6 = 6;
                            continue;
                        }
                    } else if (i6 > 8) {
                        i6 = 8;
                        continue;
                    } else {
                        continue;
                    }
                } while (i5 != i4);
            } else if (c < 128) {
                do {
                    int[] iArr = this.jjstateSet;
                    i5--;
                    int i9 = iArr[i5];
                    if (i9 == 0) {
                        if (i6 > 8) {
                            i6 = 8;
                        }
                        if (this.curChar == '\\') {
                            int i10 = this.jjnewStateCnt;
                            this.jjnewStateCnt = i10 + 1;
                            iArr[i10] = 1;
                            continue;
                        } else {
                            continue;
                        }
                    } else if (i9 != 1) {
                        if (i9 == 2 && i6 > 8) {
                            i6 = 8;
                            continue;
                        }
                    } else if (i6 > 6) {
                        i6 = 6;
                        continue;
                    } else {
                        continue;
                    }
                } while (i5 != i4);
            } else {
                int i11 = (c & 255) >> 6;
                long j = 1 << (c & '?');
                do {
                    i5--;
                    int i12 = this.jjstateSet[i5];
                    if (i12 != 0) {
                        if (i12 == 1 && (jjbitVec0[i11] & j) != 0 && i6 > 6) {
                            i6 = 6;
                            continue;
                        }
                    } else if ((jjbitVec0[i11] & j) != 0 && i6 > 8) {
                        i6 = 8;
                        continue;
                    }
                } while (i5 != i4);
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
            if (c < '@') {
                do {
                    i5--;
                    int i8 = this.jjstateSet[i5];
                    if (i8 != 0) {
                        if (i8 == 1 && i6 > 9) {
                            i6 = 9;
                            continue;
                        }
                    } else if (i6 > 12) {
                        i6 = 12;
                        continue;
                    } else {
                        continue;
                    }
                } while (i5 != i4);
            } else if (c < 128) {
                do {
                    int[] iArr = this.jjstateSet;
                    i5--;
                    int i9 = iArr[i5];
                    if (i9 == 0) {
                        if (i6 > 12) {
                            i6 = 12;
                        }
                        if (this.curChar == '\\') {
                            int i10 = this.jjnewStateCnt;
                            this.jjnewStateCnt = i10 + 1;
                            iArr[i10] = 1;
                            continue;
                        } else {
                            continue;
                        }
                    } else if (i9 != 1) {
                        if (i9 == 2 && i6 > 12) {
                            i6 = 12;
                            continue;
                        }
                    } else if (i6 > 9) {
                        i6 = 9;
                        continue;
                    } else {
                        continue;
                    }
                } while (i5 != i4);
            } else {
                int i11 = (c & 255) >> 6;
                long j = 1 << (c & '?');
                do {
                    i5--;
                    int i12 = this.jjstateSet[i5];
                    if (i12 != 0) {
                        if (i12 == 1 && (jjbitVec0[i11] & j) != 0 && i6 > 9) {
                            i6 = 9;
                            continue;
                        }
                    } else if ((jjbitVec0[i11] & j) != 0 && i6 > 12) {
                        i6 = 12;
                        continue;
                    }
                } while (i5 != i4);
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

    private final int jjMoveNfa_3(int i, int i2) {
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
            if (c < '@') {
                long j = 1 << c;
                do {
                    i5--;
                    int i8 = this.jjstateSet[i5];
                    if (i8 != 0) {
                        if (i8 != 1) {
                            if (i8 != 2) {
                                continue;
                            }
                        } else if (i6 > 14) {
                            i6 = 14;
                            continue;
                        } else {
                            continue;
                        }
                    }
                    if ((j & (-17179869185L)) != 0) {
                        if (i6 > 15) {
                            i6 = 15;
                        }
                        jjCheckNAdd(2);
                        continue;
                    } else {
                        continue;
                    }
                } while (i5 != i4);
            } else if (c < 128) {
                long j2 = 1 << (c & '?');
                do {
                    int[] iArr = this.jjstateSet;
                    i5--;
                    int i9 = iArr[i5];
                    if (i9 != 0) {
                        if (i9 != 1) {
                            if (i9 == 2 && (j2 & (-268435457)) != 0) {
                                if (i6 > 15) {
                                    i6 = 15;
                                }
                                jjCheckNAdd(2);
                                continue;
                            }
                        } else if (i6 > 14) {
                            i6 = 14;
                            continue;
                        } else {
                            continue;
                        }
                    } else if ((j2 & (-268435457)) != 0) {
                        if (i6 > 15) {
                            i6 = 15;
                        }
                        jjCheckNAdd(2);
                        continue;
                    } else if (this.curChar == '\\') {
                        int i10 = this.jjnewStateCnt;
                        this.jjnewStateCnt = i10 + 1;
                        iArr[i10] = 1;
                        continue;
                    } else {
                        continue;
                    }
                } while (i5 != i4);
            } else {
                int i11 = (c & 255) >> 6;
                long j3 = 1 << (c & '?');
                do {
                    i5--;
                    int i12 = this.jjstateSet[i5];
                    if (i12 != 0) {
                        if (i12 != 1) {
                            if (i12 != 2) {
                                continue;
                            }
                        } else if ((jjbitVec0[i11] & j3) != 0 && i6 > 14) {
                            i6 = 14;
                            continue;
                        }
                    }
                    if ((jjbitVec0[i11] & j3) != 0) {
                        if (i6 > 15) {
                            i6 = 15;
                        }
                        jjCheckNAdd(2);
                        continue;
                    } else {
                        continue;
                    }
                } while (i5 != i4);
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

    private final int jjMoveStringLiteralDfa0_0() {
        char c = this.curChar;
        return c != '\n' ? c != '\r' ? c != '\"' ? c != '(' ? c != '.' ? jjMoveNfa_0(2, 0) : jjStopAtPos(0, 18) : jjStopAtPos(0, 4) : jjStopAtPos(0, 13) : jjStartNfaWithStates_0(0, 1, 0) : jjStartNfaWithStates_0(0, 2, 0);
    }

    private final int jjMoveStringLiteralDfa0_1() {
        char c = this.curChar;
        return c != '(' ? c != ')' ? jjMoveNfa_1(0, 0) : jjStopAtPos(0, 5) : jjStopAtPos(0, 7);
    }

    private final int jjMoveStringLiteralDfa0_2() {
        char c = this.curChar;
        return c != '(' ? c != ')' ? jjMoveNfa_2(0, 0) : jjStopAtPos(0, 11) : jjStopAtPos(0, 10);
    }

    private final int jjMoveStringLiteralDfa0_3() {
        return this.curChar != '\"' ? jjMoveNfa_3(0, 0) : jjStopAtPos(0, 16);
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
        StringBuffer stringBuffer = null;
        StringBuffer stringBuffer2 = null;
        StringBuffer stringBuffer3 = null;
        int i = this.jjimageLen;
        int i2 = this.jjmatchedPos + 1;
        this.lengthOfMatch = i2;
        this.jjimageLen = i + i2;
        switch (this.jjmatchedKind) {
            case 6:
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                this.image.deleteCharAt(stringBuffer.length() - 2);
                return;
            case 7:
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                this.commentNest = 1;
                return;
            case 8:
            case 12:
            default:
                return;
            case 9:
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                this.image.deleteCharAt(stringBuffer2.length() - 2);
                return;
            case 10:
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                this.commentNest++;
                return;
            case 11:
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                int i3 = this.commentNest - 1;
                this.commentNest = i3;
                if (i3 == 0) {
                    SwitchTo(1);
                    return;
                }
                return;
            case 13:
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                StringBuffer stringBuffer4 = this.image;
                stringBuffer4.deleteCharAt(stringBuffer4.length() - 1);
                return;
            case 14:
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                this.image.deleteCharAt(stringBuffer3.length() - 2);
                return;
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

    public void SwitchTo(int i) {
        if (i >= 4 || i < 0) {
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " + i + ". State unchanged.", 2);
        }
        this.curLexState = i;
    }

    void TokenLexicalActions(Token token) {
        if (this.jjmatchedKind == 16) {
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

    /* JADX WARN: Code restructure failed: missing block: B:30:0x00a3, code lost:
        if (((1 << (r9 & 63)) & org.apache.james.mime4j.field.mimeversion.parser.MimeVersionParserTokenManager.jjtoSpecial[r9 >> 6]) == 0) goto L_0x00b1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x00a5, code lost:
        r7 = jjFillToken();
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x00a9, code lost:
        if (r4 != null) goto L_0x00ac;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x00ac, code lost:
        r7.specialToken = r4;
        r4.next = r7;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x00b0, code lost:
        r4 = r7;
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x00b1, code lost:
        r7 = org.apache.james.mime4j.field.mimeversion.parser.MimeVersionParserTokenManager.jjnewLexState;
        r8 = r18.jjmatchedKind;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x00b7, code lost:
        if (r7[r8] == (-1)) goto L_0x0008;
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x00b9, code lost:
        r18.curLexState = r7[r8];
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public org.apache.james.mime4j.field.mimeversion.parser.Token getNextToken() {
        /*
            Method dump skipped, instructions count: 320
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.james.mime4j.field.mimeversion.parser.MimeVersionParserTokenManager.getNextToken():org.apache.james.mime4j.field.mimeversion.parser.Token");
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
