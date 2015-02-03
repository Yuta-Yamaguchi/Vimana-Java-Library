package jp.ac.teu.tlab.vimana.object.page;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import jp.ac.teu.tlab.vimana.object.Content;
import static jp.ac.teu.tlab.vimana.base.Engine.staticContentPageMustache;

/**
 *
 * @author Yuta YAMAGUCHI
 */
public class StaticContentPage extends ContentPage {
    
    public StaticContentPage(String path)
            throws IOException {
        
        super.content = new Content(path);
        
        StringWriter stringWriter = new StringWriter();
        staticContentPageMustache.execute(stringWriter, super.content).flush();
        
        super.page = stringWriter.toString();
    }
    
    @Override
    public void executeFlash(PrintWriter writer)
            throws IOException {
        
        staticContentPageMustache.execute(writer, super.content).flush();
    }
}
