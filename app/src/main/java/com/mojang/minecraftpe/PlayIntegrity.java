package com.mojang.minecraftpe;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.integrity.IntegrityManager;
import com.google.android.play.core.integrity.IntegrityManagerFactory;
import com.google.android.play.core.integrity.IntegrityTokenRequest;
import com.google.android.play.core.integrity.IntegrityTokenResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
//import com.craftsman.go.StringFog;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.UUID;
import javax.crypto.spec.SecretKeySpec;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwx.JsonWebStructure;

/* loaded from: classes3.dex */
public class PlayIntegrity {
    private final int AES_KEY_SIZE_BYTES = 32;
    private final long ALLOWED_WINDOW_MILLIS = 20000;
    private final String DECRYPTION_KEY = "3ryCOT1pFunTCGJqgjPP6m5riSGMiiqecLgKcJRxGC8=";
    public final int VERDICT_RESULT_CODE_ERROR_BAD_NONCE = -5;
    public final int VERDICT_RESULT_CODE_ERROR_PARSE = -4;
    public final int VERDICT_RESULT_CODE_ERROR_TIMEOUT = -2;
    public final int VERDICT_RESULT_CODE_ERROR_TOKEN_EXPIRED = -6;
    public final int VERDICT_RESULT_CODE_ERROR_TOKEN_TAMPERED = -3;
    public final int VERDICT_RESULT_CODE_UNLICENSED = -1;
    public final int VERDICT_RESULT_CODE_VERIFIED = 0;
    private final String VERIFICATION_KEY = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEFqKPlgRr9q6HgEN8bAPc/hK3tYBu81R07jt6rOMDGiYB62O5/k5a8dhs1CPqKdbKarX2U6UEDuurNrhsiIiSXg==";
    private Context mContext;
    private String mNonce;

    public native void nativePlayIntegrityComplete(int i, String str, String str2, String str3, String str4);

    public PlayIntegrity(Context context) {
        this.mContext = context;
    }

    public void startIntegrityCheck() {
        IntegrityManager create = IntegrityManagerFactory.create(this.mContext);
        String uuid = UUID.randomUUID().toString();
        this.mNonce = uuid;
        create.requestIntegrityToken(IntegrityTokenRequest.builder().setNonce(Base64.encodeToString(uuid.getBytes(StandardCharsets.UTF_8), 1)).build()).addOnCompleteListener(new OnCompleteListener<IntegrityTokenResponse>() { // from class: com.mojang.minecraftpe.PlayIntegrity.1
            @Override // com.google.android.gms.tasks.OnCompleteListener
            public void onComplete(Task<IntegrityTokenResponse> task) {
                String str;
                if (task.isSuccessful()) {
                    try {
                        String str2 = task.getResult().token();
                        byte[] decode = Base64.decode("3ryCOT1pFunTCGJqgjPP6m5riSGMiiqecLgKcJRxGC8=", 0);
                        byte[] decode2 = Base64.decode("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEFqKPlgRr9q6HgEN8bAPc/hK3tYBu81R07jt6rOMDGiYB62O5/k5a8dhs1CPqKdbKarX2U6UEDuurNrhsiIiSXg==", 0);
                        SecretKeySpec secretKeySpec = new SecretKeySpec(decode, 0, 32, "AES");
                        PublicKey generatePublic = KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(decode2));
                        JsonWebEncryption jsonWebEncryption = (JsonWebEncryption) JsonWebStructure.fromCompactSerialization(str2);
                        jsonWebEncryption.setKey(secretKeySpec);
                        JsonWebSignature jsonWebSignature = (JsonWebSignature) JsonWebStructure.fromCompactSerialization(jsonWebEncryption.getPayload());
                        jsonWebSignature.setKey(generatePublic);
                        str = jsonWebSignature.getPayload();
                    } catch (Exception e) {
                        String decrypt = "MCPE";
                        Log.e(decrypt, "PlayIntegrity result error: "+ e.getMessage());
                        String str3 = new String();
                        PlayIntegrity.this.nativePlayIntegrityComplete(-3, str3, str3, str3, str3);
                        str = null;
                    }
                    if (str != null) {
                        try {
                            IntegrityResponse validateVerdict = PlayIntegrity.this.validateVerdict(str);
                            PlayIntegrity.this.nativePlayIntegrityComplete(validateVerdict.verdictResult, validateVerdict.packageName, validateVerdict.appRecognitionVerdict, validateVerdict.deviceIntegrity, validateVerdict.appLicensingVerdict);
                            return;
                        } catch (Exception e2) {
//                            String decrypt2 = StringFog.decrypt("UKSISQ==\n", "HefYDGOCHbE=\n");
//                            Log.e(decrypt2, StringFog.decrypt("TDx9J80/8Tt7InUq/XH3O28lcCqkNPcscyImfg==\n", "HFAcXoRRhV4=\n") + e2.getMessage());
                            String str4 = new String();
                            PlayIntegrity.this.nativePlayIntegrityComplete(-4, str4, str4, str4, str4);
                            return;
                        }
                    }
                    return;
                }
                Exception exception = task.getException();
//                String decrypt3 = StringFog.decrypt("QpesVA==\n", "D9T8Efd6Ys4=\n");
//                Log.e(decrypt3, StringFog.decrypt("HaLlfYkmZ64qvO1wuWhhrj676HDgLWG5Iry+JA==\n", "Tc6EBMBIE8s=\n") + exception.getMessage());
                String str5 = new String();
                PlayIntegrity.this.nativePlayIntegrityComplete(-2, str5, str5, str5, str5);
            }
        });
    }

    public IntegrityResponse validateVerdict(String str) {
        int i;
        IntegrityResponse integrityResponse = new IntegrityResponse();
        JsonObject asJsonObject = JsonParser.parseString(str).getAsJsonObject();
        if (asJsonObject == null) {
            integrityResponse.verdictResult = -4;
            return integrityResponse;
        }
        JsonObject asJsonObject2 = asJsonObject.get("requestDetails").getAsJsonObject();
        JsonObject asJsonObject3 = asJsonObject.get("appIntegrity").getAsJsonObject();
        JsonObject asJsonObject4 = asJsonObject.get("deviceIntegrity").getAsJsonObject();
        JsonObject asJsonObject5 = asJsonObject.get("accountDetails").getAsJsonObject();
        if (asJsonObject2 == null || asJsonObject3 == null || asJsonObject4 == null || asJsonObject5 == null) {
            integrityResponse.verdictResult = -4;
            return integrityResponse;
        }
        asJsonObject2.get("requestPackageName").getAsString();
        String asString = asJsonObject2.get("nonce").getAsString();
        long parseLong = Long.parseLong(asJsonObject2.get("timestampMillis").getAsString(), 10);
        long currentTimeMillis = System.currentTimeMillis();
        if (!new String(Base64.decode(asString.getBytes(StandardCharsets.UTF_8), 0), StandardCharsets.UTF_8).equals(this.mNonce)) {
//            Log.w(StringFog.decrypt("kHYA+g==\n", "3TVQv9CSevs=\n"), StringFog.decrypt("NrYunSKDOZ4WsjCQL4E52233NpwjwiOULrQn1CKNKIgu8DbUK4M5mCj3NpwjwiKVJfcxkSiW\n", "QNdC9EbiTfs=\n"));
            integrityResponse.verdictResult = -5;
        }
        if (currentTimeMillis - parseLong > 20000) {
//            Log.w(StringFog.decrypt("iTkLTQ==\n", "xHpbCG9nvsk=\n"), StringFog.decrypt("ZkMKUkvhD2lGRxRfRuMPLD0CElNKoA9je0cIG0fhCCx1WhZSXeUf\n", "ECJmOy+Aeww=\n"));
            integrityResponse.verdictResult = -6;
        }
        String asString2 = asJsonObject3.get("appRecognitionVerdict").getAsString();
        String asString3 = asJsonObject3.get("packageName").getAsString();
        int i2 = -1;
        if (asString2.equals("PLAY_RECOGNIZED")) {
            i = 0;
        } else {
//            Log.w(StringFog.decrypt("yqVJEg==\n", "h+YZV07aDmg=\n"), StringFog.decrypt("QNHLzroQrqxz18eaoRqj42Lc3IqhFrnjfcqOgKcB7ZNY+PexmjCOjFP357SNMQ==\n", "FLmu7sh1zcM=\n"));
            i = -1;
        }
        JsonArray asJsonArray = asJsonObject4.get("deviceRecognitionVerdict").getAsJsonArray();
        if (!asJsonArray.contains(new JsonPrimitive("MEETS_DEVICE_INTEGRITY"))) {
//            Log.w(StringFog.decrypt("PK8Jsg==\n", "cexZ939aJwc=\n"), StringFog.decrypt("nTAiyxSLQcKsNmvbVZJLia8qJNUUnkCJvDY/ykGMWt6mKj/QTd9KzL8xKN0a\n", "yVhLuDT/Lqk=\n"));
        }
        String asString4 = asJsonObject5.get("appLicensingVerdict").getAsString();
        if (asString4.equals("LICENSED")) {
            i2 = i;
        } else {
//            Log.w(StringFog.decrypt("krFoWg==\n", "3/I4H3+wRYI=\n"), StringFog.decrypt("HL5MAsISco09uF1mxgVwiySlB0PTAV2LK7NHUcofdrQtpE1LwAUxizv2R03XUV2rC5NnceY1Pw==\n", "SNYpIqNxEeI=\n"));
        }
        integrityResponse.verdictResult = i2;
        integrityResponse.packageName = asString3;
        integrityResponse.appRecognitionVerdict = asString2;
        if (!asJsonArray.isEmpty()) {
            integrityResponse.deviceIntegrity = asJsonArray.get(0).getAsString();
        }
        integrityResponse.appLicensingVerdict = asString4;
        return integrityResponse;
    }
}
