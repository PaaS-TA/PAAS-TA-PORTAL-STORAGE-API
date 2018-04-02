package org.openpaas.paasta.portal.storage.api.store;

import org.openpaas.paasta.portal.storage.api.common.SwiftOSConstants.ResultStatus;

/**
 * 
 * @author hgcho (Hyungu Cho)
 * @version 
 * @since 2018. 4. 2.
 *
 */
public abstract class ObjectStorageFileInfo {
    protected String filename;
    protected String storedFilename;
    protected String fileURL;
    protected String fileType;
    protected long length;
    protected ResultStatus resultStatus;

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

    public ResultStatus getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus( ResultStatus resultStatus ) {
        this.resultStatus = resultStatus;
    }
}
