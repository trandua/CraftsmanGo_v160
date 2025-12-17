package com.microsoft.xbox.toolkit;

/* loaded from: classes3.dex */
public enum AsyncActionStatus {
    SUCCESS,
    FAIL,
    NO_CHANGE,
    NO_OP_SUCCESS,
    NO_OP_FAIL;
    
    private static AsyncActionStatus[][] MERGE_MATRIX = {new AsyncActionStatus[]{null, null, null, null, null}, new AsyncActionStatus[]{null, null, null, null, null}, new AsyncActionStatus[]{null, null, null, null, null}, new AsyncActionStatus[]{null, null, null, null, null}, new AsyncActionStatus[]{null, null, null, null, null}};

    public static boolean getIsFail(AsyncActionStatus asyncActionStatus) {
        return asyncActionStatus == FAIL || asyncActionStatus == NO_OP_FAIL;
    }

    public static AsyncActionStatus merge(AsyncActionStatus asyncActionStatus, AsyncActionStatus... asyncActionStatusArr) {
        for (AsyncActionStatus asyncActionStatus2 : asyncActionStatusArr) {
            asyncActionStatus = MERGE_MATRIX[asyncActionStatus.ordinal()][asyncActionStatus2.ordinal()];
        }
        return asyncActionStatus;
    }
}
