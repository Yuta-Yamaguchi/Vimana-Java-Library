package jp.ac.teu.tlab.vimana.base;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Yuta YAMAGUCHI
 */
public abstract class GlobalSettings {
    
    public void onStart() {
        
    }
    
    public void onError(HttpServletResponse response, Throwable ex)
            throws IOException {
        
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    
    public void onNotFoundError(HttpServletResponse response)
            throws IOException {
        
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
    
    public void onBadRequestError(HttpServletResponse response)
            throws IOException {
        
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
}