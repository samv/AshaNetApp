
package org.ashanet.typedef;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import java.util.Date;

@ParseClassName("FocusType")
public class FocusType extends ParseObject {
    public FocusType() {
        super();
    }

    public int getId() { return getInt("focus_type_id"); }
    public String getTitle() { return getString("title"); }
    public String getDescription() { return getString("description"); }
}
