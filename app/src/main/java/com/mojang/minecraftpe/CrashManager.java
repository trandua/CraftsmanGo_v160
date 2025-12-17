package com.mojang.minecraftpe;

import android.util.Log;
import android.util.Pair;
import com.google.android.vending.expansion.downloader.Constants;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.Thread;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class CrashManager {
    private String mCrashDumpFolder;
    private String mCrashUploadURI;
    private String mCrashUploadURIWithSentryKey;
    private String mCurrentSessionId;
    private String mExceptionUploadURI;
    private Thread.UncaughtExceptionHandler mPreviousUncaughtExceptionHandler = null;

    public CrashManager(String crashDumpFolder, String currentSessionId, SentryEndpointConfig sentryEndpointConfig) {
        this.mCrashUploadURI = null;
        this.mCrashUploadURIWithSentryKey = null;
        this.mExceptionUploadURI = null;
        this.mCrashDumpFolder = null;
        this.mCurrentSessionId = null;
        this.mCrashDumpFolder = crashDumpFolder;
        this.mCurrentSessionId = currentSessionId;
        this.mCrashUploadURI = sentryEndpointConfig.url + "/api/" + sentryEndpointConfig.projectId + "/minidump/";
        StringBuilder sb = new StringBuilder();
        sb.append(this.mCrashUploadURI);
        sb.append("?sentry_key=");
        sb.append(sentryEndpointConfig.publicKey);
        this.mCrashUploadURIWithSentryKey = sb.toString();
        this.mExceptionUploadURI = sentryEndpointConfig.url + "/api/" + sentryEndpointConfig.projectId + "/store/?sentry_version=7&sentry_key=" + sentryEndpointConfig.publicKey;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleUncaughtException(Thread t, Throwable e) {
        Thread.setDefaultUncaughtExceptionHandler(this.mPreviousUncaughtExceptionHandler);
        //Log.e("MCPE", "In handleUncaughtException()");
        try {
            JSONObject jSONObject = new JSONObject(nativeNotifyUncaughtException());
            Object replaceAll = UUID.randomUUID().toString().toLowerCase().replaceAll(Constants.FILENAME_SEQUENCE_SEPARATOR, "");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Object format = simpleDateFormat.format(new Date());
            jSONObject.put("event_id", replaceAll);
            jSONObject.put("timestamp", format);
            jSONObject.put("logger", "na");
            jSONObject.put("platform", "java");
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("type", e.getClass().getName());
            jSONObject2.put("value", e.getMessage());
            JSONObject jSONObject3 = new JSONObject();
            JSONArray jSONArray = new JSONArray();
            StackTraceElement[] stackTrace = e.getStackTrace();
            for (int length = stackTrace.length - 1; length >= 0; length--) {
                StackTraceElement stackTraceElement = stackTrace[length];
                JSONObject jSONObject4 = new JSONObject();
                jSONObject4.put("filename", stackTraceElement.getFileName());
                jSONObject4.put("function", stackTraceElement.getMethodName());
                jSONObject4.put("module", stackTraceElement.getClassName());
                jSONObject4.put("in_app", stackTraceElement.getClassName().startsWith("com.mojang"));
                if (stackTraceElement.getLineNumber() > 0) {
                    jSONObject4.put("lineno", stackTraceElement.getLineNumber());
                }
                jSONArray.put(jSONObject4);
            }
            jSONObject3.put("frames", jSONArray);
            jSONObject2.put("stacktrace", jSONObject3);
            jSONObject.put("exception", jSONObject2);
            String str = this.mCrashDumpFolder + "/" + this.mCurrentSessionId + ".except";
            Log.d("MCPE", "CrashManager: Writing unhandled exception information to: " + str);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(str));
            outputStreamWriter.write(jSONObject.toString(4));
            outputStreamWriter.close();
        } catch (IOException e2) {
            //Log.e("MCPE", "IO exception: " + e2.toString());
            e2.printStackTrace();
        } catch (JSONException e3) {
            //Log.e("MCPE", "JSON exception: " + e3.toString());
            e3.printStackTrace();
        }
        this.mPreviousUncaughtExceptionHandler.uncaughtException(t, e);
    }

    private static native String nativeNotifyUncaughtException();

    private String uploadCrashFile(String filePath, String sessionID, String sentryParametersJSON) {
//        File file = new File(filePath);
//        String str = null;
//        while (true) {
//            Pair<HttpResponse, String> uploadDump = filePath.endsWith(".dmp") ? uploadDump(file, this.mCrashUploadURIWithSentryKey, sessionID, sentryParametersJSON) : uploadException(file);
//            if (uploadDump.first != null) {
//                int statusCode = ((HttpResponse) uploadDump.first).getStatusLine().getStatusCode();
//                if (statusCode == 200) {
//                    Log.i("MCPE", "Successfully uploaded dump file " + filePath);
//                    return str;
//                } else if (statusCode == 429) {
//                    Header firstHeader = ((HttpResponse) uploadDump.first).getFirstHeader("Retry-After");
//                    if (firstHeader != null) {
//                        int parseInt = Integer.parseInt(firstHeader.getValue());
//                        Log.w("MCPE", "Received Too Many Requests response, retrying after " + parseInt + "s");
//                        try {
//                            Thread.sleep(parseInt * 1000);
//                        } catch (InterruptedException unused) {
//                        }
//                    } else {
//                        Log.w("MCPE", "Received Too Many Requests response with no Retry-After header, so dropping event " + filePath);
//                        str = "TooManyRequestsNoRetryAfter";
//                    }
//                } else {
//                    //Log.e("MCPE", "Unrecognied HTTP response: \"" + ((HttpResponse) uploadDump.first).getStatusLine() + "\", dropping event " + filePath);
//                    StringBuilder sb = new StringBuilder();
//                    sb.append("HTTP: ");
//                    sb.append(((HttpResponse) uploadDump.first).getStatusLine().toString());
//                    str = sb.toString();
//                }
//            } else {
//                //Log.e("MCPE", "An error occurred uploading an event; dropping event " + filePath);
//                str = (String) uploadDump.second;
//            }
//        }
        return "";
    }

    private static Pair<HttpResponse, String> uploadDump(File dumpFile, final String crashUploadURI, final String sessionID, final String sentryParametersJSON) {
        Exception e;
        HttpResponse httpResponse = null;
        try {
            Log.i("MCPE", "CrashManager: uploading " + dumpFile.getPath());
            Log.d("MCPE", "CrashManager: sentry parameters: " + sentryParametersJSON);
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(crashUploadURI);
            MultipartEntity multipartEntity = new MultipartEntity();
            multipartEntity.addPart("upload_file_minidump", new FileBody(dumpFile));
            multipartEntity.addPart("sentry", new StringBody(sentryParametersJSON));
            httpPost.setEntity(multipartEntity);
            httpResponse = defaultHttpClient.execute(httpPost);
        } catch (Exception e2) {
            e = e2;
        }
        try {
            Log.d("MCPE", "CrashManager: Executed dump file upload with no exception: " + dumpFile.getPath());
        } catch (Exception e3) {
            e = e3;
            httpResponse = httpResponse;
            Log.w("MCPE", "CrashManager: Error uploading dump file: " + dumpFile.getPath());
            e.printStackTrace();
//            httpResponse = e.getMessage();
            return new Pair(httpResponse, httpResponse);
        }
        return new Pair(httpResponse, httpResponse);
    }

    private Pair<HttpResponse, String> uploadException(File fp) {
        String str;
        HttpResponse httpResponse = null;
        try {
            Log.i("MCPE", "CrashManager: reading exception file at " + fp.getPath());
            String str2 = "";
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(fp)));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                str2 = str2 + readLine + "\n";
            }
            Log.i("MCPE", "Sending exception by HTTP to " + this.mExceptionUploadURI);
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(this.mExceptionUploadURI);
            httpPost.setEntity(new StringEntity(str2));
            httpResponse = defaultHttpClient.execute(httpPost);
            str = null;
        } catch (Exception e) {
            Log.w("MCPE", "CrashManager: Error uploading exception: " + e.toString());
            e.printStackTrace();
            str = e.getMessage();
        }
        return new Pair<>(httpResponse, str);
    }

    public String getCrashUploadURI() {
        return this.mCrashUploadURI;
    }

    public String getExceptionUploadURI() {
        return this.mExceptionUploadURI;
    }

    public void installGlobalExceptionHandler() {
        this.mPreviousUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() { // from class: com.mojang.minecraftpe.CrashManager.1
            @Override // java.lang.Thread.UncaughtExceptionHandler
            public void uncaughtException(Thread t, Throwable e) {
                CrashManager.this.handleUncaughtException(t, e);
            }
        });
    }
}
