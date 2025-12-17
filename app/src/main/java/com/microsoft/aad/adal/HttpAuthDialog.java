package com.microsoft.aad.adal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.craftsman.go.R;
//import com.crafting.minecrafting.lokicraft.R;

/* loaded from: classes3.dex */
class HttpAuthDialog {
    public CancelListener mCancelListener;
    private final Context mContext;
    public AlertDialog mDialog = null;
    public final String mHost;
    public OkListener mOkListener;
    public EditText mPasswordView;
    public final String mRealm;
    public EditText mUsernameView;

    /* loaded from: classes3.dex */
    public interface CancelListener {
        void onCancel();
    }

    /* loaded from: classes3.dex */
    public interface OkListener {
        void onOk(String str, String str2, String str3, String str4);
    }

    public HttpAuthDialog(Context context, String str, String str2) {
        this.mContext = context;
        this.mHost = str;
        this.mRealm = str2;
        createDialog();
    }

    private void createDialog() {
        View inflate = LayoutInflater.from(this.mContext).inflate(R.layout.http_auth_dialog, (ViewGroup) null);
        this.mUsernameView = (EditText) inflate.findViewById(R.id.editUserName);
        EditText editText = (EditText) inflate.findViewById(R.id.editPassword);
        this.mPasswordView = editText;
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: com.microsoft.aad.adal.HttpAuthDialog.1
            @Override // android.widget.TextView.OnEditorActionListener
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 6) {
                    return false;
                }
                HttpAuthDialog.this.mDialog.getButton(-1).performClick();
                return true;
            }
        });
        this.mDialog = new AlertDialog.Builder(this.mContext).setTitle(this.mContext.getText(R.string.http_auth_dialog_title).toString()).setView(inflate).setPositiveButton(R.string.http_auth_dialog_login, new DialogInterface.OnClickListener() { // from class: com.microsoft.aad.adal.HttpAuthDialog.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (HttpAuthDialog.this.mOkListener != null) {
                    HttpAuthDialog.this.mOkListener.onOk(HttpAuthDialog.this.mHost, HttpAuthDialog.this.mRealm, HttpAuthDialog.this.mUsernameView.getText().toString(), HttpAuthDialog.this.mPasswordView.getText().toString());
                }
            }
        }).setNegativeButton(R.string.http_auth_dialog_cancel, new DialogInterface.OnClickListener() { // from class: com.microsoft.aad.adal.HttpAuthDialog.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (HttpAuthDialog.this.mCancelListener != null) {
                    HttpAuthDialog.this.mCancelListener.onCancel();
                }
            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.microsoft.aad.adal.HttpAuthDialog.2
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialogInterface) {
                if (HttpAuthDialog.this.mCancelListener != null) {
                    HttpAuthDialog.this.mCancelListener.onCancel();
                }
            }
        }).create();
    }

    public void setCancelListener(CancelListener cancelListener) {
        this.mCancelListener = cancelListener;
    }

    public void setOkListener(OkListener okListener) {
        this.mOkListener = okListener;
    }

    public void show() {
        this.mDialog.show();
        this.mUsernameView.requestFocus();
    }
}
