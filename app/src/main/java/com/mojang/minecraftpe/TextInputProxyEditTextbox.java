package com.mojang.minecraftpe;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;
import java.util.ArrayList;
import org.spongycastle.crypto.tls.CipherSuite;

/* loaded from: classes3.dex */
public class TextInputProxyEditTextbox extends androidx.appcompat.widget.AppCompatEditText {
    public MCPEKeyWatcher _mcpeKeyWatcher;
    public int allowedLength;
    private String mLastSentText;

    /* loaded from: classes3.dex */
    public interface MCPEKeyWatcher {
        boolean onBackKeyPressed();

        void onDeleteKeyPressed();
    }

    public TextInputProxyEditTextbox(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this._mcpeKeyWatcher = null;
        this.allowedLength = CipherSuite.TLS_DH_RSA_WITH_AES_128_GCM_SHA256;
    }

    public TextInputProxyEditTextbox(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this._mcpeKeyWatcher = null;
        this.allowedLength = CipherSuite.TLS_DH_RSA_WITH_AES_128_GCM_SHA256;
    }

    public TextInputProxyEditTextbox(Context context) {
        super(context);
        this._mcpeKeyWatcher = null;
    }

    public void updateFilters(int i, boolean z) {
        this.allowedLength = i;
        ArrayList arrayList = new ArrayList();
        if (i != 0) {
            arrayList.add(new InputFilter.LengthFilter(this.allowedLength));
        }
        if (z) {
            arrayList.add(createSingleLineFilter());
        }
        arrayList.add(createUnicodeFilter());
        setFilters((InputFilter[]) arrayList.toArray(new InputFilter[arrayList.size()]));
    }

    public boolean shouldSendText() {
        return this.mLastSentText == null || !getText().toString().equals(this.mLastSentText);
    }

    public void setTextFromGame(String str) {
        this.mLastSentText = new String(str);
        setText(str);
    }

    public void updateLastSentText() {
        this.mLastSentText = new String(getText().toString());
    }

    @Override // android.widget.TextView, android.view.View
    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
        return new MCPEInputConnection(super.onCreateInputConnection(editorInfo), true, this);
    }

    @Override // android.widget.TextView, android.view.View
    public boolean onKeyPreIme(int i, KeyEvent keyEvent) {
        if (i != 4 || keyEvent.getAction() != 1) {
            return super.onKeyPreIme(i, keyEvent);
        }
        MCPEKeyWatcher mCPEKeyWatcher = this._mcpeKeyWatcher;
        if (mCPEKeyWatcher != null) {
            return mCPEKeyWatcher.onBackKeyPressed();
        }
        return false;
    }

    public void setOnMCPEKeyWatcher(MCPEKeyWatcher mCPEKeyWatcher) {
        this._mcpeKeyWatcher = mCPEKeyWatcher;
    }

    private InputFilter createSingleLineFilter() {
        return new InputFilter() { // from class: com.mojang.minecraftpe.TextInputProxyEditTextbox.1
            @Override // android.text.InputFilter
            public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
                while (i < i2) {
                    if (charSequence.charAt(i) == '\n') {
                        return spanned.subSequence(i3, i4);
                    }
                    i++;
                }
                return null;
            }
        };
    }

    private InputFilter createUnicodeFilter() {
        return new InputFilter() { // from class: com.mojang.minecraftpe.TextInputProxyEditTextbox.2
            @Override // android.text.InputFilter
            public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
                StringBuilder sb = null;
                for (int i5 = i; i5 < i2; i5++) {
                    if (charSequence.charAt(i5) == 12288) {
                        if (sb == null) {
                            sb = new StringBuilder(charSequence);
                        }
                        sb.setCharAt(i5, ' ');
                    }
                }
                if (sb != null) {
                    return sb.subSequence(i, i2);
                }
                return null;
            }
        };
    }

    /* loaded from: classes3.dex */
    private class MCPEInputConnection extends InputConnectionWrapper {
        TextInputProxyEditTextbox textbox;

        public MCPEInputConnection(InputConnection inputConnection, boolean z, TextInputProxyEditTextbox textInputProxyEditTextbox) {
            super(inputConnection, z);
            this.textbox = textInputProxyEditTextbox;
        }

        @Override // android.view.inputmethod.InputConnectionWrapper, android.view.inputmethod.InputConnection
        public boolean sendKeyEvent(KeyEvent keyEvent) {
            if (this.textbox.getText().length() != 0 || keyEvent.getAction() != 0 || keyEvent.getKeyCode() != 67) {
                return super.sendKeyEvent(keyEvent);
            }
            if (TextInputProxyEditTextbox.this._mcpeKeyWatcher != null) {
                TextInputProxyEditTextbox.this._mcpeKeyWatcher.onDeleteKeyPressed();
                return false;
            }
            return false;
        }
    }
}
