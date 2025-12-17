package com.microsoft.aad.adal;

import android.util.Base64;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import org.jose4j.jwk.PublicJsonWebKey;
import org.jose4j.jwt.ReservedClaimNames;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public class JWSBuilder implements IJWSBuilder {
    private static final String JWS_ALGORITHM = "SHA256withRSA";
    private static final String JWS_HEADER_ALG = "RS256";
    private static final long SECONDS_MS = 1000;
    private static final String TAG = "JWSBuilder";

    /* loaded from: classes3.dex */
    final class Claims {
        @SerializedName(ReservedClaimNames.AUDIENCE)
        public String mAudience;
        @SerializedName(ReservedClaimNames.ISSUED_AT)
        public long mIssueAt;
        @SerializedName("nonce")
        public String mNonce;

        private Claims() {
        }
    }

    /* loaded from: classes3.dex */
    final class JwsHeader {
        @SerializedName("alg")
        public String mAlgorithm;
        @SerializedName(PublicJsonWebKey.X509_CERTIFICATE_CHAIN_PARAMETER)
        public String[] mCert;
        @SerializedName("typ")
        public String mType;

        private JwsHeader() {
        }
    }

    private static String sign(RSAPrivateKey rSAPrivateKey, byte[] bArr) throws AuthenticationException {
        try {
            Signature signature = Signature.getInstance(JWS_ALGORITHM);
            signature.initSign(rSAPrivateKey);
            signature.update(bArr);
            return StringExtensions.encodeBase64URLSafeString(signature.sign());
        } catch (UnsupportedEncodingException unused) {
            throw new AuthenticationException(ADALError.ENCODING_IS_NOT_SUPPORTED);
        } catch (InvalidKeyException e) {
            ADALError aDALError = ADALError.KEY_CHAIN_PRIVATE_KEY_EXCEPTION;
            throw new AuthenticationException(aDALError, "Invalid private RSA key: " + e.getMessage(), e);
        } catch (NoSuchAlgorithmException e2) {
            ADALError aDALError2 = ADALError.DEVICE_NO_SUCH_ALGORITHM;
            throw new AuthenticationException(aDALError2, "Unsupported RSA algorithm: " + e2.getMessage(), e2);
        } catch (SignatureException e3) {
            ADALError aDALError3 = ADALError.SIGNATURE_EXCEPTION;
            throw new AuthenticationException(aDALError3, "RSA signature exception: " + e3.getMessage(), e3);
        }
    }

    @Override // com.microsoft.aad.adal.IJWSBuilder
    public String generateSignedJWT(String str, String str2, RSAPrivateKey rSAPrivateKey, RSAPublicKey rSAPublicKey, X509Certificate x509Certificate) throws AuthenticationException {
        String str3 = "";
        if (StringExtensions.isNullOrBlank(str)) {
            throw new IllegalArgumentException("nonce");
        }
        if (StringExtensions.isNullOrBlank(str2)) {
            throw new IllegalArgumentException("audience");
        }
        if (rSAPrivateKey != null) {
            if (rSAPublicKey != null) {
                Gson gson = new Gson();
                Claims claims = new Claims();
                claims.mNonce = str;
                claims.mAudience = str2;
                claims.mIssueAt = System.currentTimeMillis() / 1000;
                JwsHeader jwsHeader = new JwsHeader();
                jwsHeader.mAlgorithm = "RS256";
                jwsHeader.mType = "JWT";
                try {
                    jwsHeader.mCert = new String[1];
                    jwsHeader.mCert[0] = new String(Base64.encode(x509Certificate.getEncoded(), 2), "UTF-8");
                    String json = gson.toJson(jwsHeader);
                    String json2 = gson.toJson(claims);
                    ADALError aDALError = null;
                    Logger.m14615v("JWSBuilder:generateSignedJWT", "Generate client certificate challenge response JWS Header. ", "Header: " + json, null);
                    return (StringExtensions.encodeBase64URLSafeString(json.getBytes("UTF-8")) + "." + StringExtensions.encodeBase64URLSafeString(json2.getBytes("UTF-8"))) + "." + sign(rSAPrivateKey, str3.getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new AuthenticationException(ADALError.ENCODING_IS_NOT_SUPPORTED, "Unsupported encoding", e);
                } catch (CertificateEncodingException e2) {
                    throw new AuthenticationException(ADALError.CERTIFICATE_ENCODING_ERROR, "Certificate encoding error", e2);
                }
            }
            throw new IllegalArgumentException("pubKey");
        }
        throw new IllegalArgumentException("privateKey");
    }
}
