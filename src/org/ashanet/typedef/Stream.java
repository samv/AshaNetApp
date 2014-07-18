
package org.ashanet.typedef;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import java.util.Date;

// Curated item for the main feed view

@ParseClassName("Stream")
public class Stream extends ParseObject {
    public Stream() {
        super();
    }

    public String getTitle() { return getString("Title"); }
    public String getSubtitle() { return getString("Subtitle"); }
    public String getDescription() { return getString("Description"); }
    public String getColorName() { return getString("ColorName"); }

    public Date getPublishDate() { return getDate("publishAt"); }
    public Date getExpiryDate() { return getDate("expiresAt"); }

    public String getChapterId() { return getString("ChapterId"); }
    public String getProjectId() { return getString("ProjectId"); }
    public String getProjectTypeId() { return getString("ProjectTypeId"); }
    public String getEventId() { return getString("EventId"); }
    public String getImageId() { return getString("ImageId"); }
}
