package org.apache.james.mime4j.field.datetime.parser;


import java.io.IOException;
import java.io.PrintStream;

/* loaded from: classes.dex */
public class DateTimeParserTokenManager implements DateTimeParserConstants {
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
    public static final String[] jjstrLiteralImages = {"", "\r", "\n", ",", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec", ":", null, "UT", "GMT", "EST", "EDT", "CST", "CDT", "MST", "MDT", "PST", "PDT", null, null, null, null, null, null, null, null, null, null, null, null, null, null};
    public static final String[] lexStateNames = {"DEFAULT", "INCOMMENT", "NESTED_COMMENT"};
    public static final int[] jjnewLexState = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 0, -1, 2, -1, -1, -1, -1, -1, -1, -1, -1};
    static final long[] jjtoToken = {70437463654399L};
    static final long[] jjtoSkip = {343597383680L};
    static final long[] jjtoSpecial = {68719476736L};
    static final long[] jjtoMore = {69956427317248L};

    public DateTimeParserTokenManager(SimpleCharStream simpleCharStream) {
        this.debugStream = System.out;
        this.jjrounds = new int[4];
        this.jjstateSet = new int[8];
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.input_stream = simpleCharStream;
    }

    public DateTimeParserTokenManager(SimpleCharStream simpleCharStream, int i) {
        this(simpleCharStream);
        SwitchTo(i);
    }

    private final void ReInitRounds() {
        this.jjround = -2147483647;
        int i = 4;
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
        this.jjnewStateCnt = 4;
        this.jjstateSet[0] = i;
        int i3 = 1;
        int i4 = Integer.MAX_VALUE;
        int i5 = i2;
        int i6 = 0;
        int i7 = 1;
        int i8 = Integer.MAX_VALUE;
        while (true) {
            int i9 = this.jjround + i3;
            this.jjround = i9;
            if (i9 == i4) {
                ReInitRounds();
            }
            char c = this.curChar;
            if (c < '@') {
                long j = 1 << c;
                while (true) {
                    i7--;
                    int i10 = this.jjstateSet[i7];
                    if (i10 != 0) {
                        if (i10 != 2) {
                            if (i10 == 3 && (j & 287948901175001088L) != 0) {
                                jjCheckNAdd(3);
                                i8 = 46;
                            }
                        } else if ((j & 4294967808L) != 0) {
                            jjCheckNAdd(2);
                            i8 = 36;
                        }
                    } else if ((287948901175001088L & j) != 0) {
                        if (i8 > 46) {
                            i8 = 46;
                        }
                        jjCheckNAdd(3);
                    } else if ((j & 4294967808L) != 0) {
                        if (i8 > 36) {
                            i8 = 36;
                        }
                        jjCheckNAdd(2);
                    } else if ((43980465111040L & j) != 0 && i8 > 24) {
                        i8 = 24;
                    }
                    if (i7 == i6) {
                        break;
                    }
                }
            } else if (c < 128) {
                long j2 = 1 << (c & '?');
                do {
                    i7--;
                    if (this.jjstateSet[i7] == 0 && (576456345801194494L & j2) != 0) {
                        i8 = 35;
                        continue;
                    }
                } while (i7 != i6);
            } else {
                do {
                    i7--;
                    int i11 = this.jjstateSet[i7];
                } while (i7 != i6);
            }
            if (i8 != Integer.MAX_VALUE) {
                this.jjmatchedKind = i8;
                this.jjmatchedPos = i5;
                i8 = Integer.MAX_VALUE;
            }
            i5++;
            i7 = this.jjnewStateCnt;
            this.jjnewStateCnt = i6;
            i6 = 4 - i6;
            if (i7 == i6) {
                return i5;
            }
            try {
                this.curChar = this.input_stream.readChar();
                i3 = 1;
                i4 = Integer.MAX_VALUE;
            } catch (IOException unused) {
                return i5;
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
                        if (i8 == 1 && i6 > 39) {
                            i6 = 39;
                            continue;
                        }
                    } else if (i6 > 41) {
                        i6 = 41;
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
                        if (i6 > 41) {
                            i6 = 41;
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
                        if (i9 == 2 && i6 > 41) {
                            i6 = 41;
                            continue;
                        }
                    } else if (i6 > 39) {
                        i6 = 39;
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
                        if (i12 == 1 && (jjbitVec0[i11] & j) != 0 && i6 > 39) {
                            i6 = 39;
                            continue;
                        }
                    } else if ((jjbitVec0[i11] & j) != 0 && i6 > 41) {
                        i6 = 41;
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
                        if (i8 == 1 && i6 > 42) {
                            i6 = 42;
                            continue;
                        }
                    } else if (i6 > 45) {
                        i6 = 45;
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
                        if (i6 > 45) {
                            i6 = 45;
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
                        if (i9 == 2 && i6 > 45) {
                            i6 = 45;
                            continue;
                        }
                    } else if (i6 > 42) {
                        i6 = 42;
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
                        if (i12 == 1 && (jjbitVec0[i11] & j) != 0 && i6 > 42) {
                            i6 = 42;
                            continue;
                        }
                    } else if ((jjbitVec0[i11] & j) != 0 && i6 > 45) {
                        i6 = 45;
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
        if (c == '\n') {
            return jjStopAtPos(0, 2);
        }
        if (c == '\r') {
            return jjStopAtPos(0, 1);
        }
        if (c == '(') {
            return jjStopAtPos(0, 37);
        }
        if (c == ',') {
            return jjStopAtPos(0, 3);
        }
        if (c == ':') {
            return jjStopAtPos(0, 23);
        }
        if (c == 'A') {
            return jjMoveStringLiteralDfa1_0(278528L);
        }
        if (c == 'J') {
            return jjMoveStringLiteralDfa1_0(198656L);
        }
        if (c == 'W') {
            return jjMoveStringLiteralDfa1_0(64L);
        }
        switch (c) {
            case 'C':
                return jjMoveStringLiteralDfa1_0(1610612736L);
            case 'D':
                return jjMoveStringLiteralDfa1_0(4194304L);
            case 'E':
                return jjMoveStringLiteralDfa1_0(402653184L);
            case 'F':
                return jjMoveStringLiteralDfa1_0(4352L);
            case 'G':
                return jjMoveStringLiteralDfa1_0(67108864L);
            default:
                switch (c) {
                    case 'M':
                        return jjMoveStringLiteralDfa1_0(6442491920L);
                    case 'N':
                        return jjMoveStringLiteralDfa1_0(2097152);
                    case 'O':
                        return jjMoveStringLiteralDfa1_0(1048576L);
                    case 'P':
                        return jjMoveStringLiteralDfa1_0(25769803776L);
                    default:
                        switch (c) {
                            case 'S':
                                return jjMoveStringLiteralDfa1_0(525824L);
                            case 'T':
                                return jjMoveStringLiteralDfa1_0(160L);
                            case 'U':
                                return jjMoveStringLiteralDfa1_0(33554432L);
                            default:
                                return jjMoveNfa_0(0, 0);
                        }
                }
        }
    }

    private final int jjMoveStringLiteralDfa0_1() {
        char c = this.curChar;
        return c != '(' ? c != ')' ? jjMoveNfa_1(0, 0) : jjStopAtPos(0, 38) : jjStopAtPos(0, 40);
    }

    private final int jjMoveStringLiteralDfa0_2() {
        char c = this.curChar;
        return c != '(' ? c != ')' ? jjMoveNfa_2(0, 0) : jjStopAtPos(0, 44) : jjStopAtPos(0, 43);
    }

    private final int jjMoveStringLiteralDfa1_0(long j) {
        try {
            char readChar = this.input_stream.readChar();
            this.curChar = readChar;
            if (readChar == 'D') {
                return jjMoveStringLiteralDfa2_0(j, 22817013760L);
            }
            if (readChar == 'M') {
                return jjMoveStringLiteralDfa2_0(j, 67108864L);
            }
            if (readChar == 'a') {
                return jjMoveStringLiteralDfa2_0(j, 43520L);
            }
            if (readChar == 'c') {
                return jjMoveStringLiteralDfa2_0(j, 1048576L);
            }
            if (readChar == 'e') {
                return jjMoveStringLiteralDfa2_0(j, 4722752L);
            }
            if (readChar == 'h') {
                return jjMoveStringLiteralDfa2_0(j, 128L);
            }
            if (readChar == 'r') {
                return jjMoveStringLiteralDfa2_0(j, 256L);
            }
            if (readChar == 'u') {
                return jjMoveStringLiteralDfa2_0(j, 459808L);
            }
            if (readChar == 'S') {
                return jjMoveStringLiteralDfa2_0(j, 11408506880L);
            }
            if (readChar != 'T') {
                if (readChar == 'o') {
                    return jjMoveStringLiteralDfa2_0(j, 2097168L);
                }
                if (readChar == 'p') {
                    return jjMoveStringLiteralDfa2_0(j, 16384L);
                }
            } else if ((33554432 & j) != 0) {
                return jjStopAtPos(1, 25);
            }
            return jjStartNfa_0(0, j);
        } catch (IOException unused) {
            jjStopStringLiteralDfa_0(0, j);
            return 1;
        }
    }

    private final int jjMoveStringLiteralDfa2_0(long j, long j2) {
        long j3 = j2 & j;
        if (j3 == 0) {
            return jjStartNfa_0(0, j);
        }
        try {
            char readChar = this.input_stream.readChar();
            this.curChar = readChar;
            if (readChar != 'T') {
                if (readChar != 'g') {
                    if (readChar != 'i') {
                        if (readChar != 'l') {
                            if (readChar != 'n') {
                                if (readChar != 'p') {
                                    if (readChar != 'r') {
                                        if (readChar != 'y') {
                                            switch (readChar) {
                                                case 'b':
                                                    if ((4096 & j3) != 0) {
                                                        return jjStopAtPos(2, 12);
                                                    }
                                                    break;
                                                case 'c':
                                                    if ((4194304 & j3) != 0) {
                                                        return jjStopAtPos(2, 22);
                                                    }
                                                    break;
                                                case 'd':
                                                    if ((64 & j3) != 0) {
                                                        return jjStopAtPos(2, 6);
                                                    }
                                                    break;
                                                case 'e':
                                                    if ((32 & j3) != 0) {
                                                        return jjStopAtPos(2, 5);
                                                    }
                                                    break;
                                                default:
                                                    switch (readChar) {
                                                        case 't':
                                                            if ((512 & j3) != 0) {
                                                                return jjStopAtPos(2, 9);
                                                            }
                                                            if ((1048576 & j3) != 0) {
                                                                return jjStopAtPos(2, 20);
                                                            }
                                                            break;
                                                        case 'u':
                                                            if ((128 & j3) != 0) {
                                                                return jjStopAtPos(2, 7);
                                                            }
                                                            break;
                                                        case 'v':
                                                            if ((2097152 & j3) != 0) {
                                                                return jjStopAtPos(2, 21);
                                                            }
                                                            break;
                                                    }
                                            }
                                        } else if ((32768 & j3) != 0) {
                                            return jjStopAtPos(2, 15);
                                        }
                                    } else if ((8192 & j3) != 0) {
                                        return jjStopAtPos(2, 13);
                                    } else {
                                        if ((16384 & j3) != 0) {
                                            return jjStopAtPos(2, 14);
                                        }
                                    }
                                } else if ((524288 & j3) != 0) {
                                    return jjStopAtPos(2, 19);
                                }
                            } else if ((16 & j3) != 0) {
                                return jjStopAtPos(2, 4);
                            } else {
                                if ((1024 & j3) != 0) {
                                    return jjStopAtPos(2, 10);
                                }
                                if ((2048 & j3) != 0) {
                                    return jjStopAtPos(2, 11);
                                }
                                if ((65536 & j3) != 0) {
                                    return jjStopAtPos(2, 16);
                                }
                            }
                        } else if ((131072 & j3) != 0) {
                            return jjStopAtPos(2, 17);
                        }
                    } else if ((256 & j3) != 0) {
                        return jjStopAtPos(2, 8);
                    }
                } else if ((262144 & j3) != 0) {
                    return jjStopAtPos(2, 18);
                }
            } else if ((67108864 & j3) != 0) {
                return jjStopAtPos(2, 26);
            } else {
                if ((134217728 & j3) != 0) {
                    return jjStopAtPos(2, 27);
                }
                if ((268435456 & j3) != 0) {
                    return jjStopAtPos(2, 28);
                }
                if ((536870912 & j3) != 0) {
                    return jjStopAtPos(2, 29);
                }
                if ((1073741824 & j3) != 0) {
                    return jjStopAtPos(2, 30);
                }
                if ((2147483648L & j3) != 0) {
                    return jjStopAtPos(2, 31);
                }
                if ((4294967296L & j3) != 0) {
                    return jjStopAtPos(2, 32);
                }
                if ((8589934592L & j3) != 0) {
                    return jjStopAtPos(2, 33);
                }
                if ((17179869184L & j3) != 0) {
                    return jjStopAtPos(2, 34);
                }
            }
            return jjStartNfa_0(1, j3);
        } catch (IOException unused) {
            jjStopStringLiteralDfa_0(1, j3);
            return 2;
        }
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

    private final int jjStartNfa_0(int i, long j) {
        return jjMoveNfa_0(jjStopStringLiteralDfa_0(i, j), i + 1);
    }

    private final int jjStartNfa_1(int i, long j) {
        return jjMoveNfa_1(jjStopStringLiteralDfa_1(i, j), i + 1);
    }

    private final int jjStartNfa_2(int i, long j) {
        return jjMoveNfa_2(jjStopStringLiteralDfa_2(i, j), i + 1);
    }

    private final int jjStopAtPos(int i, int i2) {
        this.jjmatchedKind = i2;
        this.jjmatchedPos = i;
        return i + 1;
    }

    private final int jjStopStringLiteralDfa_0(int i, long j) {
        if (i != 0) {
            if (i == 1 && (j & 34334373872L) != 0 && this.jjmatchedPos == 0) {
                this.jjmatchedKind = 35;
                this.jjmatchedPos = 0;
            }
            return -1;
        }
        if ((j & 34334373872L) != 0) {
            this.jjmatchedKind = 35;
        }
        return -1;
    }

    private final int jjStopStringLiteralDfa_1(int i, long j) {
        return -1;
    }

    private final int jjStopStringLiteralDfa_2(int i, long j) {
        return -1;
    }

    void MoreLexicalActions() {
        StringBuffer stringBuffer = null;
        StringBuffer stringBuffer2 = null;
        int i = this.jjimageLen;
        int i2 = this.jjmatchedPos + 1;
        this.lengthOfMatch = i2;
        this.jjimageLen = i + i2;
        switch (this.jjmatchedKind) {
            case 39:
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                this.image.deleteCharAt(stringBuffer.length() - 2);
                return;
            case 40:
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                commentNest = 1;
                return;
            case 41:
            default:
                return;
            case 42:
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                this.image.deleteCharAt(stringBuffer2.length() - 2);
                return;
            case 43:
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                commentNest++;
                return;
            case 44:
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
        if (i >= 3 || i < 0) {
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " + i + ". State unchanged.", 2);
        }
        this.curLexState = i;
    }

    /* JADX WARN: Code restructure failed: missing block: B:27:0x0094, code lost:
        if (((1 << (r9 & 63)) & org.apache.james.mime4j.field.datetime.parser.DateTimeParserTokenManager.jjtoSpecial[r9 >> 6]) == 0) goto L_0x00a2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0096, code lost:
        r7 = jjFillToken();
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x009a, code lost:
        if (r4 != null) goto L_0x009d;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x009d, code lost:
        r7.specialToken = r4;
        r4.next = r7;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x00a1, code lost:
        r4 = r7;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x00a2, code lost:
        r7 = org.apache.james.mime4j.field.datetime.parser.DateTimeParserTokenManager.jjnewLexState;
        r8 = r18.jjmatchedKind;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x00a8, code lost:
        if (r7[r8] == (-1)) goto L_0x0008;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x00aa, code lost:
        r18.curLexState = r7[r8];
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public org.apache.james.mime4j.field.datetime.parser.Token getNextToken() {
        /*
            Method dump skipped, instructions count: 305
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.james.mime4j.field.datetime.parser.DateTimeParserTokenManager.getNextToken():org.apache.james.mime4j.field.datetime.parser.Token");
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
