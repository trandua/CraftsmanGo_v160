package com.amazon.device.iap.model;

import com.amazon.device.iap.internal.model.ReceiptBuilder;
//import com.amazon.device.iap.internal.util.d;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class Receipt {
    private static final String CANCEL_DATE = "endDate";
    private static final Date DATE_CANCELED = new Date(1);
    private static final String PRODUCT_TYPE = "itemType";
    private static final String PURCHASE_DATE = "purchaseDate";
    private static final String RECEIPT_ID = "receiptId";
    private static final String SKU = "sku";
    private final Date cancelDate;
    private final ProductType productType;
    private final Date purchaseDate;
    private final String receiptId;
    private final String sku;

    public int hashCode() {
        int hashCode;
        int hashCode2;
        int hashCode3;
        int hashCode4;
        int i = 0;
        if (this.cancelDate == null) {
            hashCode = 0;
        } else {
            hashCode = this.cancelDate.hashCode();
        }
        int i2 = (hashCode + 31) * 31;
        if (this.productType == null) {
            hashCode2 = 0;
        } else {
            hashCode2 = this.productType.hashCode();
        }
        int i3 = (hashCode2 + i2) * 31;
        if (this.purchaseDate == null) {
            hashCode3 = 0;
        } else {
            hashCode3 = this.purchaseDate.hashCode();
        }
        int i4 = (hashCode3 + i3) * 31;
        if (this.receiptId == null) {
            hashCode4 = 0;
        } else {
            hashCode4 = this.receiptId.hashCode();
        }
        int i5 = (hashCode4 + i4) * 31;
        if (this.sku != null) {
            i = this.sku.hashCode();
        }
        return i5 + i;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && getClass() == obj.getClass()) {
            Receipt receipt = (Receipt) obj;
            if (this.cancelDate == null) {
                if (receipt.cancelDate != null) {
                    return false;
                }
            } else if (!this.cancelDate.equals(receipt.cancelDate)) {
                return false;
            }
            if (this.productType != receipt.productType) {
                return false;
            }
            if (this.purchaseDate == null) {
                if (receipt.purchaseDate != null) {
                    return false;
                }
            } else if (!this.purchaseDate.equals(receipt.purchaseDate)) {
                return false;
            }
            if (this.receiptId == null) {
                if (receipt.receiptId != null) {
                    return false;
                }
            } else if (!this.receiptId.equals(receipt.receiptId)) {
                return false;
            }
            return this.sku == null ? receipt.sku == null : this.sku.equals(receipt.sku);
        }
        return false;
    }

    public Receipt(ReceiptBuilder receiptBuilder) {
//        d.a((Object) receiptBuilder.getSku(), SKU);
//        d.a(receiptBuilder.getProductType(), "productType");
//        if (ProductType.SUBSCRIPTION == receiptBuilder.getProductType()) {
//            d.a(receiptBuilder.getPurchaseDate(), PURCHASE_DATE);
//        }
        this.receiptId = receiptBuilder.getReceiptId();
        this.sku = receiptBuilder.getSku();
        this.productType = receiptBuilder.getProductType();
        this.purchaseDate = receiptBuilder.getPurchaseDate();
        this.cancelDate = receiptBuilder.getCancelDate();
    }

    public String getReceiptId() {
        return this.receiptId;
    }

    public String getSku() {
        return this.sku;
    }

    public ProductType getProductType() {
        return this.productType;
    }

    public Date getPurchaseDate() {
        return this.purchaseDate;
    }

    public Date getCancelDate() {
        return this.cancelDate;
    }

    public JSONObject toJSON() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(RECEIPT_ID, this.receiptId);
            jSONObject.put(SKU, this.sku);
            jSONObject.put(PRODUCT_TYPE, this.productType);
            jSONObject.put(PURCHASE_DATE, this.purchaseDate);
            jSONObject.put(CANCEL_DATE, this.cancelDate);
        } catch (JSONException e) {
        }
        return jSONObject;
    }

    public String toString() {
        try {
            return toJSON().toString(4);
        } catch (JSONException e) {
            return null;
        }
    }

    public boolean isCanceled() {
        return this.cancelDate != null;
    }
}