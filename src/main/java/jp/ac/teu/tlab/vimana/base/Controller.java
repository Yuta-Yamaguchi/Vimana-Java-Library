package jp.ac.teu.tlab.vimana.base;

import javax.servlet.http.HttpServlet;

/**
 * 
 * @author Yuta YAMAGUCHI
 */
public abstract class Controller {
    private static HttpServlet servlet;
    
    static void setHttpServlet(HttpServlet servlet) {
        Controller.servlet = servlet;
    }
    
    public static String getRealPath(String path) {
        return servlet.getServletContext().getRealPath(path);
    }
}