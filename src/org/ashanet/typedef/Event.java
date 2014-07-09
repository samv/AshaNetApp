
package org.ashanet.typedef;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import java.util.Date;

@ParseClassName("Event")
public class Event extends ParseObject {
    public Event() {
        super();
    }

    public String getAddress() { return getString("Address"); }
    public String getDescription() { return getString("Description"); }
    public String getName() { return getString("Name"); }
    public String getTicketSiteUrl() { return getString("Ticket_site_url"); }
    public ParseFile getImageFile() { return getParseFile("Image"); }
    public Date getEventStart() { return getDate("Event_time"); }
}
