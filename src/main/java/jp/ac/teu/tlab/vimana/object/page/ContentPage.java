package jp.ac.teu.tlab.vimana.object.page;

import java.io.IOException;
import java.io.PrintWriter;
import jp.ac.teu.tlab.vimana.object.Content;

/**
 *
 * @author Yuta YAMAGUCHI
 */
public abstract class ContentPage {
    protected Content content;
    public String page = ""; // Mustache's Access field

    public abstract void executeFlash(PrintWriter writer) throws IOException;
    
    public Content getContent() {
        return content;
    }
    
    @Override
    public String toString(){
        return page;
    }
}
