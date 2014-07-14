
package org.ashanet.typedef;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import java.util.Date;

@ParseClassName("Chapter")
public class Chapter extends ParseObject {
    public Chapter() {
        super();
    }

    public int getId() { return getInt("chapter_id"); }
    public String getName() { return getString("name"); }
    public int getEstablishedYear() { return getInt("established_year"); }
    public String getChapterUrl() { return getString("chapter_url"); }
}
