package com.google.android.vending.expansion.downloader.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;
import android.util.Log;

/* loaded from: classes7.dex */
public class DownloadsDB {
    private static final int CONTROL_IDX = 7;
    private static final int CURRENTBYTES_IDX = 4;
    private static final String DATABASE_NAME = "DownloadsDB";
    private static final int DATABASE_VERSION = 7;
    private static final String[] DC_PROJECTION = {DownloadColumns.FILENAME, DownloadColumns.URI, DownloadColumns.ETAG, DownloadColumns.TOTALBYTES, DownloadColumns.CURRENTBYTES, DownloadColumns.LASTMOD, DownloadColumns.STATUS, DownloadColumns.CONTROL, DownloadColumns.NUM_FAILED, DownloadColumns.RETRY_AFTER, DownloadColumns.REDIRECT_COUNT, DownloadColumns.INDEX};
    private static final int ETAG_IDX = 2;
    private static final int FILENAME_IDX = 0;
    private static final int INDEX_IDX = 11;
    private static final int LASTMOD_IDX = 5;
    public static final String LOG_TAG = "com.google.android.vending.expansion.downloader.impl.DownloadsDB";
    private static final int NUM_FAILED_IDX = 8;
    private static final int REDIRECT_COUNT_IDX = 10;
    private static final int RETRY_AFTER_IDX = 9;
    private static final int STATUS_IDX = 6;
    private static final int TOTALBYTES_IDX = 3;
    private static final int URI_IDX = 1;
    private static DownloadsDB mDownloadsDB;
    int mFlags;
    SQLiteStatement mGetDownloadByIndex;
    final SQLiteOpenHelper mHelper;
    long mMetadataRowID;
    int mStatus;
    SQLiteStatement mUpdateCurrentBytes;
    int mVersionCode;

    /* loaded from: classes7.dex */
    public static class DownloadColumns implements BaseColumns {
        public static final String TABLE_NAME = "DownloadColumns";
        public static final String _ID = "DownloadColumns._id";
        public static final String INDEX = "FILEIDX";
        public static final String URI = "URI";
        public static final String FILENAME = "FN";
        public static final String ETAG = "ETAG";
        public static final String TOTALBYTES = "TOTALBYTES";
        public static final String CURRENTBYTES = "CURRENTBYTES";
        public static final String LASTMOD = "LASTMOD";
        public static final String STATUS = "STATUS";
        public static final String CONTROL = "CONTROL";
        public static final String NUM_FAILED = "FAILCOUNT";
        public static final String RETRY_AFTER = "RETRYAFTER";
        public static final String REDIRECT_COUNT = "REDIRECTCOUNT";
        public static final String[][] SCHEMA = {new String[]{"_id", "INTEGER PRIMARY KEY"}, new String[]{INDEX, "INTEGER UNIQUE"}, new String[]{URI, "TEXT"}, new String[]{FILENAME, "TEXT UNIQUE"}, new String[]{ETAG, "TEXT"}, new String[]{TOTALBYTES, "INTEGER"}, new String[]{CURRENTBYTES, "INTEGER"}, new String[]{LASTMOD, "INTEGER"}, new String[]{STATUS, "INTEGER"}, new String[]{CONTROL, "INTEGER"}, new String[]{NUM_FAILED, "INTEGER"}, new String[]{RETRY_AFTER, "INTEGER"}, new String[]{REDIRECT_COUNT, "INTEGER"}};
    }

    /* loaded from: classes7.dex */
    protected static class DownloadsContentDBHelper extends SQLiteOpenHelper {
        private static final String[][][] sSchemas = {DownloadColumns.SCHEMA, MetadataColumns.SCHEMA};
        private static final String[] sTables = {DownloadColumns.TABLE_NAME, MetadataColumns.TABLE_NAME};

        DownloadsContentDBHelper(Context paramContext) {
            super(paramContext, DownloadsDB.DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, 7);
        }

        private String createTableQueryFromArray(String paramString, String[][] paramArrayOfString) {
            StringBuilder sb = new StringBuilder();
            sb.append("CREATE TABLE ");
            sb.append(paramString);
            sb.append(" (");
            for (String[] strArr : paramArrayOfString) {
                sb.append(' ');
                sb.append(strArr[0]);
                sb.append(' ');
                sb.append(strArr[1]);
                sb.append(',');
            }
            sb.setLength(sb.length() - 1);
            sb.append(");");
            return sb.toString();
        }

        private void dropTables(SQLiteDatabase paramSQLiteDatabase) {
            String[] strArr;
            for (String str : sTables) {
                try {
                    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS " + str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase paramSQLiteDatabase) {
            int length = sSchemas.length;
            for (int i = 0; i < length; i++) {
                try {
                    paramSQLiteDatabase.execSQL(createTableQueryFromArray(sTables[i], sSchemas[i]));
                } catch (Exception e) {
                    while (true) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2) {
            String name = DownloadsContentDBHelper.class.getName();
            Log.w(name, "Upgrading database from version " + paramInt1 + " to " + paramInt2 + ", which will destroy all old data");
            dropTables(paramSQLiteDatabase);
            onCreate(paramSQLiteDatabase);
        }
    }

    /* loaded from: classes7.dex */
    public static class MetadataColumns implements BaseColumns {
        public static final String APKVERSION = "APKVERSION";
        public static final String DOWNLOAD_STATUS = "DOWNLOADSTATUS";
        public static final String FLAGS = "DOWNLOADFLAGS";
        public static final String[][] SCHEMA = {new String[]{"_id", "INTEGER PRIMARY KEY"}, new String[]{APKVERSION, "INTEGER"}, new String[]{DOWNLOAD_STATUS, "INTEGER"}, new String[]{FLAGS, "INTEGER"}};
        public static final String TABLE_NAME = "MetadataColumns";
        public static final String _ID = "MetadataColumns._id";
    }

    private DownloadsDB(Context paramContext) {
        this.mMetadataRowID = -1L;
        this.mVersionCode = -1;
        this.mStatus = -1;
        DownloadsContentDBHelper downloadsContentDBHelper = new DownloadsContentDBHelper(paramContext);
        this.mHelper = downloadsContentDBHelper;
        Cursor rawQuery = downloadsContentDBHelper.getReadableDatabase().rawQuery("SELECT APKVERSION,_id,DOWNLOADSTATUS,DOWNLOADFLAGS FROM MetadataColumns LIMIT 1", null);
        if (rawQuery != null && rawQuery.moveToFirst()) {
            this.mVersionCode = rawQuery.getInt(0);
            this.mMetadataRowID = rawQuery.getLong(1);
            this.mStatus = rawQuery.getInt(2);
            this.mFlags = rawQuery.getInt(3);
            rawQuery.close();
        }
        mDownloadsDB = this;
    }

    public static synchronized DownloadsDB getDB(Context paramContext) {
        synchronized (DownloadsDB.class) {
            DownloadsDB downloadsDB = mDownloadsDB;
            if (downloadsDB != null) {
                return downloadsDB;
            }
            return new DownloadsDB(paramContext);
        }
    }

    private SQLiteStatement getDownloadByIndexStatement() {
        if (this.mGetDownloadByIndex == null) {
            this.mGetDownloadByIndex = this.mHelper.getReadableDatabase().compileStatement("SELECT _id FROM DownloadColumns WHERE FILEIDX = ?");
        }
        return this.mGetDownloadByIndex;
    }

    private SQLiteStatement getUpdateCurrentBytesStatement() {
        if (this.mUpdateCurrentBytes == null) {
            this.mUpdateCurrentBytes = this.mHelper.getReadableDatabase().compileStatement("UPDATE DownloadColumns SET CURRENTBYTES = ? WHERE FILEIDX = ?");
        }
        return this.mUpdateCurrentBytes;
    }

    public void close() {
        this.mHelper.close();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public DownloadInfo getDownloadInfoByFileName(String fileName) {
        Cursor cursor;
        Throwable th;
        try {
            cursor = this.mHelper.getReadableDatabase().query(DownloadColumns.TABLE_NAME, DC_PROJECTION, "FN = ?", new String[]{fileName}, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        DownloadInfo downloadInfoFromCursor = getDownloadInfoFromCursor(cursor);
                        if (cursor != null) {
                            cursor.close();
                        }
                        return downloadInfoFromCursor;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw th;
                }
            }
            if (cursor != null) {
                cursor.close();
            }
            return null;
        } catch (Throwable th3) {
            th = th3;
            cursor = null;
        }
        return null;
    }

    public DownloadInfo getDownloadInfoFromCursor(Cursor cur) {
        DownloadInfo downloadInfo = new DownloadInfo(cur.getInt(11), cur.getString(0), getClass().getPackage().getName());
        setDownloadInfoFromCursor(downloadInfo, cur);
        return downloadInfo;
    }

    public DownloadInfo[] getDownloads() {
        Cursor cursor;
        Throwable th;
        try {
            cursor = this.mHelper.getReadableDatabase().query(DownloadColumns.TABLE_NAME, DC_PROJECTION, null, null, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        DownloadInfo[] downloadInfoArr = new DownloadInfo[cursor.getCount()];
                        int i = 0;
                        while (true) {
                            i++;
                            downloadInfoArr[i] = getDownloadInfoFromCursor(cursor);
                            if (!cursor.moveToNext()) {
                                break;
                            }
                        }
                        if (cursor != null) {
                            cursor.close();
                        }
                        return downloadInfoArr;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw th;
                }
            }
            if (cursor != null) {
                cursor.close();
            }
            return null;
        } catch (Throwable th3) {
            th = th3;
            cursor = null;
        }
        return null;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public long getIDByIndex(int index) {
        SQLiteStatement downloadByIndexStatement = getDownloadByIndexStatement();
        downloadByIndexStatement.clearBindings();
        downloadByIndexStatement.bindLong(1, index);
        try {
            return downloadByIndexStatement.simpleQueryForLong();
        } catch (SQLiteDoneException unused) {
            return -1L;
        }
    }

    public long getIDForDownloadInfo(final DownloadInfo di) {
        return getIDByIndex(di.mIndex);
    }

    public int getLastCheckedVersionCode() {
        return this.mVersionCode;
    }

    public boolean isDownloadRequired() {
        Cursor rawQuery = this.mHelper.getReadableDatabase().rawQuery("SELECT Count(*) FROM DownloadColumns WHERE STATUS <> 0", null);
        boolean z = true;
        if (rawQuery != null) {
            try {
                if (rawQuery.moveToFirst()) {
                    if (rawQuery.getInt(0) != 0) {
                        z = false;
                    }
                    return z;
                }
            } finally {
                if (rawQuery != null) {
                    rawQuery.close();
                }
            }
        }
        if (rawQuery != null) {
            rawQuery.close();
        }
        return true;
    }

    public void setDownloadInfoFromCursor(DownloadInfo di, Cursor cur) {
        di.mUri = cur.getString(1);
        di.mETag = cur.getString(2);
        di.mTotalBytes = cur.getLong(3);
        di.mCurrentBytes = cur.getLong(4);
        di.mLastMod = cur.getLong(5);
        di.mStatus = cur.getInt(6);
        di.mControl = cur.getInt(7);
        di.mNumFailed = cur.getInt(8);
        di.mRetryAfter = cur.getInt(9);
        di.mRedirectCount = cur.getInt(10);
    }

    public boolean updateDownload(DownloadInfo di) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DownloadColumns.INDEX, Integer.valueOf(di.mIndex));
        contentValues.put(DownloadColumns.FILENAME, di.mFileName);
        contentValues.put(DownloadColumns.URI, di.mUri);
        contentValues.put(DownloadColumns.ETAG, di.mETag);
        contentValues.put(DownloadColumns.TOTALBYTES, Long.valueOf(di.mTotalBytes));
        contentValues.put(DownloadColumns.CURRENTBYTES, Long.valueOf(di.mCurrentBytes));
        contentValues.put(DownloadColumns.LASTMOD, Long.valueOf(di.mLastMod));
        contentValues.put(DownloadColumns.STATUS, Integer.valueOf(di.mStatus));
        contentValues.put(DownloadColumns.CONTROL, Integer.valueOf(di.mControl));
        contentValues.put(DownloadColumns.NUM_FAILED, Integer.valueOf(di.mNumFailed));
        contentValues.put(DownloadColumns.RETRY_AFTER, Integer.valueOf(di.mRetryAfter));
        contentValues.put(DownloadColumns.REDIRECT_COUNT, Integer.valueOf(di.mRedirectCount));
        return updateDownload(di, contentValues);
    }

    public boolean updateDownload(DownloadInfo di, ContentValues cv) {
        SQLiteDatabase writableDatabase = null;
        long iDForDownloadInfo = di == null ? -1L : getIDForDownloadInfo(di);
        try {
            writableDatabase = this.mHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (iDForDownloadInfo == -1) {
            return -1 != writableDatabase.insert(DownloadColumns.TABLE_NAME, DownloadColumns.URI, cv);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("DownloadColumns._id = ");
        sb.append(iDForDownloadInfo);
        return 1 != writableDatabase.update(DownloadColumns.TABLE_NAME, cv, sb.toString(), null) ? false : false;
    }

    public void updateDownloadCurrentBytes(final DownloadInfo di) {
        SQLiteStatement updateCurrentBytesStatement = getUpdateCurrentBytesStatement();
        updateCurrentBytesStatement.clearBindings();
        updateCurrentBytesStatement.bindLong(1, di.mCurrentBytes);
        updateCurrentBytesStatement.bindLong(2, di.mIndex);
        updateCurrentBytesStatement.execute();
    }

    public boolean updateFlags(int flags) {
        if (this.mFlags == flags) {
            return true;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(MetadataColumns.FLAGS, Integer.valueOf(flags));
        if (!updateMetadata(contentValues)) {
            return false;
        }
        this.mFlags = flags;
        return true;
    }

    public boolean updateFromDb(DownloadInfo di) {
        Cursor cursor = null;
        try {
            cursor = this.mHelper.getReadableDatabase().query(DownloadColumns.TABLE_NAME, DC_PROJECTION, "FN= ?", new String[]{di.mFileName}, null, null, null);
            if (cursor == null || !cursor.moveToFirst()) {
                if (cursor != null) {
                    cursor.close();
                }
                return false;
            }
            setDownloadInfoFromCursor(di, cursor);
            return true;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public boolean updateMetadata(int apkVersion, int downloadStatus) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MetadataColumns.APKVERSION, Integer.valueOf(apkVersion));
        contentValues.put(MetadataColumns.DOWNLOAD_STATUS, Integer.valueOf(downloadStatus));
        if (!updateMetadata(contentValues)) {
            return false;
        }
        this.mVersionCode = apkVersion;
        this.mStatus = downloadStatus;
        return true;
    }

    public boolean updateMetadata(ContentValues cv) {
        SQLiteDatabase writableDatabase = this.mHelper.getWritableDatabase();
        if (-1 == this.mMetadataRowID) {
            long insert = writableDatabase.insert(MetadataColumns.TABLE_NAME, MetadataColumns.APKVERSION, cv);
            if (-1 == insert) {
                return false;
            }
            this.mMetadataRowID = insert;
            return true;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("_id = ");
        sb.append(this.mMetadataRowID);
        return writableDatabase.update(MetadataColumns.TABLE_NAME, cv, sb.toString(), null) != 0;
    }

    public boolean updateStatus(int status) {
        if (this.mStatus == status) {
            return true;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(MetadataColumns.DOWNLOAD_STATUS, Integer.valueOf(status));
        if (!updateMetadata(contentValues)) {
            return false;
        }
        this.mStatus = status;
        return true;
    }
}
