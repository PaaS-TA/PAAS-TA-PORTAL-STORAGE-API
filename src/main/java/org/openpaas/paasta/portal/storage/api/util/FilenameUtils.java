package org.openpaas.paasta.portal.storage.api.util;

import java.util.UUID;

import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;

public class FilenameUtils {
    public static final String generateStoredFilename( final String filename ) {
        return generateStoredFilename( filename, 0L );
    }
    
    public static final String generateStoredFilename( final String filename, Long timestamp ) {
        Assert.notNull( filename, "Filename instance is empty : " + filename );
        if (null == timestamp)
            timestamp = 0L;
        
        // Filename Rule : [uuid-32-chars]-[timestamp]-[original-file-name]
        // example : 081e756fd63f4648b077a42cc4acf88e-151800101-site_logo.png
        final StringBuffer buffer = new StringBuffer();
        final String uuidStr = UUID.randomUUID().toString().replaceAll( "-", "" );
        final String encodeFilename = Base64Utils.encodeToUrlSafeString( filename.getBytes() );
        
        buffer.append( uuidStr ).append( '-' )
            .append( timestamp ).append( '-' )
            .append( encodeFilename );
        
        return buffer.toString(); 
    }
    
    public static final long getUploadTimestamp ( final String storedFilename ) {
        Assert.notNull( storedFilename, "Stored object's filename is empty" );
        
        if ( !storedFilename.contains( "-" ) )
            return 0L;
        
        final int firstIndex = storedFilename.indexOf( '-' ) + 1;
        final int secondIndex = storedFilename.lastIndexOf( '-' );
        
        return Long.parseLong( storedFilename.substring( firstIndex, secondIndex ) );
    }
    
    public static final String getEncodeOriginalFilename( final String storedFilename ) {
        Assert.notNull( storedFilename, "Stored object's filename is empty" );
        
        if ( !storedFilename.contains( "-" ) )
            return storedFilename;
        
        int firstIndex = storedFilename.lastIndexOf( '-' ) + 1;
        final int lastIndex = storedFilename.length();
        
        return storedFilename.substring( firstIndex, lastIndex );
    }
    
    public static final String getStoredFilenameWithDecodedOriginalFilename( final String storedFilename ) {
        if ( !storedFilename.contains( "-" ) )
            return storedFilename;
        
        final int lastIndex = storedFilename.lastIndexOf( '-' ) + 1;
        
        if ( lastIndex == storedFilename.indexOf( '-' ) + 1 )
            return storedFilename;
        
        return (storedFilename.substring( 0, lastIndex ) + getOriginalFilename( storedFilename ));
    }
    
    public static final String getOriginalFilename( final String storedFilename ) {
        final String encodeFilename = getEncodeOriginalFilename(storedFilename);
        return new String( Base64Utils.decodeFromUrlSafeString( encodeFilename ) );
    }
    
    public static final String encodeFilename( final String filename ) {
        return Base64Utils.encodeToUrlSafeString( filename.getBytes() );
    }
    
    public static final String decodeFilename( final String encodeFilename ) {
        return new String( Base64Utils.decodeFromUrlSafeString( encodeFilename ) );
    }
}
