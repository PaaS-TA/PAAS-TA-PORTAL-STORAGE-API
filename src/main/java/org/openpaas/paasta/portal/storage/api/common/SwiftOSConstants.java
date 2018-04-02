package org.openpaas.paasta.portal.storage.api.common;

/**
 * Storage API constant
 * 
 * @author hgcho (Hyungu Cho)
 * @version 
 * @since 2018. 4. 2.
 *
 */
public class SwiftOSConstants {
    public static enum ResultStatus {
        SUCCESS, FAIL
    }
    
    /**
     * Environment keys for object storage
     * @author hgcho (Hyungu Cho)
     * @version 
     * @since 2018. 4. 2.
     *
     */
    public static class EnvironmentKeys {
        /** Tenent name of object storage */
        public static final String OBJECT_STORAGE_TENANT_NAME = "objectStorage.tenantName";
        /** User name to use object storage */
        public static final String OBJECT_STORAGE_USER_NAME = "objectStorage.username";
        /** Password of user to use object storage */
        public static final String OBJECT_STORAGE_PASSWORD = "objectStorage.password";
        /** Authentication URL of object storage */
        public static final String OBJECT_STORAGE_AUTH_URL = "objectStorage.authUrl";
        /** Container of object storage */
        public static final String OBJECT_STORAGE_CONTAINER = "objectStorage.container";
    }
    
    /**
     * Request URI for object storage
     * @author hgcho (Hyungu Cho)
     * @version 
     * @since 2018. 4. 2.
     */
    public static class ControllerURI {
        /** Object storage root URI */
        public static final String OBJECT_STORAGE_URI = "/";
        
        /** Get object in object storage (GET) */
        public static final String OBJECT_GET_URI = "/{name}";
        
        /** Insert(Register) object in object storage (POST) */
        public static final String OBJECT_INSERT_URI = "/upload";
        
        /** Modify object in object storage (PUT) */
        public static final String OBJECT_MODIFY_URI = "/{name}";
        
        /** Delete object in object storage (DELETE) */
        public static final String OBJECT_DELETE_URI = "/{name}";
    }
    
    public static class ControllerParameter {
        public static final String OBJECT_INSERT_FILE = "file";
        public static final String OBJECT_FILENAME_PATH_VARIABLE = "name";
        public static final String OBJECT_ORIGINAL_FILENAME_METAKEY = "originalfilename";
    }
}
