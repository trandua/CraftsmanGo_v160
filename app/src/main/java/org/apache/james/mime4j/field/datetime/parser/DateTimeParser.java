package org.apache.james.mime4j.field.datetime.parser;

import androidx.core.view.ViewCompat;
import com.google.android.vending.expansion.downloader.Constants;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Vector;
import org.apache.james.mime4j.field.datetime.DateTime;

/* loaded from: classes.dex */
public class DateTimeParser implements DateTimeParserConstants {
    private static final boolean ignoreMilitaryZoneOffset = true;
    private static int[] jj_la1_0;
    private static int[] jj_la1_1;
    private Vector<int[]> jj_expentries;
    private int[] jj_expentry;
    private int jj_gen;
    SimpleCharStream jj_input_stream;
    private int jj_kind;
    private final int[] jj_la1;
    public Token jj_nt;
    private int jj_ntk;
    public Token token;
    public DateTimeParserTokenManager token_source;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Date {
        private int day;
        private int month;
        private String year;

        public Date(String str, int i, int i2) {
            this.year = str;
            this.month = i;
            this.day = i2;
        }

        public int getDay() {
            return this.day;
        }

        public int getMonth() {
            return this.month;
        }

        public String getYear() {
            return this.year;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Time {
        private int hour;
        private int minute;
        private int second;
        private int zone;

        public Time(int i, int i2, int i3, int i4) {
            this.hour = i;
            this.minute = i2;
            this.second = i3;
            this.zone = i4;
        }

        public int getHour() {
            return this.hour;
        }

        public int getMinute() {
            return this.minute;
        }

        public int getSecond() {
            return this.second;
        }

        public int getZone() {
            return this.zone;
        }
    }

    static {
        jj_la1_0();
        jj_la1_1();
    }

    public DateTimeParser(InputStream inputStream) {
        this(inputStream, null);
    }

    public DateTimeParser(InputStream inputStream, String str) {
        this.jj_la1 = new int[7];
        this.jj_expentries = new Vector<>();
        this.jj_kind = -1;
        try {
            this.jj_input_stream = new SimpleCharStream(inputStream, str, 1, 1);
            this.token_source = new DateTimeParserTokenManager(this.jj_input_stream);
            this.token = new Token();
            this.jj_ntk = -1;
            this.jj_gen = 0;
            for (int i = 0; i < 7; i++) {
                this.jj_la1[i] = -1;
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public DateTimeParser(Reader reader) {
        this.jj_la1 = new int[7];
        this.jj_expentries = new Vector<>();
        this.jj_kind = -1;
        this.jj_input_stream = new SimpleCharStream(reader, 1, 1);
        this.token_source = new DateTimeParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 7; i++) {
            this.jj_la1[i] = -1;
        }
    }

    public DateTimeParser(DateTimeParserTokenManager dateTimeParserTokenManager) {
        this.jj_la1 = new int[7];
        this.jj_expentries = new Vector<>();
        this.jj_kind = -1;
        this.token_source = dateTimeParserTokenManager;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 7; i++) {
            this.jj_la1[i] = -1;
        }
    }

    private static int getMilitaryZoneOffset(char c) {
        return 0;
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
        jj_la1_0 = new int[]{2, 2032, 2032, 8386560, 8388608, ViewCompat.MEASURED_STATE_MASK, -33554432};
    }

    private static void jj_la1_1() {
        jj_la1_1 = new int[]{0, 0, 0, 0, 0, 15, 15};
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

    public static void main(String[] strArr) throws ParseException {
        while (true) {
            try {
                new DateTimeParser(System.in).parseLine();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private static int parseDigits(Token token) {
        return Integer.parseInt(token.image, 10);
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
            for (int i = 0; i < 7; i++) {
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
        for (int i = 0; i < 7; i++) {
            this.jj_la1[i] = -1;
        }
    }

    public void ReInit(DateTimeParserTokenManager dateTimeParserTokenManager) {
        this.token_source = dateTimeParserTokenManager;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 7; i++) {
            this.jj_la1[i] = -1;
        }
    }

    public final Date date() throws ParseException {
        int day = day();
        return new Date(year(), month(), day);
    }

    public final DateTime date_time() throws ParseException {
        int i = this.jj_ntk;
        if (i == -1) {
            i = jj_ntk();
        }
        switch (i) {
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
                day_of_week();
                jj_consume_token(3);
                break;
            default:
                this.jj_la1[1] = this.jj_gen;
                break;
        }
        Date date = date();
        Time time = time();
        return new DateTime(date.getYear(), date.getMonth(), date.getDay(), time.getHour(), time.getMinute(), time.getSecond(), time.getZone());
    }

    public final int day() throws ParseException {
        return parseDigits(jj_consume_token(46));
    }

    public final String day_of_week() throws ParseException {
        int i = this.jj_ntk;
        if (i == -1) {
            i = jj_ntk();
        }
        switch (i) {
            case 4:
                jj_consume_token(4);
                break;
            case 5:
                jj_consume_token(5);
                break;
            case 6:
                jj_consume_token(6);
                break;
            case 7:
                jj_consume_token(7);
                break;
            case 8:
                jj_consume_token(8);
                break;
            case 9:
                jj_consume_token(9);
                break;
            case 10:
                jj_consume_token(10);
                break;
            default:
                this.jj_la1[2] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
        return this.token.image;
    }

    public final void disable_tracing() {
    }

    public final void enable_tracing() {
    }

    public ParseException generateParseException() {
//        this.jj_expentries.removeAllElements();
//        boolean[] zArr = new boolean[49];
//        for (int i = 0; i < 49; i++) {
//            zArr[i] = false;
//        }
//        int i2 = this.jj_kind;
//        if (i2 >= 0) {
//            zArr[i2] = true;
//            this.jj_kind = -1;
//        }
//        for (int i3 = 0; i3 < 7; i3++) {
//            if (this.jj_la1[i3] == this.jj_gen) {
//                for (int i4 = 0; i4 < 32; i4++) {
//                    int i5 = 1 << i4;
//                    if ((jj_la1_0[i3] & i5) != 0) {
//                        zArr[i4] = true;
//                    }
//                    if ((jj_la1_1[i3] & i5) != 0) {
//                        zArr[i4 + 32] = true;
//                    }
//                }
//            }
//        }
//        for (int i6 = 0; i6 < 49; i6++) {
//            if (zArr[i6]) {
//                this.jj_expentry = r5;
//                int[] iArr = {i6};
//                this.jj_expentries.addElement(iArr);
//            }
//        }
//        int[][] iArr2 = new int[this.jj_expentries.size()];
//        for (int i7 = 0; i7 < this.jj_expentries.size(); i7++) {
//            iArr2[i7] = this.jj_expentries.elementAt(i7);
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

    public final int hour() throws ParseException {
        return parseDigits(jj_consume_token(46));
    }

    public final int minute() throws ParseException {
        return parseDigits(jj_consume_token(46));
    }

    public final int month() throws ParseException {
        int i = this.jj_ntk;
        if (i == -1) {
            i = jj_ntk();
        }
        switch (i) {
            case 11:
                jj_consume_token(11);
                return 1;
            case 12:
                jj_consume_token(12);
                return 2;
            case 13:
                jj_consume_token(13);
                return 3;
            case 14:
                jj_consume_token(14);
                return 4;
            case 15:
                jj_consume_token(15);
                return 5;
            case 16:
                jj_consume_token(16);
                return 6;
            case 17:
                jj_consume_token(17);
                return 7;
            case 18:
                jj_consume_token(18);
                return 8;
            case 19:
                jj_consume_token(19);
                return 9;
            case 20:
                jj_consume_token(20);
                return 10;
            case 21:
                jj_consume_token(21);
                return 11;
            case 22:
                jj_consume_token(22);
                return 12;
            default:
                this.jj_la1[3] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
    }

    public final int obs_zone() throws ParseException {
        int i = this.jj_ntk;
        if (i == -1) {
            i = jj_ntk();
        }
        int i2 = -7;
        switch (i) {
            case 25:
                jj_consume_token(25);
                i2 = 0;
                break;
            case 26:
                jj_consume_token(26);
                i2 = 0;
                break;
            case 27:
                jj_consume_token(27);
                i2 = -5;
                break;
            case 28:
                jj_consume_token(28);
                i2 = -4;
                break;
            case 29:
                jj_consume_token(29);
                i2 = -6;
                break;
            case 30:
                jj_consume_token(30);
                i2 = -5;
                break;
            case 31:
                jj_consume_token(31);
                break;
            case 32:
                jj_consume_token(32);
                i2 = -6;
                break;
            case 33:
                jj_consume_token(33);
                i2 = -8;
                break;
            case 34:
                jj_consume_token(34);
                break;
            case 35:
                i2 = getMilitaryZoneOffset(jj_consume_token(35).image.charAt(0));
                break;
            default:
                this.jj_la1[6] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
        return i2 * 100;
    }

    public final DateTime parseAll() throws ParseException {
        DateTime date_time = date_time();
        jj_consume_token(0);
        return date_time;
    }

    public final DateTime parseLine() throws ParseException {
        DateTime date_time = date_time();
        int i = this.jj_ntk;
        if (i == -1) {
            i = jj_ntk();
        }
        if (i != 1) {
            this.jj_la1[0] = this.jj_gen;
        } else {
            jj_consume_token(1);
        }
        jj_consume_token(2);
        return date_time;
    }

    public final int second() throws ParseException {
        return parseDigits(jj_consume_token(46));
    }

    public final Time time() throws ParseException {
        int i;
        int hour = hour();
        jj_consume_token(23);
        int minute = minute();
        int i2 = this.jj_ntk;
        if (i2 == -1) {
            i2 = jj_ntk();
        }
        if (i2 != 23) {
            this.jj_la1[4] = this.jj_gen;
            i = 0;
        } else {
            jj_consume_token(23);
            i = second();
        }
        return new Time(hour, minute, i, zone());
    }

    public final String year() throws ParseException {
        return jj_consume_token(46).image;
    }

    public final int zone() throws ParseException {
        int i = this.jj_ntk;
        int i2 = -1;
        if (i == -1) {
            i = jj_ntk();
        }
        switch (i) {
            case 24:
                Token jj_consume_token = jj_consume_token(24);
                int parseDigits = parseDigits(jj_consume_token(46));
                if (!jj_consume_token.image.equals(Constants.FILENAME_SEQUENCE_SEPARATOR)) {
                    i2 = 1;
                }
                return parseDigits * i2;
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
                return obs_zone();
            default:
                this.jj_la1[5] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
    }
}
