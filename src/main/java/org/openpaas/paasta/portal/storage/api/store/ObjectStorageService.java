package org.openpaas.paasta.portal.storage.api.store;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

/**
 * 
 * @author hgcho (Hyungu Cho)
 * @version 
 * @since 2018. 4. 2.
 *
 */
public abstract class ObjectStorageService<T> {
    /**
     * Put object into Object Storage (upload, POST)
     * @param multipartFile the multipart file
     * @return stored file's infomation
     * @throws IOException 
     */
    public abstract T putObject(MultipartFile multipartFile) throws IOException;
    
    /**
     * Get object in object storage (get, GET)
     * @return stored file's infomation
     * @throws FileNotFoundException 
     */
    public abstract T getObject(String storedFilename) throws FileNotFoundException; 
    
    /**
     * Update object in object storage (update, PUT)
     * @return stored file's infomation
     */
    public abstract T updateObject(String storedFilename, MultipartFile multipartFile);
    
    /**
     * Remove object in object storage (remove/delete, DELETE)
     * @return 
     */
    public abstract boolean removeObject(String storedFilename);
}
