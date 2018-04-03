package org.openpaas.paasta.portal.storage.api.store.swift;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.javaswift.joss.model.StoredObject;
import org.openpaas.paasta.portal.storage.api.config.SwiftOSConstants.ResultStatus;
import org.openpaas.paasta.portal.storage.api.config.SwiftOSConstants.SwiftOSCommonParameter;
import org.openpaas.paasta.portal.storage.api.config.SwiftOSConstants.SwiftOSControllerURI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class SwiftOSController {
    @Autowired
    SwiftOSService swiftOSService;
    
    @Autowired
    RestTemplate restTemplate;

    /**
     * Put object into Object Storage (upload, POST)
     * @throws IOException 
     */
    @PostMapping( SwiftOSControllerURI.OBJECT_INSERT_URI )
    public SwiftOSFileInfo uploadObject(
        @RequestParam( SwiftOSCommonParameter.OBJECT_INSERT_FILE ) MultipartFile multipartFile ) throws IOException {
        final SwiftOSFileInfo fileInfo = swiftOSService.putObject( multipartFile );
        
        return fileInfo;
    }

    /**
     * Get object in object storage (get, GET)
     * @throws FileNotFoundException 
     */
    @GetMapping( SwiftOSControllerURI.OBJECT_GET_URI )
    public String getObjectURL( @PathVariable( SwiftOSCommonParameter.OBJECT_FILENAME_PATH_VARIABLE ) String name, 
        final HttpServletRequest request, final HttpServletResponse response ) throws FileNotFoundException {
        final SwiftOSFileInfo fileInfo = swiftOSService.getObject( name );

        return fileInfo.getFileURL();
    }

    /**
     * Update object in object storage (update, PUT)
     */
    @PutMapping( SwiftOSControllerURI.OBJECT_MODIFY_URI )
    public StoredObject updateObject( String filename, StoredObject object ) {
        throw new UnsupportedOperationException("Updating object doesn't support yet.");
    }

    /**
     * Remove object in object storage (remove/delete, DELETE)
     */
    @DeleteMapping( SwiftOSControllerURI.OBJECT_DELETE_URI )
    public String removeObject( String filename ) {
        if (swiftOSService.removeObject( filename ))
            return ResultStatus.SUCCESS.name();
        else
            return ResultStatus.FAIL.name();
    }
    
    @GetMapping( SwiftOSControllerURI.OBJECT_LIST_URI )
    public String listFiles( ) {
        final StringBuffer buffer = new StringBuffer();
        final List<String> files = swiftOSService.listFileURLs();
        
        for (final String file : files) {
            buffer.append( "<p>" )
            .append( file )
            .append( "</p>" );
        }
        
        return buffer.toString();
    }

    @GetMapping( "/upload-test/{local-file}")
    public SwiftOSFileInfo uploadTestObject(@PathVariable("local-file") String localFilePath) {
        //final SwiftOSFileInfo fileInfo = swiftOSService.putObject( multipartFile )
        
        InputStream is = getClass().getResourceAsStream( localFilePath );
        final SwiftOSFileInfo fileInfo = swiftOSService.putObject( localFilePath, is, "application/octet-stream" );
        
        return fileInfo;
    }
}
