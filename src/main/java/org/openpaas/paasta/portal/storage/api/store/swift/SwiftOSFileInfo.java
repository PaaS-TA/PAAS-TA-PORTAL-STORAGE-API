package org.openpaas.paasta.portal.storage.api.store.swift;

import org.javaswift.joss.model.StoredObject;
import org.openpaas.paasta.portal.storage.api.config.SwiftOSConstants.ResultStatus;
import org.openpaas.paasta.portal.storage.api.config.SwiftOSConstants.SwiftOSCommonParameter;
import org.openpaas.paasta.portal.storage.api.store.ObjectStorageFileInfo;
import org.springframework.util.Assert;

public class SwiftOSFileInfo extends ObjectStorageFileInfo<SwiftOSFileInfo> {
    private SwiftOSFileInfo() { super(); }

    public static final SwiftOSFileInfo newInstance() {
        return new SwiftOSFileInfo();
    }

    public static final SwiftOSFileInfo newInstanceFromStoredObject( final StoredObject storedObj ) {
        final SwiftOSFileInfo info = newInstance();
        info.setStoredFilename( storedObj.getName() );
        info.setFileType( storedObj.getContentType() );
        info.setLength( storedObj.getContentLength() );
        info.setFileURL( storedObj.getPublicURL() );
        info.setResultStatus( ResultStatus.SUCCESS );

        final Object metaOriginalFilename = storedObj.getMetadata( SwiftOSCommonParameter.OBJECT_ORIGINAL_FILENAME_METAKEY );
        String originalFilename;
        if (null != metaOriginalFilename ) {
            originalFilename = (String) metaOriginalFilename;
            if ( null == originalFilename
                || ( null != originalFilename && "".equals( originalFilename ) ) ) {
                originalFilename = storedObj.getName().substring( 0, storedObj.getName().indexOf( '-' ) );
            }
        } else {
            originalFilename = getOriginalFilename(storedObj.getName());
        }
        info.setFilename( originalFilename );

        return info;
    }
    
    public static final String getOriginalFilename( final String storedFilename ) {
        Assert.notNull( storedFilename, "Stored object's filename is empty" );
        
        if ( !storedFilename.contains( "-" ) )
            return storedFilename;
        
        final int firstIndex = storedFilename.indexOf( '-' ) + 1;
        final int lastIndex = storedFilename.length();
        
        return storedFilename.substring( firstIndex, lastIndex );
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
        buffer.append( getClass().getSimpleName() ).append( '@' ).append( Integer.toHexString( this.hashCode() ) );
        if ( null == this.filename)
        buffer.append( "= Detail information : " );
        buffer.append( "|- File name : " ).append( getFilename() );
        buffer.append( "|- File type : " ).append( getFileType() );
        buffer.append( "|- Stored file name : ").append( getStoredFilename() );
        buffer.append( "|- File length : " ).append( getLength() );
        buffer.append( "|- File Public URL : " ).append( getFileURL() );
        buffer.append( "\\- Get result status : " ).append( getResultStatus() );
        
        return buffer.toString();
    }
}
