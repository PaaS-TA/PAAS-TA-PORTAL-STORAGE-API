package org.openpaas.paasta.portal.storage.api.store.swift;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.javaswift.joss.model.StoredObject;
import org.openpaas.paasta.portal.storage.api.config.SwiftOSConstants.ResultStatus;
import org.openpaas.paasta.portal.storage.api.config.SwiftOSConstants.SwiftOSCommonParameter;
import org.openpaas.paasta.portal.storage.api.config.SwiftOSConstants.SwiftOSControllerURI;
import org.openpaas.paasta.portal.storage.api.util.ObjectMapperUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping( SwiftOSControllerURI.OBJECT_STORAGE_HELLO_SERVICE )
    public String helloSerivce() {
        return "Hello Swift object storage service on Storage API of OpenPaaS Portal!";
    }
    
    /**
     * Put object into Object Storage (upload, POST)
     * @throws IOException 
     */
    @PostMapping( SwiftOSControllerURI.OBJECT_INSERT_URI )
    public Object uploadObject(
        @RequestParam( SwiftOSCommonParameter.OBJECT_INSERT_FILE ) MultipartFile multipartFile ) throws IOException {
        final SwiftOSFileInfo fileInfo = swiftOSService.putObject( multipartFile );
        
        HttpHeaders headers = new HttpHeaders();
        headers.add( "Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE );
        headers.add( "X-Content-Type-Options", "nosniff" );
        
        //return ObjectMapperUtils.writeValueAsString( fileInfo );
        return createResponseEntity(
            ObjectMapperUtils.writeValueAsString( fileInfo ), headers, HttpStatus.CREATED);
    }

    /**
     * Get object in object storage (get, GET)
     * @throws FileNotFoundException 
     */
    @GetMapping( SwiftOSControllerURI.OBJECT_GET_RAW_URI )
    public Object getObjectRawURL( 
        @PathVariable( SwiftOSCommonParameter.OBJECT_FILENAME_PATH_VARIABLE ) String name, final HttpServletResponse response ) throws FileNotFoundException {
        final SwiftOSFileInfo fileInfo = swiftOSService.getObject( name );
        //addNoSniffContentHeader( response, MediaType.TEXT_PLAIN );
        
        //return fileInfo.getFileURL();
        return createResponseEntity( fileInfo.getFileURL(), null, HttpStatus.CREATED );
    }
    
    @GetMapping( SwiftOSControllerURI.OBJECT_GET_RESOURCE_URI )
    public Object getObjectDownload( 
        @PathVariable( SwiftOSCommonParameter.OBJECT_FILENAME_PATH_VARIABLE ) String name, final HttpServletResponse response )
            throws IOException {
        final StoredObject object = swiftOSService.getRawObject( name );
        final HttpHeaders headers = new HttpHeaders();
        headers.add( "Content-Disposition", ( "attachment;filename=" + name ) );
        headers.add( "Content-Transfer-Encoding", "binary" );
        
        if (null == object) {
            return createResponseEntity( new byte[0], headers, HttpStatus.NOT_FOUND );
        } else {
            headers.add( "Content-Type", object.getContentType() );

            // debugging only
            for ( Entry<String, List<String>> entry : headers.entrySet() )
            LOGGER.debug( "Header {} : {}", entry.getKey(), entry.getValue() );
            byte[] rawContents = object.downloadObject();
            LOGGER.debug( "Raw content's length of {} : {}", object.getName(), rawContents.length );
            return createResponseEntity( rawContents, headers, HttpStatus.CREATED );
        }
    }

    /**
     * Update object in object storage (update, PUT)
     */
    @PutMapping( SwiftOSControllerURI.OBJECT_MODIFY_URI )
    public Object updateObject( String filename, StoredObject object ) {
        throw new UnsupportedOperationException("Updating object doesn't support yet.");
    }

    /**
     * Remove object in object storage (remove/delete, DELETE)
     */
    @DeleteMapping( SwiftOSControllerURI.OBJECT_DELETE_URI )
    public Object removeObject( @PathVariable( SwiftOSCommonParameter.OBJECT_FILENAME_PATH_VARIABLE ) String name, final HttpServletResponse response ) {
        //addNoSniffContentHeader( response, MediaType.TEXT_PLAIN );
        
        ResultStatus status;
        if (swiftOSService.removeObject( name ))
            status = ResultStatus.SUCCESS;
        else
            status = ResultStatus.FAIL;
        
        return createResponseEntity( status, null, HttpStatus.CREATED );
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
    
    private <T> ResponseEntity<T> createResponseEntity(final T object, HttpHeaders headers, final HttpStatus httpStatus) {
        if (null == headers) {
            headers = new HttpHeaders();
            headers.add( "Content-Type", MediaType.TEXT_PLAIN_VALUE );
            headers.add( "X-Content-Type-Options", "nosniff" );
        }
        headers.add( "X-XSS-Protection", "1; mode=block" ); 
        
        final ResponseEntity<T> resEntity = new ResponseEntity<>( object, headers, httpStatus );
        return resEntity;
    }

    @GetMapping( "/v2/swift/upload-test/{local-file:.+}" )
    public Object uploadTestObject(@PathVariable("local-file") String localFilePath) throws IOException {
        //final SwiftOSFileInfo fileInfo = swiftOSService.putObject( multipartFile )
        InputStream is = getClass().getResourceAsStream( '/' + localFilePath );
        SwiftOSFileInfo fileInfo;
        if (null != is) {
            fileInfo = swiftOSService.putObject( localFilePath, is, "Application/octet-stream" );
        } else {
            fileInfo = SwiftOSFileInfo.newInstance();
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.add( "Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE );
        headers.add( "X-Content-Type-Options", "nosniff" );

        return createResponseEntity(
            ObjectMapperUtils.writeValueAsString( fileInfo ), headers, HttpStatus.CREATED);
    }
}
