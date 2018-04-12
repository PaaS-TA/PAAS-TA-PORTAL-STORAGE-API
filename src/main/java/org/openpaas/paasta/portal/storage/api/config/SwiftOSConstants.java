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
        public static final String OBJECT_STORAGE_ROOT_URI = "/v2/swift";
        
        public static final String OBJECT_STORAGE_HELLO_SERVICE = OBJECT_STORAGE_ROOT_URI + "/";
        
        /** Get object in object storage (GET) */
        public static final String OBJECT_GET_RESOURCE_URI = OBJECT_STORAGE_ROOT_URI + "/{name:.+}";
        
        /** Get object in object storage (GET) */
        public static final String OBJECT_GET_RAW_URI = OBJECT_STORAGE_ROOT_URI + "/{name:.+}/rawurl";
        
        /** Get content type of object in object storage (GET) */
        public static final String OBJECT_GET_CONTENT_TYPE_URI = OBJECT_STORAGE_ROOT_URI + "/{name:.+}/contenttype";
        
        /** Insert(Register) object in object storage (POST) */
        public static final String OBJECT_INSERT_URIS_A = OBJECT_STORAGE_ROOT_URI + "/";
        /** Insert(Register) object in object storage (POST) */
        public static final String OBJECT_INSERT_URIS_B = OBJECT_STORAGE_ROOT_URI;
        
        /** Modify object in object storage (PUT) -- Unsupported Operation */
        public static final String OBJECT_MODIFY_URI = OBJECT_STORAGE_ROOT_URI + "/{name:.+}";
        
        /** Delete object in object storage (DELETE) */
        public static final String OBJECT_DELETE_URI = OBJECT_STORAGE_ROOT_URI + "/{name:.+}";
        
        /** List objects in object storage (GET) */
        public static final String OBJECT_LIST_URI = OBJECT_STORAGE_ROOT_URI + "/list";
        
        /** Upload test using local file */
        protected static final String OBJECT_PUT_TEST_URI = OBJECT_STORAGE_ROOT_URI + "/upload-test/{local-file:.+}";
    }
    
    public static class SwiftOSCommonParameter {
        public static final String OBJECT_INSERT_FILE = "file";
        public static final String OBJECT_FILENAME_PATH_VARIABLE = "name";
        public static final String OBJECT_ORIGINAL_FILENAME_METAKEY = "originalfilename";
        public static final String OBJECT_UPLOAD_TIMESTAMP = "uploadtimestamp";
        public static final String OBJECT_CONTENT_TYPE = "contenttype";
    }
}
