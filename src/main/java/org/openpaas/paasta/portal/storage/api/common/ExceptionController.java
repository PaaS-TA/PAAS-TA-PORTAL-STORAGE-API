package org.openpaas.paasta.portal.storage.api.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingDeque;

import javax.annotation.PostConstruct;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.openpaas.paasta.portal.storage.api.common.ExceptionService.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exception Handler for unexpected situation
 * 
 * @author hgcho (Hyungu Cho)
 * @version 
 * @since 2018. 4. 2.
 *
 */
@ControllerAdvice
@RestController
public class ExceptionController {
    @Value( "${paasta.portal.storage.api.common.countoflastestexception:10}" )
    private int countOfLastestExceptions;
    
    private static final String EXCEPTION_MESSAGE_HEADER = "Occured unexpected exception...";

    @Autowired 
    private ExceptionService exceptionService;
    
    private LinkedBlockingDeque<ExceptionInfo> lastExceptions = null;

    @PostConstruct
    public void initialize() {
        lastExceptions = new LinkedBlockingDeque<>( this.countOfLastestExceptions );
    }

    @ResponseStatus( HttpStatus.INTERNAL_SERVER_ERROR )
    @ExceptionHandler( Throwable.class )
    public String handleException( final HttpServletRequest request, final Throwable ex ) {
        if ( lastExceptions.size() >= countOfLastestExceptions )
            lastExceptions.removeLast();
        
        final ExceptionInfo exInfo = ExceptionInfo.get( ex );
        lastExceptions.push( exInfo );
        exceptionService.log( EXCEPTION_MESSAGE_HEADER, ex );

        // debugging info
        exceptionService.log( Level.DEBUG, "remote host: {}({}) / remote port: {}", 
            request.getRemoteHost(), request.getRemoteAddr(), request.getRemotePort());
        
        final StringBuilder sb = new StringBuilder(EXCEPTION_MESSAGE_HEADER);
        sb.append( '\n' ).append( exInfo.getStackTraceString() ).append( '\n' );
        
        return sb.toString();
    }
    
    @GetMapping( "/errors" )
    public String printLastestExceptions() {
        StringBuilder sb = new StringBuilder();
        
        if (lastExceptions.size() <= 0) {
            sb.append( "<p>" ).append( "No error." ).append( "</p>" );
        } else {
            Iterator<ExceptionInfo> iterator = lastExceptions.iterator();
            
            int count = 0;
            while (iterator.hasNext()) {
                final ExceptionInfo cursor = iterator.next();
                sb.append( "<p><pre>" );
                sb.append( "Exception(" ).append( ++count ).append( '/' ).append( countOfLastestExceptions ).append( ") - " )
                    .append( "Creation time : " ).append( cursor.timestampString ).append( '\n' )
                    .append( cursor.getStackTraceString() ).append( "\n============================\n" );
                sb.append( "</pre></p>" );
            }
        }
        
        return sb.toString();
    }
    
    private static class ExceptionInfo {
        private static final SimpleDateFormat defaultDateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss", Locale.KOREA );
        
        public final Throwable exception;
        public final String timestampString;
        
        private ExceptionInfo(final Throwable exception) {
            this.exception = exception;
            this.timestampString = defaultDateFormat.format( new Date( System.currentTimeMillis() ) );
        }
        
        public static ExceptionInfo get(final Throwable exception) {
            return new ExceptionInfo( exception );
        }
        
        public String getStackTraceString() {
            final StringWriter writer = new StringWriter();
            final PrintWriter printWriter = new PrintWriter( writer );
            exception.printStackTrace( printWriter );
            printWriter.println();
            printWriter.flush();
            
            return writer.toString();
        }
    }
}
