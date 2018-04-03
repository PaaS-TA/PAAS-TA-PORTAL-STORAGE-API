package org.openpaas.paasta.portal.storage.api.util;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperUtils {
    public static <T> T parseObject(String string, Class<T> clazz) throws IOException {
        assertNotNull(string, clazz);
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(string.getBytes(), clazz);
    }
    
    public static <T> String writeValueAsString(T object) throws IOException {
        assertNotNull(object);
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString( object );
    }
}
