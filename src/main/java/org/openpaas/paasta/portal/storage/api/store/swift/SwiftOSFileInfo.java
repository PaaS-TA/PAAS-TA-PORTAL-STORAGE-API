package org.openpaas.paasta.portal.storage.api.store.swift;

import org.javaswift.joss.model.StoredObject;
import org.openpaas.paasta.portal.storage.api.config.SwiftOSConstants.ResultStatus;
import org.openpaas.paasta.portal.storage.api.config.SwiftOSConstants.SwiftOSCommonParameter;
import org.openpaas.paasta.portal.storage.api.store.ObjectStorageFileInfo;
import org.openpaas.paasta.portal.storage.api.util.FilenameUtils;
import org.springframework.http.MediaType;

public class SwiftOSFileInfo extends ObjectStorageFileInfo<SwiftOSFileInfo> {
    private SwiftOSFileInfo() { super(); }

    public static final SwiftOSFileInfo newInstance() {
        return new SwiftOSFileInfo();
    }

    public static final SwiftOSFileInfo newInstanceFromStoredObject( final StoredObject storedObj ) {
        if (null == storedObj)
            return null;

        final SwiftOSFileInfo info = newInstance();
        info.setStoredFilename( storedObj.getName() );
        info.setLength( storedObj.getContentLength() );
        info.setFileURL( storedObj.getPublicURL() );
        info.setResultStatus( ResultStatus.SUCCESS );

        // set original filename
        final Object metaOriginalFilename = storedObj.getMetadata( SwiftOSCommonParameter.OBJECT_ORIGINAL_FILENAME_METAKEY );
        String originalFilename;
        if (null != metaOriginalFilename ) {
            originalFilename = (String) metaOriginalFilename;
            if ( null == originalFilename
                || ( null != originalFilename && "".equals( originalFilename ) ) ) {
                originalFilename = storedObj.getName().substring( 0, storedObj.getName().indexOf( '-' ) );
            }
        } else {
            originalFilename = FilenameUtils.getOriginalFilename( storedObj.getName() );
        }
        info.setFilename( originalFilename );
        
        // set upload timestamp
        final Object timestamp = storedObj.getMetadata( SwiftOSCommonParameter.OBJECT_UPLOAD_TIMESTAMP );
        if (null == timestamp || (null != timestamp && "".equals( timestamp )))
            info.setUploadTimestamp( 0L );
        else 
            info.setUploadTimestamp( Long.parseLong( timestamp.toString() ) );
        
        // set content type
        final Object contentType = storedObj.getMetadata( SwiftOSCommonParameter.OBJECT_CONTENT_TYPE );
        if (null == contentType)
            info.setFileType( MediaType.APPLICATION_OCTET_STREAM.getType() );
        else
            info.setFileType( contentType.toString() );

        return info;
    }
    
    public boolean isEmptyInstance() {
        if (null == this.filename)
            return true;
        
        if (null == this.storedFilename)
            return true;
        
        if ("".equals( this.filename.replaceAll( " ", "" ) ))
            return true;
        
        if ("".equals( this.storedFilename.replaceAll( " ", "" ) ))
            return true;
        
        // finally...
        return false;
    }

    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append( getClass().getSimpleName() ).append( '@' ).append( Integer.toHexString( this.hashCode() ) ).append( '\n' );
        if ( false == isEmptyInstance() ) {
            buffer.append( "= Detail information : " ).append( '\n' );
            buffer.append( "|- File name : " ).append( getFilename() ).append( '\n' );
            buffer.append( "|- File type : " ).append( getFileType() ).append( '\n' );
            buffer.append( "|- Stored file name : ").append( getStoredFilename() ).append( '\n' );
            buffer.append( "|- File length : " ).append( getLength() ).append( '\n' );
            buffer.append( "|- File Public URL : " ).append( getFileURL() ).append( '\n' );
        }
        buffer.append( "\\- Get result status : " ).append( getResultStatus() );
        
        return buffer.toString();
    }
}
