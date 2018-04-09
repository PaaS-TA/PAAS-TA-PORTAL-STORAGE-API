package org.openpaas.paasta.portal.storage.api.util;

import static org.junit.Assert.assertTrue;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openpaas.paasta.portal.storage.api.AbstractTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FilenameUtilsTest extends AbstractTest {
    @Test
    public void testGenerateFilenameAndExtractOriginalFilename() throws Exception {
        final String[] filenames = {
            "test1234.png", "test-1234.png", "test-1234.bak.png",
            ".test1234.png", ".test-1234.png", ".test-1234.bak.png",
            "-test1234.png", "-test-1234.png", "-test-1234.bak.png",
        };
        
        int count = 0;
        int maxCount = filenames.length;
        for (final String filename : filenames) {
            ++count;
            long timestamp = System.currentTimeMillis();
            logger.info( "{} / {} | Filename : {} / Timestamp : {}", count, maxCount, filename, timestamp );
            
            String storedFilename = FilenameUtils.generateStoredFilename( filename, timestamp );
            logger.info( "{} / {} | Stored Filename : {}", count, maxCount, storedFilename  );
            
            String restoreFilename = FilenameUtils.getOriginalFilename( storedFilename );
            logger.info( "{} / {} | Restored original filename : {}", count, maxCount, storedFilename  );
            
            String partialDecodeFilename = FilenameUtils.getStoredFilenameWithDecodedOriginalFilename( storedFilename );
            logger.info( "{} / {} | Decode original filename : {}", partialDecodeFilename);
            
            long restoreTimestamp = FilenameUtils.getUploadTimestamp( storedFilename );
            logger.info( "{} / {} | Restored upload timestamp : {}", count, maxCount, restoreTimestamp  );
            
            assertTrue( restoreFilename.equals( filename ) );
            assertTrue( Long.compare( timestamp, restoreTimestamp ) == 0 );
            assertTrue( partialDecodeFilename.contains( filename ) );
        }
    }
}
