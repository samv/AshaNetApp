
package org.ashanet.typedef;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import java.util.Date;

@ParseClassName("State")
public class State extends ParseObject {
    public State() {
        super();
    }

    public int getId() { return getInt("state_id"); }
    public String getPopulationCount() { return getString("population_count"); }
    public String getName() { return getString("name"); }
    public String getCapital() { return getString("capital"); }
    public Double getLiteracyPercent() { return getDouble("literacy_percent"); }
}
