package org.openpaas.paasta.portal.storage.api.store.swift;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import javax.swing.filechooser.FileNameExtensionFilter;

import org.javaswift.joss.model.Container;
import org.javaswift.joss.model.StoredObject;
import org.openpaas.paasta.portal.storage.api.common.SwiftOSConstants.ControllerParameter;
import org.openpaas.paasta.portal.storage.api.common.SwiftOSConstants.ResultStatus;
import org.openpaas.paasta.portal.storage.api.store.ObjectStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SwiftOSService extends ObjectStorageService<SwiftOSFileInfo> {
    private static final Logger LOGGER = LoggerFactory.getLogger( SwiftOSService.class );
    
    private final Container container;
    
    @Autowired
    public SwiftOSService(Container container) {
        this.container = container;
    }
    
    @Override
    public SwiftOSFileInfo putObject( final MultipartFile multipartFile ) throws IOException {
        final String filename = multipartFile.getOriginalFilename();
        final String storedFilename = ( UUID.randomUUID().toString().replaceAll( "-", "" )
            + '-' + filename.substring(filename.lastIndexOf('.')) );
        LOGGER.debug( "transferring filename : {}", filename );
        LOGGER.debug( "storing filename into object storage : {}", storedFilename );
        
        final StoredObject object = container.getObject( storedFilename );
        LOGGER.debug( "StoredObject : {}", object );
        object.setContentType( multipartFile.getContentType() );
        object.setAndSaveMetadata( ControllerParameter.OBJECT_ORIGINAL_FILENAME_METAKEY, multipartFile.getOriginalFilename() );
        object.uploadObject( multipartFile.getInputStream() );
        
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

    @Override
    public SwiftOSFileInfo getObject( final String filename ) throws FileNotFoundException {
        final StoredObject object = container.getObject( filename );
        if (object.exists())
            throw new FileNotFoundException( "\"" + filename + "\" is not found in object storage." );
        final SwiftOSFileInfo fileInfo = SwiftOSFileInfo.newInstanceFromStoredObject( object );
        LOGGER.debug( "SwiftOSFileInfo : {}", fileInfo );
        
        return fileInfo;
    }

    @Override
    public SwiftOSFileInfo updateObject( String filename, MultipartFile multipartFile ) {
        throw new UnsupportedOperationException("Updating object doesn't support yet.");
    }

    @Override
    public void removeObject( final String filename ) {
        final StoredObject object = container.getObject( filename );
        LOGGER.debug( "Delete object : {} ({})", object.getName(), object.getMetadata( ControllerParameter.OBJECT_ORIGINAL_FILENAME_METAKEY ) );
        
        if (object.exists())
            object.delete();
        
        // after delete...
        if (object.exists())
            throw new IllegalStateException( "File(" + filename + ") can't delete..." );
    }

}
