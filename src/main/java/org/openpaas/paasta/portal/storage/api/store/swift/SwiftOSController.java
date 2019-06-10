package org.openpaas.paasta.portal.storage.api.store.swift;

import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;

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
import org.springframework.web.bind.annotation.*;
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
    @CrossOrigin
    @Consumes(MediaType.MULTIPART_FORM_DATA_VALUE)
    @PostMapping( value = { SwiftOSControllerURI.OBJECT_INSERT_URIS_A, SwiftOSControllerURI.OBJECT_INSERT_URIS_B } )
    public Object uploadObject(
        @RequestParam( SwiftOSCommonParameter.OBJECT_INSERT_FILE ) MultipartFile multipartFile ) throws IOException {

        final SwiftOSFileInfo fileInfo = swiftOSService.putObject( multipartFile );
        if (null == fileInfo) {
            LOGGER.warn("Cannot find information for stored object in swift object storage. :: uploadObject");
            return createResponseEntity( new byte[0], null, HttpStatus.NOT_FOUND );
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add( "Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE );
        headers.add( "X-Content-Type-Options", "nosniff" );
        
        return createResponseEntity( 
            ObjectMapperUtils.writeValueAsString( fileInfo ), headers, HttpStatus.CREATED );
    }

    /**
     * Get object in object storage (get, GET)
     * @throws FileNotFoundException 
     */
    @CrossOrigin
    @GetMapping( SwiftOSControllerURI.OBJECT_GET_RAW_URI )
    public Object getObjectRawURL( 
        @PathVariable( SwiftOSCommonParameter.OBJECT_FILENAME_PATH_VARIABLE ) String name ) throws FileNotFoundException {
        final SwiftOSFileInfo fileInfo = swiftOSService.getObject( name );
        if (null == fileInfo) {
            LOGGER.warn("Cannot create information instance for stored object in swift object storage. :: " +
                "getObjectRawURL");
            return createResponseEntity( new byte[0], null, HttpStatus.NOT_FOUND );
        }

        return createResponseEntity( fileInfo.getFileURL(), null, HttpStatus.CREATED );
    }
    
    /**
     * Get content type of object in object storage (GET)
     * @throws FileNotFoundException
     */
    @CrossOrigin
    @GetMapping( SwiftOSControllerURI.OBJECT_GET_CONTENT_TYPE_URI )
    public Object getObjectContentType( 
        @PathVariable( SwiftOSCommonParameter.OBJECT_FILENAME_PATH_VARIABLE ) String name) throws FileNotFoundException {
        final SwiftOSFileInfo fileInfo = swiftOSService.getObject( name );
        if (null == fileInfo) {
            LOGGER.warn("Cannot create information instance for stored object in swift object storage. :: getObjectContentType");
            return createResponseEntity( new byte[0], null, HttpStatus.NOT_FOUND );
        }
        
        return createResponseEntity( fileInfo.getFileType(), null, HttpStatus.CREATED );
    }
    @CrossOrigin
    @GetMapping( SwiftOSControllerURI.OBJECT_GET_RESOURCE_URI )
    public Object getObjectDownload( 
        @PathVariable( SwiftOSCommonParameter.OBJECT_FILENAME_PATH_VARIABLE ) String name, final HttpServletResponse response )
            throws IOException {
        final StoredObject object = swiftOSService.getRawObject( name );
        if (null == object) {
            LOGGER.warn("Cannot find stored object in swift object storage.");
            //return createResponseEntity( new byte[0], null, HttpStatus.NOT_FOUND );
        }

        final SwiftOSFileInfo fileInfo = SwiftOSFileInfo.newInstanceFromStoredObject( object );
        if (null == fileInfo) {
            LOGGER.warn("Cannot create information instance for stored object in swift object storage.");
            return createResponseEntity( new byte[0], null, HttpStatus.NOT_FOUND );
        }
        
        final HttpHeaders headers = new HttpHeaders();
        
        // use SwiftOSFileInfo.getFilename() instead of name(stored filename)
        headers.add( "Content-Disposition", ( "attachment;filename=" + fileInfo.getFilename() ) );
        headers.add( "Content-Transfer-Encoding", "binary" );


        // use SwiftOSFileInfo.getFileType() instead of StoredObject.getContentType()
        headers.add( "Content-Type", fileInfo.getFileType() );

        // debugging only
        if (LOGGER.isDebugEnabled()) {
            for ( Entry<String, List<String>> entry : headers.entrySet() )
                LOGGER.debug( "Header {} : {}", entry.getKey(), entry.getValue() );
        }
        
        byte[] rawContents = object.downloadObject();
        LOGGER.debug( "Raw content's length of {} : {}", object.getName(), rawContents.length );
        return createResponseEntity( rawContents, headers, HttpStatus.CREATED );
    }

    /**
     * Update object in object storage (update, PUT)
     */
    @CrossOrigin
    @PutMapping( SwiftOSControllerURI.OBJECT_MODIFY_URI )
    public Object updateObject( String filename, StoredObject object ) {
        throw new UnsupportedOperationException("Updating object doesn't support yet.");
    }

    /**
     * Remove object in object storage (remove/delete, DELETE)
     * @throws IOException 
     */
    @CrossOrigin
    @DeleteMapping( SwiftOSControllerURI.OBJECT_DELETE_URI )
    public Object removeObject( @PathVariable( SwiftOSCommonParameter.OBJECT_FILENAME_PATH_VARIABLE ) String name, final HttpServletResponse response ) throws IOException {
        if (swiftOSService.removeObject( name )) {
            return createResponseEntity( ResultStatus.SUCCESS, null, HttpStatus.CREATED );
        } else {
            return createResponseEntity( ResultStatus.FAIL, null, HttpStatus.CREATED );
        }
    }
    @CrossOrigin
    @GetMapping( SwiftOSControllerURI.OBJECT_LIST_URI )
    public String listFiles( ) {
        final StringBuffer buffer = new StringBuffer();
        final List<String> files = swiftOSService.listFileURLs();
        
        if (files.size() <= 0) {
            buffer.append( "<h2>No files.</h2>" );
        } else {
            for (final String file : files) {
                buffer.append( "<p>" )
                .append( file )
                .append( "</p>" );
            }
        }
        
        return buffer.toString();
    }
    
    private <T> ResponseEntity<T> createResponseEntity(final T object, HttpHeaders headers, HttpStatus httpStatus) {
        assertNotNull( object );
        
        if (null == headers) {
            headers = new HttpHeaders();
            headers.add( "Content-Type", MediaType.TEXT_PLAIN_VALUE );
            // This header works only for script or css
            headers.add( "X-Content-Type-Options", "nosniff" );
        }
        headers.add( "X-XSS-Protection", "1; mode=block" ); 
        
        if (null == httpStatus)
            httpStatus = HttpStatus.CREATED;
        
        final ResponseEntity<T> resEntity = new ResponseEntity<>( object, headers, httpStatus );
        return resEntity;
    }
    @CrossOrigin
    @GetMapping( "/v2/swift/upload-test/{local-file:.+}" )
    public Object uploadTestObject(@PathVariable("local-file") String localFilePath) throws IOException, URISyntaxException {
        //final SwiftOSFileInfo fileInfo = swiftOSService.putObject( multipartFile )
        InputStream is = getClass().getResourceAsStream( '/' + localFilePath );
        String contentType = Files.probeContentType( Paths.get( getClass().getResource( '/' + localFilePath ).toURI() ) );
        SwiftOSFileInfo fileInfo;
        if (null != is) {
            fileInfo = swiftOSService.putObject( localFilePath, is, contentType );
            if (null == fileInfo) {
                LOGGER.warn("Cannot find information for stored object in swift object storage :: upload test object");
                return createResponseEntity( new byte[0], null, HttpStatus.NOT_FOUND );
            }
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
