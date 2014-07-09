
package org.ashanet.typedef;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Project")
public class Project extends ParseObject {
    public Project() {
        super();
    }

    public Project(String name) {
        super();
        setName(name);
    }

    public String getArea() { return getString("area"); }
    public String getChapterOid() { return getString("contact_chapter"); }
    public String getDescription() { return getString("description"); }
    public int getFocusId() { return getInt("focus"); }
    public String getSecondaryFocus() { return getString("secondary_focus"); }
    public int getStatusId() { return getInt("status"); }
    public int getId() { return getInt("project_id"); }
    public int getProjectTypeId() { return getInt("project_type"); }
    public double getTotalFunds() { return getDouble("total_funds"); }

    public String getName() {
        return getString("name");
    }

    public void setName(String value) {
        put("name", value);
    }

    public String getState() {
        return getString("state");
    }

    public void setState(String state) {
        put("state", state);
    }
}
