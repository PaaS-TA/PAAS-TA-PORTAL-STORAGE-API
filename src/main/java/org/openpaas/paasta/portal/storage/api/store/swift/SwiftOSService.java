package org.openpaas.paasta.portal.storage.api.store.swift;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import org.javaswift.joss.model.Container;
import org.javaswift.joss.model.StoredObject;
import org.openpaas.paasta.portal.storage.api.config.SwiftOSConstants.SwiftOSCommonParameter;
import org.openpaas.paasta.portal.storage.api.store.ObjectStorageService;
import org.openpaas.paasta.portal.storage.api.util.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SwiftOSService extends ObjectStorageService<SwiftOSFileInfo> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SwiftOSService.class);

    @Autowired
    private final Container container;

    public SwiftOSService(Container container) {
        this.container = container;
    }


    //@HystrixCommand(commandKey = "putObject")
    @Override
    public SwiftOSFileInfo putObject(final MultipartFile multipartFile) throws IOException {
        Assert.notNull(multipartFile, "MultipartFile instance is empty : " + multipartFile);


        return putObject(multipartFile.getOriginalFilename(), multipartFile.getInputStream(), convertContentType(multipartFile.getOriginalFilename(), multipartFile.getContentType()));
    }

    public String convertContentType(String filename, String content_type) {
        if (filename.indexOf("png") > -1) {
            content_type = "image/png";
        } else if (filename.indexOf("jpg") > -1) {
            content_type = "image/jpg";
        } else if (filename.indexOf("gif") > -1) {
            content_type = "image/gif";
        }
        return content_type;
    }


    //@HystrixCommand(commandKey = "putObject")
    public SwiftOSFileInfo putObject(final String filename, final InputStream contents, final String contentType) {
        Assert.notNull(filename, "Filename instance is empty : " + filename);
        Assert.notNull(contents, "InputStream content instance is empty : " + contents);
        Assert.notNull(contentType, "Content type instance is empty : " + contentType);

        // create StoredObject instance
        final Long currentTimestamp = System.currentTimeMillis();
        final String storedFilename = generateStoredFilename(filename, currentTimestamp);
        final StoredObject object = container.getObject(storedFilename);
        if (null == object) return null;

        LOGGER.debug("StoredObject : {}", object);

        // upload object
        object.uploadObject(contents);
        object.setContentType(contentType);
        LOGGER.debug("Done upload object : {} ({})", storedFilename, object.getPublicURL());

        // after its service uploads object(contents), it sets content type and additional metadata in object storage
        object.setAndDoNotSaveMetadata(SwiftOSCommonParameter.OBJECT_ORIGINAL_FILENAME_METAKEY, filename);
        object.setAndDoNotSaveMetadata(SwiftOSCommonParameter.OBJECT_UPLOAD_TIMESTAMP, currentTimestamp);
        object.setAndDoNotSaveMetadata(SwiftOSCommonParameter.OBJECT_CONTENT_TYPE, contentType);
        object.saveMetadata();

        final SwiftOSFileInfo fileInfo = SwiftOSFileInfo.newInstanceFromStoredObject(object);
        LOGGER.debug("SwiftOSFileInfo : {}", fileInfo);

        return fileInfo;
    }

    //@HystrixCommand(commandKey = "getObject")
    @Override
    public SwiftOSFileInfo getObject(final String filename) throws FileNotFoundException {
        Assert.notNull(filename, "Filename instance is empty : " + filename);

        final StoredObject object = getRawObject(filename);
        final SwiftOSFileInfo fileInfo = SwiftOSFileInfo.newInstanceFromStoredObject(object);
        LOGGER.debug("StoredObject : {} / SwiftOSFileInfo : {}", object, fileInfo);

        return fileInfo;
    }

    //@HystrixCommand(commandKey = "getRawObject")
    public StoredObject getRawObject(final String filename) {
        Assert.notNull(filename, "Filename instance is empty : " + filename);

        final StoredObject object = container.getObject(filename);
        if (false == object.exists()) return null;
        else return object;
    }

    //@HystrixCommand(commandKey = "updateObject")
    @Override
    public SwiftOSFileInfo updateObject(String filename, MultipartFile multipartFile) {
        throw new UnsupportedOperationException("Updating object doesn't support yet.");
    }

    //@HystrixCommand(commandKey = "removeObject")
    @Override
    public boolean removeObject(final String filename) {
        Assert.notNull(filename, "Filename instance is empty : " + filename);

        // reload before delete object
        container.reload();

        final StoredObject object = container.getObject(filename);
        Assert.notNull(object, "StoredObject instance is empty : " + object);

        if (true == object.exists()) {
            LOGGER.debug("Delete object : {} ({})", object.getName(), object.getMetadata(SwiftOSCommonParameter.OBJECT_ORIGINAL_FILENAME_METAKEY));
            object.delete();
        } else {
            LOGGER.warn("Cannot delete non-existed object... : {}", filename);
        }

        // after delete...
        if (true == object.exists()) {
            Exception ex = new IllegalStateException("File(" + filename + ") can't delete...");
            LOGGER.error("Cannot delete...", ex);

            return false;
        }

        return true;
    }

    //@HystrixCommand(commandKey = "listFileURLs")
    public List<String> listFileURLs() {
        final Collection<StoredObject> list = container.list();
        Assert.notNull(list, "StoredObject list is empty : " + list);

        final List<String> urlList = new ArrayList<>();
        for (StoredObject object : list)
            urlList.add(object.getName() + " ( <a>" + object.getPublicURL() + "</a> )");

        return urlList;
    }

    protected final String generateStoredFilename(final String filename, final Long timestamp) {
        Assert.notNull(filename, "Filename is empty");
        Assert.notNull(timestamp, "Timestamp is empty");
        return FilenameUtils.generateStoredFilename(filename, timestamp);
    }

    protected final String getOriginalFilename(final String storedFilename) {
        Assert.notNull(storedFilename, "Stored object's filename instance is empty : " + storedFilename);
        return FilenameUtils.getOriginalFilename(storedFilename);
    }
}
