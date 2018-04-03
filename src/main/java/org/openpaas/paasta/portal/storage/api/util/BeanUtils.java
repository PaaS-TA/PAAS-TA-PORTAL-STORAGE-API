package org.openpaas.paasta.portal.storage.api.util;

import org.springframework.context.ApplicationContext;

public class BeanUtils {
    @SuppressWarnings( "unchecked" )
    public static <T> T getBean(String beanId) {
        final ApplicationContext appCtx = ApplicationContextProvider.getApplicationContext();
        
        if( null == appCtx ) 
            throw new NullPointerException( "Doesn't initialize application context..." );
        
        if ( null == beanId )
            throw new NullPointerException( "Bean ID cannot null value.");
        else if ( "".equals( beanId.replaceAll( " ", "" ) ))
            throw new NullPointerException( "Bean ID cannot empty string.");
         
        Object beanObj = appCtx.getBean(beanId);
        if (null == beanObj)
            throw new NullPointerException( "Bean is null : " + beanId );
        
        return ((T) beanObj);
    }
}
