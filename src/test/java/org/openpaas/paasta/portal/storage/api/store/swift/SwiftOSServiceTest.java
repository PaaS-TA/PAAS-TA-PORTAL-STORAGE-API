package org.openpaas.paasta.portal.storage.api.store.swift;

import static org.junit.Assert.assertTrue;

import org.javaswift.joss.client.mock.AccountMock;
import org.javaswift.joss.client.mock.ContainerMock;
import org.javaswift.joss.model.Container;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.openpaas.paasta.portal.storage.api.AbstractTest;
//import org.junit.runner.RunWith;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;


//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SwiftOSServiceTest extends AbstractTest {
    @Mock
    private SwiftOSService swiftOSService;
    
    @Mock private AccountMock account; 
    @Mock private Container container;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        
        account = new AccountMock();
        container = new ContainerMock( account, "passta-portal" );        
        swiftOSService = new SwiftOSService( container );
    }
    
    @Test
    public void generateFilenameAndExtractOriginalFilename() throws Exception {
        final String[] filenames = {
            "test1234.png", "test-1234.png", "test-1234.bak.png",
            ".test1234.png", ".test-1234.png", ".test-1234.bak.png",
            "-test1234.png", "-test-1234.png", "-test-1234.bak.png",
        };
        
        for (final String filename : filenames) {
            String storedFilename = swiftOSService.generateStoredFilename( filename );
            String restoreFilename = swiftOSService.getOriginalFilename( storedFilename );
            logger.info( "original filename : {}", filename );
            logger.info( "stored filename : {}", storedFilename );
            logger.info( "restored original filename : {}", restoreFilename );
            
            assertTrue( restoreFilename.equals( filename ) );
        }
    }
}
