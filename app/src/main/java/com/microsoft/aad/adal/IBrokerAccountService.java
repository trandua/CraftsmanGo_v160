package com.microsoft.aad.adal;

import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.Map;

/* loaded from: classes3.dex */
public interface IBrokerAccountService extends IInterface {
    Bundle acquireTokenSilently(Map map) throws RemoteException;

    Bundle getBrokerUsers() throws RemoteException;

    Intent getIntentForInteractiveRequest() throws RemoteException;

    void removeAccounts() throws RemoteException;

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IBrokerAccountService {
        private static final String DESCRIPTOR = "com.microsoft.aad.adal.IBrokerAccountService";
        static final int TRANSACTION_acquireTokenSilently = 2;
        static final int TRANSACTION_getBrokerUsers = 1;
        static final int TRANSACTION_getIntentForInteractiveRequest = 3;
        static final int TRANSACTION_removeAccounts = 4;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IBrokerAccountService {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // com.microsoft.aad.adal.IBrokerAccountService
            public Bundle acquireTokenSilently(Map map) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeMap(map);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.microsoft.aad.adal.IBrokerAccountService
            public Bundle getBrokerUsers() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.microsoft.aad.adal.IBrokerAccountService
            public Intent getIntentForInteractiveRequest() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (Intent) Intent.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.microsoft.aad.adal.IBrokerAccountService
            public void removeAccounts() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IBrokerAccountService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IBrokerAccountService)) ? new Proxy(iBinder) : (IBrokerAccountService) queryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1) {
                parcel.enforceInterface(DESCRIPTOR);
                Bundle brokerUsers = getBrokerUsers();
                parcel2.writeNoException();
                if (brokerUsers != null) {
                    parcel2.writeInt(1);
                    brokerUsers.writeToParcel(parcel2, 1);
                } else {
                    parcel2.writeInt(0);
                }
                return true;
            } else if (i == 2) {
                parcel.enforceInterface(DESCRIPTOR);
                Bundle acquireTokenSilently = acquireTokenSilently(parcel.readHashMap(getClass().getClassLoader()));
                parcel2.writeNoException();
                if (acquireTokenSilently != null) {
                    parcel2.writeInt(1);
                    acquireTokenSilently.writeToParcel(parcel2, 1);
                } else {
                    parcel2.writeInt(0);
                }
                return true;
            } else if (i == 3) {
                parcel.enforceInterface(DESCRIPTOR);
                Intent intentForInteractiveRequest = getIntentForInteractiveRequest();
                parcel2.writeNoException();
                if (intentForInteractiveRequest != null) {
                    parcel2.writeInt(1);
                    intentForInteractiveRequest.writeToParcel(parcel2, 1);
                } else {
                    parcel2.writeInt(0);
                }
                return true;
            } else if (i == 4) {
                parcel.enforceInterface(DESCRIPTOR);
                removeAccounts();
                parcel2.writeNoException();
                return true;
            } else if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
        }
    }
}
