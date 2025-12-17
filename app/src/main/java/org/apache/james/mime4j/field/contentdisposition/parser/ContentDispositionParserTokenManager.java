package org.apache.james.mime4j.field.contentdisposition.parser;

import com.microsoft.aad.adal.AuthenticationConstants;
import java.io.IOException;
import java.io.PrintStream;

/* loaded from: classes.dex */
public class ContentDispositionParserTokenManager implements ContentDispositionParserConstants {
    static int commentNest;
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
    public static final String[] jjstrLiteralImages = {"", "\r", "\n", AuthenticationConstants.Broker.CHALLENGE_REQUEST_CERT_AUTH_DELIMETER, "=", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null};
    public static final String[] lexStateNames = {"DEFAULT", "INCOMMENT", "NESTED_COMMENT", "INQUOTEDSTRING"};
    public static final int[] jjnewLexState = {-1, -1, -1, -1, -1, -1, 1, 0, -1, 2, -1, -1, -1, -1, -1, 3, -1, -1, 0, -1, -1, -1, -1};
    static final long[] jjtoToken = {1835039};
    static final long[] jjtoSkip = {160};
    static final long[] jjtoSpecial = {32};
    static final long[] jjtoMore = {261952};

    public ContentDispositionParserTokenManager(SimpleCharStream simpleCharStream) {
        this.debugStream = System.out;
        this.jjrounds = new int[3];
        this.jjstateSet = new int[6];
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.input_stream = simpleCharStream;
    }

    public ContentDispositionParserTokenManager(SimpleCharStream simpleCharStream, int i) {
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
        this.jjnewStateCnt = 3;
        this.jjstateSet[0] = i;
        int i3 = Integer.MAX_VALUE;
        int i4 = i2;
        int i5 = 1;
        int i6 = Integer.MAX_VALUE;
        int i7 = 0;
        while (true) {
            int i8 = this.jjround + 1;
            this.jjround = i8;
            if (i8 == i3) {
                ReInitRounds();
            }
            char c = this.curChar;
            if (c < '@') {
                long j = 1 << c;
                while (true) {
                    i5--;
                    int i9 = this.jjstateSet[i5];
                    if (i9 != 0) {
                        i6 = 19;
                        if (i9 != 1) {
                            if (i9 != 2) {
                                if (i9 == 3) {
                                    if ((j & 288068726467591679L) != 0) {
                                        if (i6 > 20) {
                                            i6 = 20;
                                        }
                                        jjCheckNAdd(2);
                                    } else if ((j & 4294967808L) != 0) {
                                        if (i6 > 5) {
                                            i6 = 5;
                                        }
                                        jjCheckNAdd(0);
                                    }
                                    if ((j & 287948901175001088L) != 0) {
                                        if (i6 <= 19) {
                                            i6 = i6;
                                        }
                                        jjCheckNAdd(1);
                                    }
                                }
                            } else if ((j & 288068726467591679L) != 0) {
                                if (i6 > 20) {
                                    i6 = 20;
                                }
                                jjCheckNAdd(2);
                            }
                        } else if ((j & 287948901175001088L) != 0) {
                            if (i6 > 19) {
                                i6 = 19;
                            }
                            jjCheckNAdd(1);
                        }
                    } else if ((j & 4294967808L) != 0) {
                        jjCheckNAdd(0);
                        i6 = 5;
                    }
                    if (i5 == i7) {
                        break;
                    }
                }
            } else if (c < 128) {
                long j2 = 1 << (c & '?');
                do {
                    i5--;
                    int i10 = this.jjstateSet[i5];
                    if ((i10 == 2 || i10 == 3) && (j2 & (-939524098)) != 0) {
                        jjCheckNAdd(2);
                        i6 = 20;
                        continue;
                    }
                } while (i5 != i7);
            } else {
                int i11 = (c & 255) >> 6;
                long j3 = 1 << (c & '?');
                do {
                    i5--;
                    int i12 = this.jjstateSet[i5];
                    if ((i12 == 2 || i12 == 3) && (jjbitVec0[i11] & j3) != 0) {
                        if (i6 > 20) {
                            i6 = 20;
                        }
                        jjCheckNAdd(2);
                        continue;
                    }
                } while (i5 != i7);
            }
            if (i6 != Integer.MAX_VALUE) {
                this.jjmatchedKind = i6;
                this.jjmatchedPos = i4;
                i6 = Integer.MAX_VALUE;
            }
            i4++;
            i5 = this.jjnewStateCnt;
            this.jjnewStateCnt = i7;
            i7 = 3 - i7;
            if (i5 == i7) {
                return i4;
            }
            try {
                this.curChar = this.input_stream.readChar();
                i3 = Integer.MAX_VALUE;
            } catch (IOException unused) {
                return i4;
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
                        if (i8 == 1 && i6 > 8) {
                            i6 = 8;
                            continue;
                        }
                    } else if (i6 > 10) {
                        i6 = 10;
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
                        if (i6 > 10) {
                            i6 = 10;
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
                        if (i9 == 2 && i6 > 10) {
                            i6 = 10;
                            continue;
                        }
                    } else if (i6 > 8) {
                        i6 = 8;
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
                        if (i12 == 1 && (jjbitVec0[i11] & j) != 0 && i6 > 8) {
                            i6 = 8;
                            continue;
                        }
                    } else if ((jjbitVec0[i11] & j) != 0 && i6 > 10) {
                        i6 = 10;
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
                        if (i8 == 1 && i6 > 11) {
                            i6 = 11;
                            continue;
                        }
                    } else if (i6 > 14) {
                        i6 = 14;
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
                        if (i6 > 14) {
                            i6 = 14;
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
                        if (i9 == 2 && i6 > 14) {
                            i6 = 14;
                            continue;
                        }
                    } else if (i6 > 11) {
                        i6 = 11;
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
                        if (i12 == 1 && (jjbitVec0[i11] & j) != 0 && i6 > 11) {
                            i6 = 11;
                            continue;
                        }
                    } else if ((jjbitVec0[i11] & j) != 0 && i6 > 14) {
                        i6 = 14;
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
                        } else if (i6 > 16) {
                            i6 = 16;
                            continue;
                        } else {
                            continue;
                        }
                    }
                    if ((j & (-17179869185L)) != 0) {
                        if (i6 > 17) {
                            i6 = 17;
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
                                if (i6 > 17) {
                                    i6 = 17;
                                }
                                jjCheckNAdd(2);
                                continue;
                            }
                        } else if (i6 > 16) {
                            i6 = 16;
                            continue;
                        } else {
                            continue;
                        }
                    } else if ((j2 & (-268435457)) != 0) {
                        if (i6 > 17) {
                            i6 = 17;
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
                        } else if ((jjbitVec0[i11] & j3) != 0 && i6 > 16) {
                            i6 = 16;
                            continue;
                        }
                    }
                    if ((jjbitVec0[i11] & j3) != 0) {
                        if (i6 > 17) {
                            i6 = 17;
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
        return c != '\n' ? c != '\r' ? c != '\"' ? c != '(' ? c != ';' ? c != '=' ? jjMoveNfa_0(3, 0) : jjStopAtPos(0, 4) : jjStopAtPos(0, 3) : jjStopAtPos(0, 6) : jjStopAtPos(0, 15) : jjStartNfaWithStates_0(0, 1, 2) : jjStartNfaWithStates_0(0, 2, 2);
    }

    private final int jjMoveStringLiteralDfa0_1() {
        char c = this.curChar;
        return c != '(' ? c != ')' ? jjMoveNfa_1(0, 0) : jjStopAtPos(0, 7) : jjStopAtPos(0, 9);
    }

    private final int jjMoveStringLiteralDfa0_2() {
        char c = this.curChar;
        return c != '(' ? c != ')' ? jjMoveNfa_2(0, 0) : jjStopAtPos(0, 13) : jjStopAtPos(0, 12);
    }

    private final int jjMoveStringLiteralDfa0_3() {
        return this.curChar != '\"' ? jjMoveNfa_3(0, 0) : jjStopAtPos(0, 18);
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
            case 8:
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                this.image.deleteCharAt(stringBuffer.length() - 2);
                return;
            case 9:
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                commentNest = 1;
                return;
            case 10:
            case 14:
            default:
                return;
            case 11:
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                this.image.deleteCharAt(stringBuffer2.length() - 2);
                return;
            case 12:
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                commentNest++;
                return;
            case 13:
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                int i3 = commentNest - 1;
                commentNest = i3;
                if (i3 == 0) {
                    SwitchTo(1);
                    return;
                }
                return;
            case 15:
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                StringBuffer stringBuffer4 = this.image;
                stringBuffer4.deleteCharAt(stringBuffer4.length() - 1);
                return;
            case 16:
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
        if (this.jjmatchedKind == 18) {
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
        if (((1 << (r9 & 63)) & org.apache.james.mime4j.field.contentdisposition.parser.ContentDispositionParserTokenManager.jjtoSpecial[r9 >> 6]) == 0) goto L_0x00b1;
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
        r7 = org.apache.james.mime4j.field.contentdisposition.parser.ContentDispositionParserTokenManager.jjnewLexState;
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
    public org.apache.james.mime4j.field.contentdisposition.parser.Token getNextToken() {
        /*
            Method dump skipped, instructions count: 320
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.james.mime4j.field.contentdisposition.parser.ContentDispositionParserTokenManager.getNextToken():org.apache.james.mime4j.field.contentdisposition.parser.Token");
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
