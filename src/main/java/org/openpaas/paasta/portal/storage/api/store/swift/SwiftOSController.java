package org.openpaas.paasta.portal.storage.api.store.swift;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.javaswift.joss.model.StoredObject;
import org.openpaas.paasta.portal.storage.api.common.SwiftOSConstants.ControllerParameter;
import org.openpaas.paasta.portal.storage.api.common.SwiftOSConstants.ControllerURI;
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
    @PostMapping( ControllerURI.OBJECT_INSERT_URI )
    public SwiftOSFileInfo uploadObject(
        @RequestParam( ControllerParameter.OBJECT_INSERT_FILE ) MultipartFile multipartFile ) throws IOException {
        final SwiftOSFileInfo fileInfo = swiftOSService.putObject( multipartFile );
        
        return fileInfo;
    }

    /**
     * Get object in object storage (get, GET)
     * @throws FileNotFoundException 
     */
    @GetMapping( ControllerURI.OBJECT_GET_URI )
    public String getObjectURL( @PathVariable( ControllerParameter.OBJECT_FILENAME_PATH_VARIABLE ) String name, 
        final HttpServletRequest request, final HttpServletResponse response ) throws FileNotFoundException {
        final SwiftOSFileInfo fileInfo = swiftOSService.getObject( name );

        return fileInfo.getFileURL();
    }

    /**
     * Update object in object storage (update, PUT)
     */
    @PutMapping( ControllerURI.OBJECT_MODIFY_URI )
    public StoredObject updateObject( String filename, StoredObject object ) {
        throw new UnsupportedOperationException("Updating object doesn't support yet.");
    }

    /**
     * Remove object in object storage (remove/delete, DELETE)
     */
    @DeleteMapping( ControllerURI.OBJECT_DELETE_URI )
    public void removeObject( String filename ) {
        
    }

}
