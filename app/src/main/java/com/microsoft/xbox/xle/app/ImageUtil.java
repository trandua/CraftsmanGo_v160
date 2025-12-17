package com.microsoft.xbox.xle.app;

import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.net.URI;
import org.spongycastle.crypto.tls.CipherSuite;

/* loaded from: classes3.dex */
public class ImageUtil {
    public static final int LARGE_PHONE = 640;
    public static final int LARGE_TABLET = 800;
    public static final int MEDIUM_PHONE = 300;
    public static final int MEDIUM_TABLET = 424;
    public static final int SMALL = 200;
    public static final int TINY = 100;
    public static final String resizeFormatter = "&w=%d&h=%d&format=png";
    public static final String resizeFormatterSizeOnly = "&w=%d&h=%d";
    public static final String resizeFormatterWithPadding = "&mode=padding&w=%d&h=%d&format=png";

    /* loaded from: classes3.dex */
    static class C54971 {
        static final int[] $SwitchMap$com$microsoft$xbox$xle$app$ImageUtil$ImageType;

        C54971() {
        }

        static {
            int[] iArr = new int[ImageType.values().length];
            $SwitchMap$com$microsoft$xbox$xle$app$ImageUtil$ImageType = iArr;
            try {
                iArr[ImageType.TINY.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$xle$app$ImageUtil$ImageType[ImageType.TINY_3X4.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$xle$app$ImageUtil$ImageType[ImageType.TINY_4X3.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$xle$app$ImageUtil$ImageType[ImageType.SMALL.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$xle$app$ImageUtil$ImageType[ImageType.SMALL_3X4.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$xle$app$ImageUtil$ImageType[ImageType.SMALL_4X3.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
        }
    }

    /* loaded from: classes3.dex */
    public enum ImageType {
        TINY,
        TINY_3X4,
        TINY_4X3,
        SMALL,
        SMALL_3X4,
        SMALL_4X3,
        MEDIUM,
        MEDIUM_3X4,
        MEDIUM_4X3,
        LARGE,
        LARGE_3X4;

        public static ImageType fromString(String str) {
            try {
                return valueOf(str);
            } catch (IllegalArgumentException | NullPointerException unused) {
                return null;
            }
        }
    }

    private static URI createUri(String str) {
        if (str != null) {
            try {
                return URI.create(str);
            } catch (IllegalArgumentException unused) {
                return null;
            }
        }
        return null;
    }

    private static URI formatString(String str, int i, int i2) {
        String format;
        String sb;
        StringBuilder sb2 = null;
        if (str == null || !str.contains("images-eds")) {
            return null;
        }
        boolean contains = str.contains("&w=");
        boolean contains2 = str.contains("&h=");
        if (!contains || !contains2) {
            if (contains) {
                sb2 = new StringBuilder();
                sb2.append(str.replaceAll("w=[0-9]+", "w=" + i));
                sb2.append("&h=");
                sb2.append(i2);
            } else if (contains2) {
                str.replaceAll("h=[0-9]+", "h=" + i2);
            } else {
                if (str.contains("format=")) {
                    sb2 = new StringBuilder();
                    sb2.append(str);
                    format = String.format(resizeFormatterSizeOnly, Integer.valueOf(i), Integer.valueOf(i2));
                } else {
                    sb2 = new StringBuilder();
                    sb2.append(str);
                    format = String.format(resizeFormatter, Integer.valueOf(i), Integer.valueOf(i2));
                }
                sb2.append(format);
            }
            sb = sb2.toString();
        } else {
            String replaceAll = str.replaceAll("w=[0-9]+", "w=" + i);
            sb = replaceAll.replaceAll("h=[0-9]+", "h=" + i2);
        }
        return createUri(sb);
    }

    private static URI formatURI(URI uri, int i, int i2) {
        if (uri == null) {
            return null;
        }
        URI formatString = formatString(uri.toString(), i, i2);
        return formatString == null ? uri : formatString;
    }

    public static URI getLarge(String str) {
        int i = XboxTcuiSdk.getIsTablet() ? LARGE_TABLET : LARGE_PHONE;
        URI formatString = formatString(str, i, i);
        return (formatString != null || str == null) ? formatString : createUri(str);
    }

    public static URI getLarge(URI uri) {
        int i = XboxTcuiSdk.getIsTablet() ? LARGE_TABLET : LARGE_PHONE;
        return formatURI(uri, i, i);
    }

    public static URI getLarge3X4(String str) {
        return formatString(str, 720, 1080);
    }

    public static URI getLarge3X4(URI uri) {
        return formatURI(uri, 720, 1080);
    }

    public static URI getMedium(String str) {
        int i = XboxTcuiSdk.getIsTablet() ? 424 : 300;
        URI formatString = formatString(str, i, i);
        return (formatString != null || str == null) ? formatString : createUri(str);
    }

    public static URI getMedium(URI uri) {
        int i = XboxTcuiSdk.getIsTablet() ? 424 : 300;
        return formatURI(uri, i, i);
    }

    public static URI getMedium2X1(String str) {
        return formatString(str, 480, 270);
    }

    public static URI getMedium2X1(URI uri) {
        return formatURI(uri, 480, 270);
    }

    public static URI getMedium3X4(String str) {
        return formatString(str, 426, LARGE_PHONE);
    }

    public static URI getMedium3X4(URI uri) {
        return formatURI(uri, 426, LARGE_PHONE);
    }

    public static URI getMedium4X3(String str) {
        return formatString(str, 562, 316);
    }

    public static URI getMedium4X3(URI uri) {
        return formatURI(uri, 562, 316);
    }

    public static URI getSmall(String str) {
        URI formatString = formatString(str, 200, 200);
        return (formatString != null || str == null) ? formatString : createUri(str);
    }

    public static URI getSmall(URI uri) {
        return formatURI(uri, 200, 200);
    }

    public static URI getSmall2X1(String str) {
        return formatString(str, 243, CipherSuite.TLS_DH_anon_WITH_CAMELLIA_256_CBC_SHA);
    }

    public static URI getSmall2X1(URI uri) {
        return formatURI(uri, 243, CipherSuite.TLS_DH_anon_WITH_CAMELLIA_256_CBC_SHA);
    }

    public static URI getSmall3X4(String str) {
        return formatString(str, 215, 294);
    }

    public static URI getSmall3X4(URI uri) {
        return formatURI(uri, 215, 294);
    }

    public static URI getSmall4X3(String str) {
        return formatString(str, 275, 216);
    }

    public static URI getSmall4X3(URI uri) {
        return formatURI(uri, 275, 216);
    }

    public static URI getTiny(String str) {
        URI formatString = formatString(str, 100, 100);
        return formatString == null ? createUri(str) : formatString;
    }

    public static URI getTiny(URI uri) {
        return formatURI(uri, 100, 100);
    }

    public static URI getTiny2X1(String str) {
        return formatString(str, 150, 84);
    }

    public static URI getTiny2X1(URI uri) {
        return formatURI(uri, 150, 84);
    }

    public static URI getTiny3X4(String str) {
        return formatString(str, 85, 120);
    }

    public static URI getTiny3X4(URI uri) {
        return formatURI(uri, 85, 120);
    }

    public static URI getTiny4X3(String str) {
        return formatString(str, 120, 90);
    }

    public static URI getTiny4X3(URI uri) {
        return formatURI(uri, 120, 90);
    }

    public static URI getURI(String str, int i, int i2) {
        URI formatString = formatString(str, i, i2);
        return formatString == null ? createUri(str) : formatString;
    }

    public static URI getURI(URI uri, int i, int i2) {
        return formatURI(uri, i, i2);
    }

    public static URI getUri(String str, ImageType imageType) {
        if (imageType == null) {
            return getSmall(str);
        }
        switch (C54971.$SwitchMap$com$microsoft$xbox$xle$app$ImageUtil$ImageType[imageType.ordinal()]) {
            case 1:
                return getTiny(str);
            case 2:
                return getTiny3X4(str);
            case 3:
                return getTiny4X3(str);
            case 4:
                return getSmall(str);
            case 5:
                return getSmall3X4(str);
            case 6:
                return getSmall4X3(str);
            case 7:
                return getMedium(str);
            case 8:
                return getMedium3X4(str);
            case 9:
                return getMedium4X3(str);
            case 10:
                return getLarge(str);
            case 11:
                return getLarge3X4(str);
            default:
                return getSmall(str);
        }
    }

    public static URI getUri(URI uri, ImageType imageType) {
        if (imageType == null) {
            return getSmall(uri);
        }
        switch (C54971.$SwitchMap$com$microsoft$xbox$xle$app$ImageUtil$ImageType[imageType.ordinal()]) {
            case 1:
                return getTiny(uri);
            case 2:
                return getTiny3X4(uri);
            case 3:
                return getTiny4X3(uri);
            case 4:
                return getSmall(uri);
            case 5:
                return getSmall3X4(uri);
            case 6:
                return getSmall4X3(uri);
            case 7:
                return getMedium(uri);
            case 8:
                return getMedium3X4(uri);
            case 9:
                return getMedium4X3(uri);
            case 10:
                return getLarge(uri);
            case 11:
                return getLarge3X4(uri);
            default:
                return getSmall(uri);
        }
    }
}
