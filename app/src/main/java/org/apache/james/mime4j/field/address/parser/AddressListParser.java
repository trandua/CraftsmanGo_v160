package org.apache.james.mime4j.field.address.parser;

import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Vector;

/* loaded from: classes.dex */
public class AddressListParser implements AddressListParserTreeConstants, AddressListParserConstants {
    private static int[] jj_la1_0;
    private static int[] jj_la1_1;
    private final JJCalls[] jj_2_rtns;
    private int jj_endpos;
    private Vector<int[]> jj_expentries;
    private int[] jj_expentry;
    private int jj_gc;
    private int jj_gen;
    SimpleCharStream jj_input_stream;
    private int jj_kind;
    private int jj_la;
    private final int[] jj_la1;
    private Token jj_lastpos;
    private int[] jj_lasttokens;
    private final LookaheadSuccess jj_ls;
    public Token jj_nt;
    private int jj_ntk;
    private boolean jj_rescan;
    private Token jj_scanpos;
    private boolean jj_semLA;
    protected JJTAddressListParserState jjtree;
    public boolean lookingAhead;
    public Token token;
    public AddressListParserTokenManager token_source;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class JJCalls {
        int arg;
        Token first;
        int gen;
        JJCalls next;

        JJCalls() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class LookaheadSuccess extends Error {
        private LookaheadSuccess() {
        }
    }

    static {
        jj_la1_0();
        jj_la1_1();
    }

    public AddressListParser(InputStream inputStream) {
        this(inputStream, null);
    }

    public AddressListParser(InputStream inputStream, String str) {
        this.jjtree = new JJTAddressListParserState();
        int i = 0;
        this.lookingAhead = false;
        this.jj_la1 = new int[22];
        this.jj_2_rtns = new JJCalls[2];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_ls = new LookaheadSuccess();
        this.jj_expentries = new Vector<>();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];
        try {
            this.jj_input_stream = new SimpleCharStream(inputStream, str, 1, 1);
            this.token_source = new AddressListParserTokenManager(this.jj_input_stream);
            this.token = new Token();
            this.jj_ntk = -1;
            this.jj_gen = 0;
            for (int i2 = 0; i2 < 22; i2++) {
                this.jj_la1[i2] = -1;
            }
            while (true) {
                JJCalls[] jJCallsArr = this.jj_2_rtns;
                if (i < jJCallsArr.length) {
                    jJCallsArr[i] = new JJCalls();
                    i++;
                } else {
                    return;
                }
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public AddressListParser(Reader reader) {
        this.jjtree = new JJTAddressListParserState();
        int i = 0;
        this.lookingAhead = false;
        this.jj_la1 = new int[22];
        this.jj_2_rtns = new JJCalls[2];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_ls = new LookaheadSuccess();
        this.jj_expentries = new Vector<>();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];
        this.jj_input_stream = new SimpleCharStream(reader, 1, 1);
        this.token_source = new AddressListParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i2 = 0; i2 < 22; i2++) {
            this.jj_la1[i2] = -1;
        }
        while (true) {
            JJCalls[] jJCallsArr = this.jj_2_rtns;
            if (i < jJCallsArr.length) {
                jJCallsArr[i] = new JJCalls();
                i++;
            } else {
                return;
            }
        }
    }

    public AddressListParser(AddressListParserTokenManager addressListParserTokenManager) {
        this.jjtree = new JJTAddressListParserState();
        int i = 0;
        this.lookingAhead = false;
        this.jj_la1 = new int[22];
        this.jj_2_rtns = new JJCalls[2];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_ls = new LookaheadSuccess();
        this.jj_expentries = new Vector<>();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];
        this.token_source = addressListParserTokenManager;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i2 = 0; i2 < 22; i2++) {
            this.jj_la1[i2] = -1;
        }
        while (true) {
            JJCalls[] jJCallsArr = this.jj_2_rtns;
            if (i < jJCallsArr.length) {
                jJCallsArr[i] = new JJCalls();
                i++;
            } else {
                return;
            }
        }
    }

    private final boolean jj_2_1(int i) {
        this.jj_la = i;
        Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            return true ^ jj_3_1();
        } catch (LookaheadSuccess unused) {
            return true;
        } finally {
            jj_save(0, i);
        }
    }

    private final boolean jj_2_2(int i) {
        this.jj_la = i;
        Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            return !jj_3_2();
        } catch (LookaheadSuccess unused) {
            return true;
        } finally {
            jj_save(1, i);
        }
    }

    private final boolean jj_3R_10() {
        Token token = this.jj_scanpos;
        if (!jj_3R_12()) {
            return false;
        }
        this.jj_scanpos = token;
        return jj_scan_token(18);
    }

    private final boolean jj_3R_11() {
        Token token = this.jj_scanpos;
        if (jj_scan_token(9)) {
            this.jj_scanpos = token;
        }
        Token token2 = this.jj_scanpos;
        if (!jj_scan_token(14)) {
            return false;
        }
        this.jj_scanpos = token2;
        return jj_scan_token(31);
    }

    private final boolean jj_3R_12() {
        Token token;
        if (jj_scan_token(14)) {
            return true;
        }
        do {
            token = this.jj_scanpos;
        } while (!jj_3R_13());
        this.jj_scanpos = token;
        return false;
    }

    private final boolean jj_3R_13() {
        Token token = this.jj_scanpos;
        if (jj_scan_token(9)) {
            this.jj_scanpos = token;
        }
        return jj_scan_token(14);
    }

    private final boolean jj_3R_8() {
        return jj_3R_9() || jj_scan_token(8) || jj_3R_10();
    }

    private final boolean jj_3R_9() {
        Token token;
        Token token2 = this.jj_scanpos;
        if (jj_scan_token(14)) {
            this.jj_scanpos = token2;
            if (jj_scan_token(31)) {
                return true;
            }
        }
        do {
            token = this.jj_scanpos;
        } while (!jj_3R_11());
        this.jj_scanpos = token;
        return false;
    }

    private final boolean jj_3_1() {
        return jj_3R_8();
    }

    private final boolean jj_3_2() {
        return jj_3R_8();
    }

    private void jj_add_error_token(int i, int i2) {
        if (i2 < 100) {
            int i3 = this.jj_endpos;
            if (i2 == i3 + 1) {
                int[] iArr = this.jj_lasttokens;
                this.jj_endpos = i3 + 1;
                iArr[i3] = i;
            } else if (i3 != 0) {
                this.jj_expentry = new int[i3];
                for (int i4 = 0; i4 < this.jj_endpos; i4++) {
                    this.jj_expentry[i4] = this.jj_lasttokens[i4];
                }
                Enumeration<int[]> elements = this.jj_expentries.elements();
                boolean z = false;
                while (elements.hasMoreElements()) {
                    int[] nextElement = elements.nextElement();
                    if (nextElement.length == this.jj_expentry.length) {
                        int i5 = 0;
                        while (true) {
                            int[] iArr2 = this.jj_expentry;
                            if (i5 >= iArr2.length) {
                                z = true;
                                break;
                            } else if (nextElement[i5] != iArr2[i5]) {
                                z = false;
                                break;
                            } else {
                                i5++;
                            }
                        }
                        if (z) {
                            break;
                        }
                    }
                }
                if (!z) {
                    this.jj_expentries.addElement(this.jj_expentry);
                }
                if (i2 != 0) {
                    int[] iArr3 = this.jj_lasttokens;
                    this.jj_endpos = i2;
                    iArr3[i2 - 1] = i;
                }
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
            int i2 = this.jj_gc + 1;
            this.jj_gc = i2;
            if (i2 > 100) {
                int i3 = 0;
                this.jj_gc = 0;
                while (true) {
                    JJCalls[] jJCallsArr = this.jj_2_rtns;
                    if (i3 >= jJCallsArr.length) {
                        break;
                    }
                    for (JJCalls jJCalls = jJCallsArr[i3]; jJCalls != null; jJCalls = jJCalls.next) {
                        if (jJCalls.gen < this.jj_gen) {
                            jJCalls.first = null;
                        }
                    }
                    i3++;
                }
            }
            return this.token;
        }
        this.token = token;
        this.jj_kind = i;
        throw generateParseException();
    }

    private static void jj_la1_0() {
        jj_la1_0 = new int[]{2, -2147467200, 8, -2147467200, 80, -2147467200, -2147467200, -2147467200, 8, -2147467200, 256, 264, 8, -2147467264, -2147467264, -2147467264, -2147466752, 512, -2147467264, 16896, 512, 278528};
    }

    private static void jj_la1_1() {
        jj_la1_1 = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
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

    private final void jj_rescan_token() {
        this.jj_rescan = true;
        for (int i = 0; i < 2; i++) {
            try {
                JJCalls jJCalls = this.jj_2_rtns[i];
                do {
                    if (jJCalls.gen > this.jj_gen) {
                        this.jj_la = jJCalls.arg;
                        Token token = jJCalls.first;
                        this.jj_scanpos = token;
                        this.jj_lastpos = token;
                        if (i == 0) {
                            jj_3_1();
                        } else if (i == 1) {
                            jj_3_2();
                        }
                    }
                    jJCalls = jJCalls.next;
                } while (jJCalls != null);
            } catch (LookaheadSuccess unused) {
            }
        }
        this.jj_rescan = false;
    }

    private final void jj_save(int i, int i2) {
        JJCalls jJCalls = this.jj_2_rtns[i];
        while (true) {
            if (jJCalls.gen <= this.jj_gen) {
                break;
            } else if (jJCalls.next == null) {
                JJCalls jJCalls2 = new JJCalls();
                jJCalls.next = jJCalls2;
                jJCalls = jJCalls2;
                break;
            } else {
                jJCalls = jJCalls.next;
            }
        }
        jJCalls.gen = (this.jj_gen + i2) - this.jj_la;
        jJCalls.first = this.token;
        jJCalls.arg = i2;
    }

    private final boolean jj_scan_token(int i) {
        Token token = this.jj_scanpos;
        if (token == this.jj_lastpos) {
            this.jj_la--;
            if (token.next == null) {
                Token token2 = this.jj_scanpos;
                Token nextToken = this.token_source.getNextToken();
                token2.next = nextToken;
                this.jj_scanpos = nextToken;
                this.jj_lastpos = nextToken;
            } else {
                Token token3 = this.jj_scanpos.next;
                this.jj_scanpos = token3;
                this.jj_lastpos = token3;
            }
        } else {
            this.jj_scanpos = token.next;
        }
        if (this.jj_rescan) {
            Token token4 = this.token;
            int i2 = 0;
            while (token4 != null && token4 != this.jj_scanpos) {
                i2++;
                token4 = token4.next;
            }
            if (token4 != null) {
                jj_add_error_token(i, i2);
            }
        }
        if (this.jj_scanpos.kind != i) {
            return true;
        }
        if (this.jj_la != 0 || this.jj_scanpos != this.jj_lastpos) {
            return false;
        }
        throw this.jj_ls;
    }

    public static void main(String[] strArr) throws ParseException {
        while (true) {
            try {
                AddressListParser addressListParser = new AddressListParser(System.in);
                addressListParser.parseLine();
                ((SimpleNode) addressListParser.jjtree.rootNode()).dump("> ");
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
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
            this.jjtree.reset();
            int i = 0;
            this.jj_gen = 0;
            for (int i2 = 0; i2 < 22; i2++) {
                this.jj_la1[i2] = -1;
            }
            while (true) {
                JJCalls[] jJCallsArr = this.jj_2_rtns;
                if (i < jJCallsArr.length) {
                    jJCallsArr[i] = new JJCalls();
                    i++;
                } else {
                    return;
                }
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
        this.jjtree.reset();
        int i = 0;
        this.jj_gen = 0;
        for (int i2 = 0; i2 < 22; i2++) {
            this.jj_la1[i2] = -1;
        }
        while (true) {
            JJCalls[] jJCallsArr = this.jj_2_rtns;
            if (i < jJCallsArr.length) {
                jJCallsArr[i] = new JJCalls();
                i++;
            } else {
                return;
            }
        }
    }

    public void ReInit(AddressListParserTokenManager addressListParserTokenManager) {
        this.token_source = addressListParserTokenManager;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jjtree.reset();
        int i = 0;
        this.jj_gen = 0;
        for (int i2 = 0; i2 < 22; i2++) {
            this.jj_la1[i2] = -1;
        }
        while (true) {
            JJCalls[] jJCallsArr = this.jj_2_rtns;
            if (i < jJCallsArr.length) {
                jJCallsArr[i] = new JJCalls();
                i++;
            } else {
                return;
            }
        }
    }

    public final void addr_spec() throws ParseException {
        boolean z;
        Throwable th;
        ASTaddr_spec aSTaddr_spec = new ASTaddr_spec(9);
        this.jjtree.openNodeScope(aSTaddr_spec);
        jjtreeOpenNodeScope(aSTaddr_spec);
        try {
            local_part();
            jj_consume_token(8);
            domain();
            this.jjtree.closeNodeScope((Node) aSTaddr_spec, true);
            jjtreeCloseNodeScope(aSTaddr_spec);
        } catch (Throwable th2) {
            try {
                this.jjtree.clearNodeScope(aSTaddr_spec);
                z = false;
                try {
                    if (th2 instanceof RuntimeException) {
                        throw ((RuntimeException) th2);
                    } else if (th2 instanceof ParseException) {
                        throw th2;
                    } else {
                        throw ((Error) th2);
                    }
                } catch (Throwable th3) {
                    th = th3;
                    if (z) {
                        this.jjtree.closeNodeScope((Node) aSTaddr_spec, true);
                        jjtreeCloseNodeScope(aSTaddr_spec);
                    }
                    throw th;
                }
            } catch (Throwable th4) {
                th = th4;
                z = true;
            }
        }
    }

    public final void address() throws ParseException {
        boolean z;
        Throwable th;
        ASTaddress aSTaddress = new ASTaddress(2);
        this.jjtree.openNodeScope(aSTaddress);
        jjtreeOpenNodeScope(aSTaddress);
        try {
            if (jj_2_1(Integer.MAX_VALUE)) {
                addr_spec();
            } else {
                int i = this.jj_ntk;
                if (i == -1) {
                    i = jj_ntk();
                }
                if (i != 6) {
                    if (!(i == 14 || i == 31)) {
                        this.jj_la1[5] = this.jj_gen;
                        jj_consume_token(-1);
                        throw new ParseException();
                    }
                    phrase();
                    int i2 = this.jj_ntk;
                    if (i2 == -1) {
                        i2 = jj_ntk();
                    }
                    if (i2 == 4) {
                        group_body();
                    } else if (i2 == 6) {
                        angle_addr();
                    } else {
                        this.jj_la1[4] = this.jj_gen;
                        jj_consume_token(-1);
                        throw new ParseException();
                    }
                } else {
                    angle_addr();
                }
            }
            this.jjtree.closeNodeScope((Node) aSTaddress, true);
            jjtreeCloseNodeScope(aSTaddress);
        } catch (Throwable th2) {
            try {
                this.jjtree.clearNodeScope(aSTaddress);
                z = false;
                try {
                    if (th2 instanceof RuntimeException) {
                        throw ((RuntimeException) th2);
                    } else if (th2 instanceof ParseException) {
                        throw th2;
                    } else {
                        throw ((Error) th2);
                    }
                } catch (Throwable th3) {
                    th = th3;
                    if (z) {
                        this.jjtree.closeNodeScope((Node) aSTaddress, true);
                        jjtreeCloseNodeScope(aSTaddress);
                    }
                    throw th;
                }
            } catch (Throwable th4) {
                th = th4;
                z = true;
            }
        }
    }

    public final void address_list() throws ParseException {
        boolean z;
        Throwable th;
        ASTaddress_list aSTaddress_list = new ASTaddress_list(1);
        this.jjtree.openNodeScope(aSTaddress_list);
        jjtreeOpenNodeScope(aSTaddress_list);
        try {
            int i = this.jj_ntk;
            if (i == -1) {
                i = jj_ntk();
            }
            if (i == 6 || i == 14 || i == 31) {
                address();
            } else {
                this.jj_la1[1] = this.jj_gen;
            }
            while (true) {
                int i2 = this.jj_ntk;
                if (i2 == -1) {
                    i2 = jj_ntk();
                }
                if (i2 != 3) {
                    this.jj_la1[2] = this.jj_gen;
                    this.jjtree.closeNodeScope((Node) aSTaddress_list, true);
                    jjtreeCloseNodeScope(aSTaddress_list);
                    return;
                }
                jj_consume_token(3);
                int i3 = this.jj_ntk;
                if (i3 == -1) {
                    i3 = jj_ntk();
                }
                if (i3 == 6 || i3 == 14 || i3 == 31) {
                    address();
                } else {
                    this.jj_la1[3] = this.jj_gen;
                }
            }
        } catch (Throwable th2) {
            try {
                this.jjtree.clearNodeScope(aSTaddress_list);
                z = false;
                try {
                    if (th2 instanceof RuntimeException) {
                        throw ((RuntimeException) th2);
                    } else if (th2 instanceof ParseException) {
                        throw th2;
                    } else {
                        throw ((Error) th2);
                    }
                } catch (Throwable th3) {
                    th = th3;
                    if (z) {
                        this.jjtree.closeNodeScope((Node) aSTaddress_list, true);
                        jjtreeCloseNodeScope(aSTaddress_list);
                    }
                    throw th;
                }
            } catch (Throwable th4) {
                th = th4;
                z = true;
            }
        }
    }

    public final void angle_addr() throws ParseException {
        boolean z;
        Throwable th;
        ASTangle_addr aSTangle_addr = new ASTangle_addr(6);
        this.jjtree.openNodeScope(aSTangle_addr);
        jjtreeOpenNodeScope(aSTangle_addr);
        try {
            jj_consume_token(6);
            int i = this.jj_ntk;
            if (i == -1) {
                i = jj_ntk();
            }
            if (i != 8) {
                this.jj_la1[10] = this.jj_gen;
            } else {
                route();
            }
            addr_spec();
            jj_consume_token(7);
            this.jjtree.closeNodeScope((Node) aSTangle_addr, true);
            jjtreeCloseNodeScope(aSTangle_addr);
        } catch (Throwable th2) {
            try {
                this.jjtree.clearNodeScope(aSTangle_addr);
                z = false;
                try {
                    if (th2 instanceof RuntimeException) {
                        throw ((RuntimeException) th2);
                    } else if (th2 instanceof ParseException) {
                        throw th2;
                    } else {
                        throw ((Error) th2);
                    }
                } catch (Throwable th3) {
                    th = th3;
                    if (z) {
                        this.jjtree.closeNodeScope((Node) aSTangle_addr, true);
                        jjtreeCloseNodeScope(aSTangle_addr);
                    }
                    throw th;
                }
            } catch (Throwable th4) {
                th = th4;
                z = true;
            }
        }
    }

    public final void disable_tracing() {
    }

    public final void domain() throws ParseException {
        ASTdomain aSTdomain = new ASTdomain(11);
        this.jjtree.openNodeScope(aSTdomain);
        jjtreeOpenNodeScope(aSTdomain);
        try {
            int i = this.jj_ntk;
            if (i == -1) {
                i = jj_ntk();
            }
            if (i == 14) {
                Token jj_consume_token = jj_consume_token(14);
                while (true) {
                    int i2 = this.jj_ntk;
                    if (i2 == -1) {
                        i2 = jj_ntk();
                    }
                    if (i2 != 9 && i2 != 14) {
                        this.jj_la1[19] = this.jj_gen;
                        break;
                    }
                    int i3 = this.jj_ntk;
                    if (i3 == -1) {
                        i3 = jj_ntk();
                    }
                    if (i3 != 9) {
                        this.jj_la1[20] = this.jj_gen;
                    } else {
                        jj_consume_token = jj_consume_token(9);
                    }
                    if (jj_consume_token.image.charAt(jj_consume_token.image.length() - 1) == '.') {
                        jj_consume_token = jj_consume_token(14);
                    } else {
                        throw new ParseException("Atoms in domain names must be separated by '.'");
                    }
                }
            } else if (i == 18) {
                jj_consume_token(18);
            } else {
                this.jj_la1[21] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
            }
        } finally {
            this.jjtree.closeNodeScope((Node) aSTdomain, true);
            jjtreeCloseNodeScope(aSTdomain);
        }
    }

    public final void enable_tracing() {
    }

    public ParseException generateParseException() {
//        this.jj_expentries.removeAllElements();
//        boolean[] zArr = new boolean[34];
//        for (int i = 0; i < 34; i++) {
//            zArr[i] = false;
//        }
//        int i2 = this.jj_kind;
//        if (i2 >= 0) {
//            zArr[i2] = true;
//            this.jj_kind = -1;
//        }
//        for (int i3 = 0; i3 < 22; i3++) {
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
//        for (int i6 = 0; i6 < 34; i6++) {
//            if (zArr[i6]) {
//                this.jj_expentry = r5;
//                int[] iArr = {i6};
//                this.jj_expentries.addElement(iArr);
//            }
//        }
//        this.jj_endpos = 0;
//        jj_rescan_token();
//        jj_add_error_token(0, 0);
//        int[][] iArr2 = new int[this.jj_expentries.size()];
//        for (int i7 = 0; i7 < this.jj_expentries.size(); i7++) {
//            iArr2[i7] = this.jj_expentries.elementAt(i7);
//        }
//        return new ParseException(this.token, iArr2, tokenImage);
        return null;
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
        Token token = this.lookingAhead ? this.jj_scanpos : this.token;
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

    public final void group_body() throws ParseException {
        boolean z;
        Throwable th;
        ASTgroup_body aSTgroup_body = new ASTgroup_body(5);
        this.jjtree.openNodeScope(aSTgroup_body);
        jjtreeOpenNodeScope(aSTgroup_body);
        try {
            jj_consume_token(4);
            int i = this.jj_ntk;
            if (i == -1) {
                i = jj_ntk();
            }
            if (i == 6 || i == 14 || i == 31) {
                mailbox();
            } else {
                this.jj_la1[7] = this.jj_gen;
            }
            while (true) {
                int i2 = this.jj_ntk;
                if (i2 == -1) {
                    i2 = jj_ntk();
                }
                if (i2 != 3) {
                    this.jj_la1[8] = this.jj_gen;
                    jj_consume_token(5);
                    this.jjtree.closeNodeScope((Node) aSTgroup_body, true);
                    jjtreeCloseNodeScope(aSTgroup_body);
                    return;
                }
                jj_consume_token(3);
                int i3 = this.jj_ntk;
                if (i3 == -1) {
                    i3 = jj_ntk();
                }
                if (i3 == 6 || i3 == 14 || i3 == 31) {
                    mailbox();
                } else {
                    this.jj_la1[9] = this.jj_gen;
                }
            }
        } catch (Throwable th2) {
            try {
                this.jjtree.clearNodeScope(aSTgroup_body);
                z = false;
                try {
                    if (th2 instanceof RuntimeException) {
                        throw ((RuntimeException) th2);
                    } else if (th2 instanceof ParseException) {
                        throw th2;
                    } else {
                        throw ((Error) th2);
                    }
                } catch (Throwable th3) {
                    th = th3;
                    if (z) {
                        this.jjtree.closeNodeScope((Node) aSTgroup_body, true);
                        jjtreeCloseNodeScope(aSTgroup_body);
                    }
                    throw th;
                }
            } catch (Throwable th4) {
                th = th4;
                z = true;
            }
        }
    }

    void jjtreeCloseNodeScope(Node node) {
        ((SimpleNode) node).lastToken = getToken(0);
    }

    void jjtreeOpenNodeScope(Node node) {
        ((SimpleNode) node).firstToken = getToken(1);
    }

    public final void local_part() throws ParseException {
        Token token;
        ASTlocal_part aSTlocal_part = new ASTlocal_part(10);
        this.jjtree.openNodeScope(aSTlocal_part);
        jjtreeOpenNodeScope(aSTlocal_part);
        try {
            int i = this.jj_ntk;
            if (i == -1) {
                i = jj_ntk();
            }
            if (i == 14) {
                token = jj_consume_token(14);
            } else if (i == 31) {
                token = jj_consume_token(31);
            } else {
                this.jj_la1[15] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
            }
            while (true) {
                int i2 = this.jj_ntk;
                if (i2 == -1) {
                    i2 = jj_ntk();
                }
                if (i2 == 9 || i2 == 14 || i2 == 31) {
                    int i3 = this.jj_ntk;
                    if (i3 == -1) {
                        i3 = jj_ntk();
                    }
                    if (i3 != 9) {
                        this.jj_la1[17] = this.jj_gen;
                    } else {
                        token = jj_consume_token(9);
                    }
                    if (token.kind == 31 || token.image.charAt(token.image.length() - 1) != '.') {
                        break;
                    }
                    int i4 = this.jj_ntk;
                    if (i4 == -1) {
                        i4 = jj_ntk();
                    }
                    if (i4 == 14) {
                        token = jj_consume_token(14);
                    } else if (i4 == 31) {
                        token = jj_consume_token(31);
                    } else {
                        this.jj_la1[18] = this.jj_gen;
                        jj_consume_token(-1);
                        throw new ParseException();
                    }
                } else {
                    this.jj_la1[16] = this.jj_gen;
                    return;
                }
            }
            throw new ParseException("Words in local part must be separated by '.'");
        } finally {
            this.jjtree.closeNodeScope((Node) aSTlocal_part, true);
            jjtreeCloseNodeScope(aSTlocal_part);
        }
    }

    public final void mailbox() throws ParseException {
        boolean z;
        Throwable th;
        ASTmailbox aSTmailbox = new ASTmailbox(3);
        this.jjtree.openNodeScope(aSTmailbox);
        jjtreeOpenNodeScope(aSTmailbox);
        try {
            if (jj_2_2(Integer.MAX_VALUE)) {
                addr_spec();
            } else {
                int i = this.jj_ntk;
                if (i == -1) {
                    i = jj_ntk();
                }
                if (i != 6) {
                    if (!(i == 14 || i == 31)) {
                        this.jj_la1[6] = this.jj_gen;
                        jj_consume_token(-1);
                        throw new ParseException();
                    }
                    name_addr();
                } else {
                    angle_addr();
                }
            }
            this.jjtree.closeNodeScope((Node) aSTmailbox, true);
            jjtreeCloseNodeScope(aSTmailbox);
        } catch (Throwable th2) {
            try {
                this.jjtree.clearNodeScope(aSTmailbox);
                z = false;
                try {
                    if (th2 instanceof RuntimeException) {
                        throw ((RuntimeException) th2);
                    } else if (th2 instanceof ParseException) {
                        throw th2;
                    } else {
                        throw ((Error) th2);
                    }
                } catch (Throwable th3) {
                    th = th3;
                    if (z) {
                        this.jjtree.closeNodeScope((Node) aSTmailbox, true);
                        jjtreeCloseNodeScope(aSTmailbox);
                    }
                    throw th;
                }
            } catch (Throwable th4) {
                th = th4;
                z = true;
            }
        }
    }

    public final void name_addr() throws ParseException {
        boolean z;
        Throwable th;
        ASTname_addr aSTname_addr = new ASTname_addr(4);
        this.jjtree.openNodeScope(aSTname_addr);
        jjtreeOpenNodeScope(aSTname_addr);
        try {
            phrase();
            angle_addr();
            this.jjtree.closeNodeScope((Node) aSTname_addr, true);
            jjtreeCloseNodeScope(aSTname_addr);
        } catch (Throwable th2) {
            try {
                this.jjtree.clearNodeScope(aSTname_addr);
                z = false;
                try {
                    if (th2 instanceof RuntimeException) {
                        throw ((RuntimeException) th2);
                    } else if (th2 instanceof ParseException) {
                        throw th2;
                    } else {
                        throw ((Error) th2);
                    }
                } catch (Throwable th3) {
                    th = th3;
                    if (z) {
                        this.jjtree.closeNodeScope((Node) aSTname_addr, true);
                        jjtreeCloseNodeScope(aSTname_addr);
                    }
                    throw th;
                }
            } catch (Throwable th4) {
                th = th4;
                z = true;
            }
        }
    }

    public ASTaddress parseAddress() throws ParseException {
        try {
            parseAddress0();
            return (ASTaddress) this.jjtree.rootNode();
        } catch (TokenMgrError e) {
            throw new ParseException(e.getMessage());
        }
    }

    public final void parseAddress0() throws ParseException {
        address();
        jj_consume_token(0);
    }

    public ASTaddress_list parseAddressList() throws ParseException {
        try {
            parseAddressList0();
            return (ASTaddress_list) this.jjtree.rootNode();
        } catch (TokenMgrError e) {
            throw new ParseException(e.getMessage());
        }
    }

    public final void parseAddressList0() throws ParseException {
        address_list();
        jj_consume_token(0);
    }

    public final void parseLine() throws ParseException {
        address_list();
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
    }

    public ASTmailbox parseMailbox() throws ParseException {
        try {
            parseMailbox0();
            return (ASTmailbox) this.jjtree.rootNode();
        } catch (TokenMgrError e) {
            throw new ParseException(e.getMessage());
        }
    }

    public final void parseMailbox0() throws ParseException {
        mailbox();
        jj_consume_token(0);
    }

    public final void phrase() throws ParseException {
        ASTphrase aSTphrase = new ASTphrase(8);
        this.jjtree.openNodeScope(aSTphrase);
        jjtreeOpenNodeScope(aSTphrase);
        while (true) {
            try {
                int i = this.jj_ntk;
                if (i == -1) {
                    i = jj_ntk();
                }
                if (i == 14) {
                    jj_consume_token(14);
                } else if (i == 31) {
                    jj_consume_token(31);
                } else {
                    this.jj_la1[13] = this.jj_gen;
                    jj_consume_token(-1);
                    throw new ParseException();
                }
                int i2 = this.jj_ntk;
                if (i2 == -1) {
                    i2 = jj_ntk();
                }
                if (i2 != 14 && i2 != 31) {
                    this.jj_la1[14] = this.jj_gen;
                    return;
                }
            } finally {
                this.jjtree.closeNodeScope((Node) aSTphrase, true);
                jjtreeCloseNodeScope(aSTphrase);
            }
        }
    }

    public final void route() throws ParseException {
        boolean z;
        Throwable th;
        ASTroute aSTroute = new ASTroute(7);
        this.jjtree.openNodeScope(aSTroute);
        jjtreeOpenNodeScope(aSTroute);
        try {
            jj_consume_token(8);
            domain();
            while (true) {
                int i = this.jj_ntk;
                if (i == -1) {
                    i = jj_ntk();
                }
                if (i == 3 || i == 8) {
                    while (true) {
                        int i2 = this.jj_ntk;
                        if (i2 == -1) {
                            i2 = jj_ntk();
                        }
                        if (i2 != 3) {
                            break;
                        }
                        jj_consume_token(3);
                    }
                    this.jj_la1[12] = this.jj_gen;
                    jj_consume_token(8);
                    domain();
                } else {
                    this.jj_la1[11] = this.jj_gen;
                    jj_consume_token(4);
                    this.jjtree.closeNodeScope((Node) aSTroute, true);
                    jjtreeCloseNodeScope(aSTroute);
                    return;
                }
            }
        } catch (Throwable th2) {
            try {
                this.jjtree.clearNodeScope(aSTroute);
                z = false;
            } catch (Throwable th3) {
                th = th3;
                z = true;
            }
            try {
                if (th2 instanceof RuntimeException) {
                    throw ((RuntimeException) th2);
                } else if (th2 instanceof ParseException) {
                    throw th2;
                } else {
                    throw ((Error) th2);
                }
            } catch (Throwable th4) {
                th = th4;
                if (z) {
                    this.jjtree.closeNodeScope((Node) aSTroute, true);
                    jjtreeCloseNodeScope(aSTroute);
                }
//                throw th;
            }
        }
    }
}
