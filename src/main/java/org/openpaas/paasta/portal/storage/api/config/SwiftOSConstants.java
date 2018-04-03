package org.openpaas.paasta.portal.storage.api.config;

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
     * Environment keys for Swift Object Storage
     * @author hgcho (Hyungu Cho)
     * @version 
     * @since 2018. 4. 2.
     *
     */
    public static class SwiftOSEnvironmentKeys {
        private static final String OBJECT_STORAGE_PREFIX = "objectStorage.swift";
        
        /** Tenent name of object storage */
        public static final String OBJECT_STORAGE_TENANT_NAME = OBJECT_STORAGE_PREFIX + ".tenantName";
        
        /** User name to use object storage */
        public static final String OBJECT_STORAGE_USER_NAME = OBJECT_STORAGE_PREFIX + ".username";
        
        /** Password of user to use object storage */
        public static final String OBJECT_STORAGE_PASSWORD = OBJECT_STORAGE_PREFIX + ".password";
        
        /** Authentication URL of object storage */
        public static final String OBJECT_STORAGE_AUTH_URL = OBJECT_STORAGE_PREFIX + ".authUrl";
        
        /** Authentication Method of object storage */
        public static final String OBJECT_STORAGE_AUTH_METHOD = OBJECT_STORAGE_PREFIX + ".authMethod";
        
        /** Container of object storage */
        public static final String OBJECT_STORAGE_CONTAINER = OBJECT_STORAGE_PREFIX + ".container";
        
        /** Preferred region of object storage */
        public static final String OBJECT_STORAGE_PREFERRED_REGION = OBJECT_STORAGE_PREFIX + ".preferredRegion";
    }
    
    /**
     * Request URI for object storage
     * @author hgcho (Hyungu Cho)
     * @version 
     * @since 2018. 4. 2.
     */
    public static class SwiftOSControllerURI {
        /** Object storage root URI */
        public static final String OBJECT_STORAGE_URI = "/v2/swift";
        
        /** Get object in object storage (GET) */
        public static final String OBJECT_GET_URI = OBJECT_STORAGE_URI + "/{name}";
        
        /** Insert(Register) object in object storage (POST) */
        public static final String OBJECT_INSERT_URI = OBJECT_STORAGE_URI + "/";
        
        /** Modify object in object storage (PUT) -- Unsupported Operation */
        public static final String OBJECT_MODIFY_URI = OBJECT_STORAGE_URI + "/{name}";
        
        /** Delete object in object storage (DELETE) */
        public static final String OBJECT_DELETE_URI = OBJECT_STORAGE_URI + "/{name}";
        
        /** List objects in object storage (GET) */
        public static final String OBJECT_LIST_URI = OBJECT_STORAGE_URI + "/list";
    }
    
    public static class SwiftOSCommonParameter {
        public static final String OBJECT_INSERT_FILE = "file";
        public static final String OBJECT_FILENAME_PATH_VARIABLE = "name";
        public static final String OBJECT_ORIGINAL_FILENAME_METAKEY = "originalfilename";
    }
}
