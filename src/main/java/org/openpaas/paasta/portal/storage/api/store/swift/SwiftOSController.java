package org.openpaas.paasta.portal.storage.api.store.swift;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.javaswift.joss.model.StoredObject;
import org.openpaas.paasta.portal.storage.api.config.SwiftOSConstants.ResultStatus;
import org.openpaas.paasta.portal.storage.api.config.SwiftOSConstants.SwiftOSCommonParameter;
import org.openpaas.paasta.portal.storage.api.config.SwiftOSConstants.SwiftOSControllerURI;
import org.openpaas.paasta.portal.storage.api.util.ObjectMapperUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class SwiftOSController {
    private static final Logger LOGGER = LoggerFactory.getLogger( SwiftOSController.class );
    
    @Autowired
    SwiftOSService swiftOSService;
    
    @Autowired
    RestTemplate restTemplate;

    /**
     * Put object into Object Storage (upload, POST)
     * @throws IOException 
     */
    @PostMapping( SwiftOSControllerURI.OBJECT_INSERT_URI )
    public String uploadObject(
        @RequestParam( SwiftOSCommonParameter.OBJECT_INSERT_FILE ) MultipartFile multipartFile ) throws IOException {
        final SwiftOSFileInfo fileInfo = swiftOSService.putObject( multipartFile );
        
        // before : 
        // return ObjectMapperUtils.writeValueAsString( fileInfo );
        
        // after : 
        // return stored file name instead of SwiftOSFileInfo
        return fileInfo.getFilename();
    }

    /**
     * Get object in object storage (get, GET)
     * @throws FileNotFoundException 
     */
    @GetMapping( SwiftOSControllerURI.OBJECT_GET_RAW_URI )
    public String getObjectRawURL( 
        @PathVariable( SwiftOSCommonParameter.OBJECT_FILENAME_PATH_VARIABLE ) String name ) throws FileNotFoundException {
        final SwiftOSFileInfo fileInfo = swiftOSService.getObject( name );

        return fileInfo.getFileURL();
    }
    
    @GetMapping( SwiftOSControllerURI.OBJECT_GET_RESOURCE_URI )
    public void getObjectDownload( 
        @PathVariable( SwiftOSCommonParameter.OBJECT_FILENAME_PATH_VARIABLE ) String name, final HttpServletResponse response )
            throws IOException {
        final StoredObject object = swiftOSService.getRawObject( name );
        byte[] rawContents = object.downloadObject();
        // set header of binary object
        response.setHeader( "Content-Disposition", ( "attachment;filename=" + name ) );
        response.setHeader( "Content-Transfer-Encoding", "binary" );
        response.setContentType( object.getContentType() );
        LOGGER.debug( "Header {} : {}", "Content-Disposition", response.getHeader( "Content-Disposition"  ) );
        LOGGER.debug( "Header {} : {}", "Content-Transfer-Encoding", response.getHeader( "Content-Transfer-Encoding" ) );
        LOGGER.debug( "Content-Type : {}", object.getContentType() );
        
        response.getOutputStream().write( rawContents );
        response.flushBuffer();
        LOGGER.debug( "Response file : {} / length : {}", object.getName(), object.getContentLength() );
    }

    /**
     * Update object in object storage (update, PUT)
     */
    @PutMapping( SwiftOSControllerURI.OBJECT_MODIFY_URI )
    public String updateObject( String filename, StoredObject object ) {
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

    @GetMapping( "/v2/swift/upload-test/{local-file:.+}" )
    public @ResponseBody String uploadTestObject(@PathVariable("local-file") String localFilePath) throws IOException {
        //final SwiftOSFileInfo fileInfo = swiftOSService.putObject( multipartFile )
        
        InputStream is = getClass().getResourceAsStream( '/' + localFilePath );
        SwiftOSFileInfo fileInfo;
        if (null != is) {
            fileInfo = swiftOSService.putObject( localFilePath, is, "Application/octet-stream" );
        } else {
            fileInfo = SwiftOSFileInfo.newInstance();
        }
        
        return ObjectMapperUtils.writeValueAsString( fileInfo );
    }
}
