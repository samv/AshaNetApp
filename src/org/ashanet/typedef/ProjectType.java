
package org.ashanet.typedef;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import java.util.Date;

@ParseClassName("ProjectType")
public class ProjectType extends ParseObject {
    public ProjectType() {
        super();
    }

    public int getId() { return getInt("project_type_id"); }
    public String getTitle() { return getString("title"); }
    public String getDescription() { return getString("description"); }
}
