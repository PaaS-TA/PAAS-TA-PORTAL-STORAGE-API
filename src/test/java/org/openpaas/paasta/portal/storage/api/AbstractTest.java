package org.openpaas.paasta.portal.storage.api;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AbstractTest {
    protected static Logger logger = LoggerFactory.getLogger( AbstractTest.class );
    
    @Before
    public void setUp() throws Exception {
        
        // Last action
        logger.info( "Done test set up." );
    }
    
    @After
    public void tearDown() throws Exception {
        
        // Last action
        logger.info( "Done test tear down." );
    }
    
    @Test
    public void test() {
        
    }
}
