package org.openpaas.paasta.portal.storage.api.store.swift;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.swing.filechooser.FileNameExtensionFilter;

import org.javaswift.joss.model.Container;
import org.javaswift.joss.model.StoredObject;
import org.openpaas.paasta.portal.storage.api.config.SwiftOSConstants.ResultStatus;
import org.openpaas.paasta.portal.storage.api.config.SwiftOSConstants.SwiftOSCommonParameter;
import org.openpaas.paasta.portal.storage.api.store.ObjectStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SwiftOSService extends ObjectStorageService<SwiftOSFileInfo> {
    private static final Logger LOGGER = LoggerFactory.getLogger( SwiftOSService.class );
    
    @Autowired
    private final Container container;
    
    public SwiftOSService(Container container) {
        this.container = container;
    }
    
    @Override
    public SwiftOSFileInfo putObject( final MultipartFile multipartFile ) throws IOException {
        final String filename = multipartFile.getOriginalFilename();
        final String storedFilename = generateStoredFilename( filename );
        LOGGER.debug( "transferring filename : {}", filename );
        LOGGER.debug( "storing filename into object storage : {}", storedFilename );
        
        // create StoredObject instance
        final StoredObject object = container.getObject( storedFilename );
        LOGGER.debug( "StoredObject : {}", object );
        
        // upload object
        object.uploadObject( multipartFile.getInputStream() );
        LOGGER.debug( "Done upload object : {} ({})", storedFilename, object.getPublicURL() );
        
        // after it uploads object, it sets content type and additional metadata in object storage
        object.setContentType( multipartFile.getContentType() );
        object.setAndSaveMetadata( SwiftOSCommonParameter.OBJECT_ORIGINAL_FILENAME_METAKEY, multipartFile.getOriginalFilename() );
        
        /*
        // setting manually
        final SwiftOSFileInfo fileInfo = SwiftOSFileInfo.newInstance();
        fileInfo.setFilename( filename );
        fileInfo.setFileType( multipartFile.getContentType() );
        fileInfo.setStoredFilename( storedFilename );
        fileInfo.setLength( object.getContentLength() );
        fileInfo.setFileURL( object.getPublicURL() );
        fileInfo.setResultStatus( ResultStatus.SUCCESS );
        */
        
        final SwiftOSFileInfo fileInfo = SwiftOSFileInfo.newInstanceFromStoredObject( object );
        LOGGER.debug( "SwiftOSFileInfo : {}", fileInfo );
        
        return fileInfo;
    }
    
    public SwiftOSFileInfo putObject( final String filename, final InputStream content, final String contentType ) {
        final String storedFilename = generateStoredFilename( filename );
        
        // create StoredObject instance
        final StoredObject object = container.getObject( storedFilename );
        LOGGER.debug( "StoredObject : {}", object );
        
        // upload object
        object.uploadObject( content );
        LOGGER.debug( "Done upload object : {} ({})", storedFilename, object.getPublicURL() );
        
        // after it uploads object, it sets content type and additional metadata in object storage
        object.setContentType( contentType );
        object.setAndSaveMetadata( SwiftOSCommonParameter.OBJECT_ORIGINAL_FILENAME_METAKEY, filename );
        
        final SwiftOSFileInfo fileInfo = SwiftOSFileInfo.newInstanceFromStoredObject( object );
        LOGGER.debug( "SwiftOSFileInfo : {}", fileInfo );
        
        return fileInfo;
    }
    
    @Override
    public SwiftOSFileInfo getObject( final String filename ) throws FileNotFoundException {
        final StoredObject object = getRawObject( filename );
        final SwiftOSFileInfo fileInfo = SwiftOSFileInfo.newInstanceFromStoredObject( object );
        LOGGER.debug( "SwiftOSFileInfo : {}", fileInfo );
        
        return fileInfo;
    }
    
    public StoredObject getRawObject( final String filename ) throws FileNotFoundException {
        final StoredObject object = container.getObject( filename );
        if (false == object.exists())
            throw new FileNotFoundException( "\"" + filename + "\" is not found in object storage." );
        
        return object;
    }

    @Override
    public SwiftOSFileInfo updateObject( String filename, MultipartFile multipartFile ) {
        throw new UnsupportedOperationException("Updating object doesn't support yet.");
    }

    @Override
    public boolean removeObject( final String filename ) {
        final StoredObject object = container.getObject( filename );
        LOGGER.debug( "Delete object : {} ({})", object.getName(), object.getMetadata( SwiftOSCommonParameter.OBJECT_ORIGINAL_FILENAME_METAKEY ) );
        
        if (true == object.exists())
            object.delete();
        
        // after delete...
        if (true == object.exists()) {
            Exception ex = new IllegalStateException( "File(" + filename + ") can't delete..." );
            LOGGER.error( "Cannot delete...", ex );
            
            return false;
        }
        
        return true;
    }
    
    public List<String> listFileURLs() {
        final Collection<StoredObject> list = container.list();
        final List<String> urlList = new ArrayList<>();
        for (StoredObject object : list)
            urlList.add( object.getName() + "( " + object.getPublicURL() + " )" );
        
        return urlList;
    }

    protected final String generateStoredFilename( final String filename ) {
        final String uuid = UUID.randomUUID().toString().replaceAll( "-", "" );
        final String baseName = filename.substring( 0, filename.lastIndexOf( '.' ) );
        final String extension = filename.substring( filename.lastIndexOf( '.' ) + 1, filename.length() );
        
        return (uuid + '-' + baseName + '-' + extension);
    }
    
    protected final String getOriginalFilename( final String storedFilename ) {
        // recycle method
        return SwiftOSFileInfo.getOriginalFilename( storedFilename );
    }
}
