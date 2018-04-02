package org.openpaas.paasta.portal.storage.api.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Exception Service
 * 
 * @author hgcho (Hyungu Cho)
 * @version 
 * @since 2018. 4. 2.
 *
 */
@Service
public class ExceptionService {
    protected static Logger logger = LoggerFactory.getLogger( ExceptionService.class );

    public static enum Level {
        TRACE, DEBUG, INFO, WARN, ERROR
    }

    public void log( final Level level, final String message, final Throwable throwableObj ) {
        switch ( level ) {
        case TRACE:
            logger.trace( message, throwableObj );
            break;
        case DEBUG:
            logger.debug( message, throwableObj );
            break;
        case WARN:
            logger.warn( message, throwableObj );
            break;
        case ERROR:
            logger.error(message, throwableObj );
            break;
        default:
        case INFO:
            logger.info( message, throwableObj );
            break;
        }
    }

    public void log( final String message, final Throwable throwableObj ) {
        log( Level.ERROR, message, throwableObj );
    }
    
    public void log( final Level level, final String message, final Object... objs) {
        switch ( level ) {
        case TRACE:
            logger.trace( message, objs );
            break;
        case DEBUG:
            logger.debug( message, objs );
            break;
        case WARN:
            logger.warn( message, objs );
            break;
        case ERROR:
            logger.error(message, objs );
            break;
        default:
        case INFO:
            logger.info( message, objs );
            break;
        }
    }
}
