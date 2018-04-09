package org.openpaas.paasta.portal.storage.api.store;

import org.openpaas.paasta.portal.storage.api.config.SwiftOSConstants.ResultStatus;

/**
 * 
 * @author hgcho (Hyungu Cho)
 * @version 
 * @since 2018. 4. 2.
 *
 */
public abstract class ObjectStorageFileInfo<T> {
    protected String filename;
    protected String storedFilename;
    protected String fileURL;
    protected String fileType;
    protected long length;
    protected long uploadTimestamp;
    protected ResultStatus resultStatus;
    
    public static <T> T newInstance() {
        return null;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename( String filename ) {
        this.filename = filename;
    }

    public String getStoredFilename() {
        return storedFilename;
    }

    public void setStoredFilename( String storedFilename ) {
        this.storedFilename = storedFilename;
    }

    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL( String fileURL ) {
        this.fileURL = fileURL;
    }
    
    public String getFileType() {
        return fileType;
    }
    
    public void setFileType( String fileType ) {
        this.fileType = fileType;
    }
    
    public long getLength() {
        return length;
    }
    
    public void setLength( long length ) {
        this.length = length;
    }
    
    public long getUploadTimestamp() {
        return uploadTimestamp;
    }
    
    public void setUploadTimestamp( long uploadTimestamp ) {
        this.uploadTimestamp = uploadTimestamp;
    }

    public ResultStatus getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus( ResultStatus resultStatus ) {
        this.resultStatus = resultStatus;
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append( getClass().getSimpleName() ).append( '@' ).append( Integer.toHexString( this.hashCode() ) );
        if (null != this.filename)
            buffer.append( ':' ).append( this.filename );
        
        return buffer.toString();
    }
}
