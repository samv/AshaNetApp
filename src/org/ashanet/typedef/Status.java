
package org.ashanet.typedef;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import java.util.Date;

@ParseClassName("Status")
public class Status extends ParseObject {
    public Status() {
        super();
    }

    public int getId() { return getInt("status_id"); }
    public String getTitle() { return getString("title"); }
    public String getDescription() { return getString("description"); }
}
