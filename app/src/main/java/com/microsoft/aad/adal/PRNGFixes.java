package com.microsoft.aad.adal;

import android.os.Build;
import android.os.Process;
import androidx.exifinterface.media.ExifInterface;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.Provider;
import java.security.SecureRandomSpi;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public final class PRNGFixes {
    private static final byte[] BUILD_FINGERPRINT_AND_DEVICE_SERIAL = getBuildFingerprintAndDeviceSerial();
    private static final int ONE_KB = 1024;
    private static final String TAG = "PRNGFixes";
    private static final int VERSION_CODE_JELLY_BEAN = 16;
    private static final int VERSION_CODE_JELLY_BEAN_MR2 = 18;

    /* loaded from: classes3.dex */
    public static class LinuxPRNGSecureRandom extends SecureRandomSpi {
        private static final Object SLOCK = new Object();
        private static final File URANDOM_FILE = new File("/dev/urandom");
        private static DataInputStream sUrandomIn = null;
        private static OutputStream sUrandomOut = null;
        private static final long serialVersionUID = 1;
        private boolean mSeeded;

        private DataInputStream getUrandomInputStream() {
            DataInputStream dataInputStream;
            synchronized (SLOCK) {
                if (sUrandomIn == null) {
                    try {
                        sUrandomIn = new DataInputStream(new FileInputStream(URANDOM_FILE));
                    } catch (IOException e) {
                        throw new SecurityException("Failed to open " + URANDOM_FILE + " for reading", e);
                    }
                }
                dataInputStream = sUrandomIn;
            }
            return dataInputStream;
        }

        private OutputStream getUrandomOutputStream() throws IOException {
            OutputStream outputStream;
            synchronized (SLOCK) {
                if (sUrandomOut == null) {
                    sUrandomOut = new FileOutputStream(URANDOM_FILE);
                }
                outputStream = sUrandomOut;
            }
            return outputStream;
        }

        @Override // java.security.SecureRandomSpi
        public byte[] engineGenerateSeed(int i) {
            byte[] bArr = new byte[i];
            engineNextBytes(bArr);
            return bArr;
        }

        @Override // java.security.SecureRandomSpi
        public void engineNextBytes(byte[] bArr) {
//            DataInputStream dataInputStream;
//            if (!this.mSeeded) {
//                engineSetSeed(PRNGFixes.generateSeed());
//            }
//            try {
//                dataInputStream = getUrandomInputStream();
//            } catch (IOException e) {
//                e = e;
//                dataInputStream = null;
//            }
//            try {
//                synchronized (SLOCK) {
//                    dataInputStream.readFully(bArr);
//                }
//                if (dataInputStream != null) {
//                    try {
//                        dataInputStream.close();
//                    } catch (IOException e2) {
//                        Logger.m14614v("PRNGFixesengineNextBytes", "Failed to close the input stream to \"/dev/urandom\" . Exception: " + e2.toString());
//                    }
//                }
//            } catch (IOException e3) {
//                e = e3;
//                try {
//                    throw new SecurityException("Failed to read from " + URANDOM_FILE, e);
//                } catch (Throwable th) {
//                    if (dataInputStream != null) {
//                        try {
//                            dataInputStream.close();
//                        } catch (IOException e4) {
//                            Logger.m14614v("PRNGFixesengineNextBytes", "Failed to close the input stream to \"/dev/urandom\" . Exception: " + e4.toString());
//                        }
//                    }
//                    throw th;
//                }
//            }
            if (!this.mSeeded) {
                engineSetSeed(PRNGFixes.generateSeed());
            }
            DataInputStream dataInputStream = null;
            try {
                try {
                    dataInputStream = getUrandomInputStream();
                    synchronized (SLOCK) {
                        dataInputStream.readFully(bArr);
                    }
                    if (dataInputStream != null) {
                        try {
                            dataInputStream.close();
                        } catch (IOException e) {
//                            Logger.v("PRNGFixesengineNextBytes", "Failed to close the input stream to \"/dev/urandom\" . Exception: " + e.toString());
                        }
                    }
                } catch (Throwable th) {
                    if (dataInputStream != null) {
                        try {
                            dataInputStream.close();
                        } catch (IOException e2) {
//                            Logger.v("PRNGFixesengineNextBytes", "Failed to close the input stream to \"/dev/urandom\" . Exception: " + e2.toString());
                        }
                    }
                    throw th;
                }
            } catch (IOException e3) {
                throw new SecurityException("Failed to read from " + URANDOM_FILE, e3);
            }
        }

        @Override // java.security.SecureRandomSpi
        public void engineSetSeed(byte[] bArr) {
//            StringBuilder sb;
//            OutputStream outputStream = null;
//            try {
//                try {
//                    outputStream = getUrandomOutputStream();
//                    outputStream.write(bArr);
//                    outputStream.flush();
//                    this.mSeeded = true;
//                    if (outputStream != null) {
//                        try {
//                            outputStream.close();
//                        } catch (IOException e) {
//                            e = e;
//                            sb = new StringBuilder("Failed to close the output stream to \"/dev/urandom\". Exception: ");
//                            sb.append(e.toString());
//                            Logger.m14614v("PRNGFixesengineSetSeed", sb.toString());
//                        }
//                    }
//                } catch (Throwable th) {
//                    if (outputStream != null) {
//                        try {
//                            outputStream.close();
//                        } catch (IOException e2) {
//                            Logger.m14614v("PRNGFixesengineSetSeed", "Failed to close the output stream to \"/dev/urandom\". Exception: " + e2.toString());
//                        }
//                    }
//                    throw th;
//                }
//            } catch (IOException unused) {
//                Logger.m14616w(PRNGFixes.TAG, "Failed to mix seed into " + URANDOM_FILE);
//                this.mSeeded = true;
//                if (outputStream != null) {
//                    try {
//                        outputStream.close();
//                    } catch (IOException e3) {
//                        e = e3;
//                        sb = new StringBuilder("Failed to close the output stream to \"/dev/urandom\". Exception: ");
//                        sb.append(e.toString());
//                        Logger.m14614v("PRNGFixesengineSetSeed", sb.toString());
//                    }
//                }
//            }
//        }
            StringBuilder sb;
            OutputStream outputStream = null;
            try {
                try {
                    outputStream = getUrandomOutputStream();
                    outputStream.write(bArr);
                    outputStream.flush();
                    this.mSeeded = true;
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            e = e;
                            sb = new StringBuilder();
                            sb.append("Failed to close the output stream to \"/dev/urandom\" . Exception: ");
                            sb.append(e.toString());
//                            Logger.v("PRNGFixesengineSetSeed", sb.toString());
                        }
                    }
                } catch (Throwable th) {
                    this.mSeeded = true;
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e2) {
//                            Logger.v("PRNGFixesengineSetSeed", "Failed to close the output stream to \"/dev/urandom\" . Exception: " + e2.toString());
                        }
                    }
                    throw th;
                }
            } catch (IOException unused) {
                String simpleName = PRNGFixes.class.getSimpleName();
//                Logger.w(simpleName, "Failed to mix seed into " + URANDOM_FILE);
                this.mSeeded = true;
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e3) {
//                        e = e3;
//                        sb = new StringBuilder();
//                        sb.append("Failed to close the output stream to \"/dev/urandom\" . Exception: ");
//                        sb.append(e.toString());
//                        Logger.v("PRNGFixesengineSetSeed", sb.toString());
                        e3.printStackTrace();
                    }
                }
            }
        }
    }

    /* loaded from: classes3.dex */
    private static class LinuxPRNGSecureRandomProvider extends Provider {
        private static final long serialVersionUID = 1;

        public LinuxPRNGSecureRandomProvider() {
            super("LinuxPRNG", 1.0d, "A Linux-specific random number provider that uses /dev/urandom");
            put("SecureRandom.SHA1PRNG", LinuxPRNGSecureRandom.class.getName());
            put("SecureRandom.SHA1PRNG ImplementedIn", ExifInterface.TAG_SOFTWARE);
        }
    }

    private PRNGFixes() {
    }

    public static void apply() {
        applyOpenSSLFix();
        installLinuxPRNGSecureRandom();
    }

    private static void applyOpenSSLFix() throws SecurityException {
        Logger.m14614v("PRNGFixes:applyOpenSSLFix", "No need to apply the OpenSSL fix.");
    }

    public static byte[] generateSeed() {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            dataOutputStream.writeLong(System.currentTimeMillis());
            dataOutputStream.writeLong(System.nanoTime());
            dataOutputStream.writeInt(Process.myPid());
            dataOutputStream.writeInt(Process.myUid());
            dataOutputStream.write(BUILD_FINGERPRINT_AND_DEVICE_SERIAL);
            dataOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new SecurityException("Failed to generate seed", e);
        }
    }

    private static byte[] getBuildFingerprintAndDeviceSerial() {
        StringBuilder sb = new StringBuilder();
        String str = Build.FINGERPRINT;
        if (str != null) {
            sb.append(str);
        }
        String deviceSerialNumber = getDeviceSerialNumber();
        if (deviceSerialNumber != null) {
            sb.append(deviceSerialNumber);
        }
        try {
            return sb.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 encoding not supported", e);
        }
    }

    private static String getDeviceSerialNumber() {
        try {
            return (String) Build.class.getField("SERIAL").get(null);
        } catch (Exception unused) {
            return null;
        }
    }

    private static void installLinuxPRNGSecureRandom() throws SecurityException {
        Logger.m14614v("PRNGFixes:installLinuxPRNGSecureRandom", "No need to apply the fix.");
    }
}
