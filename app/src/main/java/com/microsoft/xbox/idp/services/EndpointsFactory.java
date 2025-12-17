package com.microsoft.xbox.idp.services;

import com.microsoft.xbox.idp.services.Endpoints;

/* loaded from: classes3.dex */
public class EndpointsFactory {

    /* loaded from: classes3.dex */
    static class C53901 {
        static final int[] $SwitchMap$com$microsoft$xbox$idp$services$Endpoints$Type;

        C53901() {
        }

        static {
            int[] iArr = new int[Endpoints.Type.values().length];
            $SwitchMap$com$microsoft$xbox$idp$services$Endpoints$Type = iArr;
            try {
                iArr[Endpoints.Type.PROD.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$services$Endpoints$Type[Endpoints.Type.DNET.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    public static Endpoints get() {
        int i = C53901.$SwitchMap$com$microsoft$xbox$idp$services$Endpoints$Type[Config.endpointType.ordinal()];
        if (i == 1) {
            return new EndpointsProd();
        }
        if (i != 2) {
            return null;
        }
        return new EndpointsDnet();
    }
}
