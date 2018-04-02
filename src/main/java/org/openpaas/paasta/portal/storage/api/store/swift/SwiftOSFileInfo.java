package org.openpaas.paasta.portal.storage.api.store.swift;

import java.lang.reflect.Field;

import org.javaswift.joss.model.StoredObject;
import org.openpaas.paasta.portal.storage.api.common.SwiftOSConstants.ControllerParameter;
import org.openpaas.paasta.portal.storage.api.common.SwiftOSConstants.ResultStatus;
import org.openpaas.paasta.portal.storage.api.store.ObjectStorageFileInfo;

public class SwiftOSFileInfo extends ObjectStorageFileInfo {
    private SwiftOSFileInfo() {
    }

    public static final SwiftOSFileInfo newInstance() {
        return new SwiftOSFileInfo();
    }

    public static final SwiftOSFileInfo newInstanceFromStoredObject(
        final StoredObject object ) {
        final SwiftOSFileInfo info = newInstance();
        info.setStoredFilename( object.getName() );
        info.setFileType( object.getContentType() );
        info.setLength( object.getContentLength() );
        info.setFileURL( object.getPublicURL() );
        info.setResultStatus( ResultStatus.SUCCESS );

        String originalFilename = (String) object
            .getMetadata( ControllerParameter.OBJECT_ORIGINAL_FILENAME_METAKEY );
        if ( null == originalFilename
            || ( null != originalFilename && "".equals( originalFilename ) ) ) {
            originalFilename = 
                object.getName().substring( 0, object.getName().indexOf( '-' ) );
        }
        info.setFilename( originalFilename );

        return info;
    }

    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append( "SwiftOSFileInfo@" ).append( Integer.toHexString( this.hashCode() ) );
        buffer.append( " detail information : " );
        buffer.append( "- File name : " ).append( getFilename() );
        buffer.append( "- File type : " ).append( getFileType() );
        buffer.append( "- Stored file name : ").append( getStoredFilename() );
        buffer.append( "- File length : " ).append( getLength() );
        buffer.append( "- File Public URL : " ).append( getFileURL() );
        buffer.append( "- Get result status : " ).append( getResultStatus() );
        
        return buffer.toString();
    }
}
