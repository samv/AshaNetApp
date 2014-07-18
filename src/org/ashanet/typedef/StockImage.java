
package org.ashanet.typedef;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import java.util.Date;

@ParseClassName("StockImage")
public class StockImage extends ParseObject {
    public StockImage() {
        super();
    }

    public String getFilename() { return getString("Filename"); }
    public ParseFile getImageFile() { return getParseFile("Original"); }
}
