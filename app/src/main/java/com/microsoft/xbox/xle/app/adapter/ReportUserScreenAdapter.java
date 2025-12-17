package com.microsoft.xbox.xle.app.adapter;

import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
//import com.crafting.minecrafting.lokicraft.R;
import com.craftsman.go.R;
import com.microsoft.xbox.telemetry.helpers.UTCReportUser;
import com.microsoft.xbox.telemetry.helpers.UTCTelemetry;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.xle.viewmodel.AdapterBase;
import com.microsoft.xbox.xle.viewmodel.ReportUserScreenViewModel;
import com.microsoft.xboxtcui.XboxTcuiSdk;

/* loaded from: classes3.dex */
public class ReportUserScreenAdapter extends AdapterBase {
    private XLEButton cancelButton;
    public EditText optionalText;
    private Spinner reasonSpinner;
    private ArrayAdapter<String> reasonSpinnerAdapter;
    private XLEButton submitButton;
    private CustomTypefaceTextView titleTextView;
    public ReportUserScreenViewModel viewModel;

    public ReportUserScreenAdapter(ReportUserScreenViewModel reportUserScreenViewModel) {
        super(reportUserScreenViewModel);
        this.cancelButton = (XLEButton) findViewById(R.id.report_user_cancel);
        this.optionalText = (EditText) findViewById(R.id.report_user_text);
        this.reasonSpinner = (Spinner) findViewById(R.id.report_user_reason);
        this.submitButton = (XLEButton) findViewById(R.id.report_user_submit);
        CustomTypefaceTextView customTypefaceTextView = (CustomTypefaceTextView) findViewById(R.id.report_user_title);
        this.titleTextView = customTypefaceTextView;
        this.viewModel = reportUserScreenViewModel;
        XLEAssert.assertNotNull(customTypefaceTextView);
        XLEAssert.assertNotNull(this.reasonSpinner);
        XLEAssert.assertNotNull(this.optionalText);
        XLEAssert.assertNotNull(this.cancelButton);
        XLEAssert.assertNotNull(this.submitButton);
    }

    @Override // com.microsoft.xbox.xle.viewmodel.AdapterBase
    public void onStart() {
        super.onStart();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(XboxTcuiSdk.getActivity(), (int) R.layout.report_spinner_item, this.viewModel.getReasonTitles());
        this.reasonSpinnerAdapter = arrayAdapter;
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item_dropdown);
        this.reasonSpinner.setAdapter((SpinnerAdapter) this.reasonSpinnerAdapter);
        this.reasonSpinner.setPopupBackgroundDrawable(new ColorDrawable(this.viewModel.getPreferredColor()));
        this.reasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.microsoft.xbox.xle.app.adapter.ReportUserScreenAdapter.1
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                ReportUserScreenAdapter.this.viewModel.setReason(i);
            }
        });
        this.cancelButton.setOnClickListener(new View.OnClickListener() { // from class: com.microsoft.xbox.xle.app.adapter.ReportUserScreenAdapter.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ReportUserScreenAdapter.this.viewModel.onBackButtonPressed();
            }
        });
        this.submitButton.setOnClickListener(new View.OnClickListener() { // from class: com.microsoft.xbox.xle.app.adapter.ReportUserScreenAdapter.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                UTCReportUser.trackReportDialogOK(ReportUserScreenAdapter.this.viewModel.getReason() == null ? UTCTelemetry.UNKNOWNPAGE : ReportUserScreenAdapter.this.viewModel.getReason().toString());
                ReportUserScreenAdapter.this.viewModel.submitReport(ReportUserScreenAdapter.this.optionalText.getText().toString());
            }
        });
    }

    @Override // com.microsoft.xbox.xle.viewmodel.AdapterBase
    public void updateViewOverride() {
        CustomTypefaceTextView customTypefaceTextView = this.titleTextView;
        if (customTypefaceTextView != null) {
            customTypefaceTextView.setText(this.viewModel.getTitle());
        }
        XLEButton xLEButton = this.submitButton;
        if (xLEButton != null) {
            xLEButton.setEnabled(this.viewModel.validReasonSelected());
        }
    }
}
