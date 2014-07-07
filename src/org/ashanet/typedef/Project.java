
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

    public int getType() {
        return getInt("type");
    }

    public void setType(int type) {
        put("type_id", type);
    }

}
