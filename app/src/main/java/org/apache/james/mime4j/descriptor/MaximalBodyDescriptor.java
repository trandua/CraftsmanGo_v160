package org.apache.james.mime4j.descriptor;

import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.field.datetime.DateTime;
import org.apache.james.mime4j.field.datetime.parser.DateTimeParser;
import org.apache.james.mime4j.field.datetime.parser.ParseException;
import org.apache.james.mime4j.field.language.parser.ContentLanguageParser;
import org.apache.james.mime4j.field.mimeversion.parser.MimeVersionParser;
import org.apache.james.mime4j.field.structured.parser.StructuredFieldParser;
import org.apache.james.mime4j.parser.Field;
import org.apache.james.mime4j.util.MimeUtil;

/* loaded from: classes.dex */
public class MaximalBodyDescriptor extends DefaultBodyDescriptor {
    private static final int DEFAULT_MAJOR_VERSION = 1;
    private static final int DEFAULT_MINOR_VERSION = 0;
    private String contentDescription;
    private DateTime contentDispositionCreationDate;
    private MimeException contentDispositionCreationDateParseException;
    private DateTime contentDispositionModificationDate;
    private MimeException contentDispositionModificationDateParseException;
    private Map<String, String> contentDispositionParameters;
    private DateTime contentDispositionReadDate;
    private MimeException contentDispositionReadDateParseException;
    private long contentDispositionSize;
    private MimeException contentDispositionSizeParseException;
    private String contentDispositionType;
    private String contentId;
    private List<String> contentLanguage;
    private MimeException contentLanguageParseException;
    private String contentLocation;
    private MimeException contentLocationParseException;
    private String contentMD5Raw;
    private boolean isContentDescriptionSet;
    private boolean isContentDispositionSet;
    private boolean isContentIdSet;
    private boolean isContentLanguageSet;
    private boolean isContentLocationSet;
    private boolean isContentMD5Set;
    private boolean isMimeVersionSet;
    private int mimeMajorVersion;
    private int mimeMinorVersion;
    private MimeException mimeVersionException;

    protected MaximalBodyDescriptor() {
        this(null);
    }

    public MaximalBodyDescriptor(BodyDescriptor bodyDescriptor) {
        super(bodyDescriptor);
        this.isMimeVersionSet = false;
        this.mimeMajorVersion = 1;
        this.mimeMinorVersion = 0;
        this.contentId = null;
        this.isContentIdSet = false;
        this.contentDescription = null;
        this.isContentDescriptionSet = false;
        this.contentDispositionType = null;
        this.contentDispositionParameters = Collections.emptyMap();
        this.contentDispositionModificationDate = null;
        this.contentDispositionModificationDateParseException = null;
        this.contentDispositionCreationDate = null;
        this.contentDispositionCreationDateParseException = null;
        this.contentDispositionReadDate = null;
        this.contentDispositionReadDateParseException = null;
        this.contentDispositionSize = -1L;
        this.contentDispositionSizeParseException = null;
        this.isContentDispositionSet = false;
        this.contentLanguage = null;
        this.contentLanguageParseException = null;
        this.isContentIdSet = false;
        this.contentLocation = null;
        this.contentLocationParseException = null;
        this.isContentLocationSet = false;
        this.contentMD5Raw = null;
        this.isContentMD5Set = false;
    }

    private void parseContentDescription(String str) {
        if (str == null) {
            this.contentDescription = "";
        } else {
            this.contentDescription = str.trim();
        }
        this.isContentDescriptionSet = true;
    }

    private void parseContentDisposition(String str) {
        this.isContentDispositionSet = true;
        Map<String, String> headerParams = MimeUtil.getHeaderParams(str);
        this.contentDispositionParameters = headerParams;
        this.contentDispositionType = headerParams.get("");
        String str2 = this.contentDispositionParameters.get("modification-date");
        if (str2 != null) {
            try {
                this.contentDispositionModificationDate = parseDate(str2);
            } catch (ParseException e) {
                this.contentDispositionModificationDateParseException = e;
            }
        }
        String str3 = this.contentDispositionParameters.get("creation-date");
        if (str3 != null) {
            try {
                this.contentDispositionCreationDate = parseDate(str3);
            } catch (ParseException e2) {
                this.contentDispositionCreationDateParseException = e2;
            }
        }
        String str4 = this.contentDispositionParameters.get("read-date");
        if (str4 != null) {
            try {
                this.contentDispositionReadDate = parseDate(str4);
            } catch (ParseException e3) {
                this.contentDispositionReadDateParseException = e3;
            }
        }
        String str5 = this.contentDispositionParameters.get("size");
        if (str5 != null) {
            try {
                this.contentDispositionSize = Long.parseLong(str5);
            } catch (NumberFormatException e4) {
                this.contentDispositionSizeParseException = (MimeException) new MimeException(e4.getMessage(), e4).fillInStackTrace();
            }
        }
        this.contentDispositionParameters.remove("");
    }

    private void parseContentId(String str) {
        if (str == null) {
            this.contentId = "";
        } else {
            this.contentId = str.trim();
        }
        this.isContentIdSet = true;
    }

    private DateTime parseDate(String str) throws ParseException {
        return new DateTimeParser(new StringReader(str)).date_time();
    }

    private void parseLanguage(String str) {
        this.isContentLanguageSet = true;
        if (str != null) {
            try {
                this.contentLanguage = new ContentLanguageParser(new StringReader(str)).parse();
            } catch (MimeException e) {
                this.contentLanguageParseException = e;
            }
        }
    }

    private void parseLocation(String str) {
        this.isContentLocationSet = true;
        if (str != null) {
            StructuredFieldParser structuredFieldParser = new StructuredFieldParser(new StringReader(str));
            structuredFieldParser.setFoldingPreserved(false);
            try {
                this.contentLocation = structuredFieldParser.parse();
            } catch (MimeException e) {
                this.contentLocationParseException = e;
            }
        }
    }

    private void parseMD5(String str) {
        this.isContentMD5Set = true;
        if (str != null) {
            this.contentMD5Raw = str.trim();
        }
    }

    private void parseMimeVersion(String str) {
        MimeVersionParser mimeVersionParser = new MimeVersionParser(new StringReader(str));
        try {
            mimeVersionParser.parse();
            int majorVersion = mimeVersionParser.getMajorVersion();
            if (majorVersion != -1) {
                this.mimeMajorVersion = majorVersion;
            }
            int minorVersion = mimeVersionParser.getMinorVersion();
            if (minorVersion != -1) {
                this.mimeMinorVersion = minorVersion;
            }
        } catch (MimeException e) {
            this.mimeVersionException = e;
        }
        this.isMimeVersionSet = true;
    }

    @Override // org.apache.james.mime4j.descriptor.DefaultBodyDescriptor, org.apache.james.mime4j.descriptor.MutableBodyDescriptor
    public void addField(Field field) {
        String name = field.getName();
        String body = field.getBody();
        String lowerCase = name.trim().toLowerCase();
        if (MimeUtil.MIME_HEADER_MIME_VERSION.equals(lowerCase) && !this.isMimeVersionSet) {
            parseMimeVersion(body);
        } else if (MimeUtil.MIME_HEADER_CONTENT_ID.equals(lowerCase) && !this.isContentIdSet) {
            parseContentId(body);
        } else if (MimeUtil.MIME_HEADER_CONTENT_DESCRIPTION.equals(lowerCase) && !this.isContentDescriptionSet) {
            parseContentDescription(body);
        } else if (MimeUtil.MIME_HEADER_CONTENT_DISPOSITION.equals(lowerCase) && !this.isContentDispositionSet) {
            parseContentDisposition(body);
        } else if (MimeUtil.MIME_HEADER_LANGAUGE.equals(lowerCase) && !this.isContentLanguageSet) {
            parseLanguage(body);
        } else if (MimeUtil.MIME_HEADER_LOCATION.equals(lowerCase) && !this.isContentLocationSet) {
            parseLocation(body);
        } else if (!MimeUtil.MIME_HEADER_MD5.equals(lowerCase) || this.isContentMD5Set) {
            super.addField(field);
        } else {
            parseMD5(body);
        }
    }

    public String getContentDescription() {
        return this.contentDescription;
    }

    public DateTime getContentDispositionCreationDate() {
        return this.contentDispositionCreationDate;
    }

    public MimeException getContentDispositionCreationDateParseException() {
        return this.contentDispositionCreationDateParseException;
    }

    public String getContentDispositionFilename() {
        return this.contentDispositionParameters.get("filename");
    }

    public DateTime getContentDispositionModificationDate() {
        return this.contentDispositionModificationDate;
    }

    public MimeException getContentDispositionModificationDateParseException() {
        return this.contentDispositionModificationDateParseException;
    }

    public Map<String, String> getContentDispositionParameters() {
        return this.contentDispositionParameters;
    }

    public DateTime getContentDispositionReadDate() {
        return this.contentDispositionReadDate;
    }

    public MimeException getContentDispositionReadDateParseException() {
        return this.contentDispositionReadDateParseException;
    }

    public long getContentDispositionSize() {
        return this.contentDispositionSize;
    }

    public MimeException getContentDispositionSizeParseException() {
        return this.contentDispositionSizeParseException;
    }

    public String getContentDispositionType() {
        return this.contentDispositionType;
    }

    public String getContentId() {
        return this.contentId;
    }

    public List<String> getContentLanguage() {
        return this.contentLanguage;
    }

    public MimeException getContentLanguageParseException() {
        return this.contentLanguageParseException;
    }

    public String getContentLocation() {
        return this.contentLocation;
    }

    public MimeException getContentLocationParseException() {
        return this.contentLocationParseException;
    }

    public String getContentMD5Raw() {
        return this.contentMD5Raw;
    }

    public int getMimeMajorVersion() {
        return this.mimeMajorVersion;
    }

    public int getMimeMinorVersion() {
        return this.mimeMinorVersion;
    }

    public MimeException getMimeVersionParseException() {
        return this.mimeVersionException;
    }
}
