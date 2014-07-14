
package org.ashanet.util;

import com.parse.ParseObject;
import java.util.HashMap;
import java.util.List;
import org.ashanet.typedef.Chapter;
import org.ashanet.typedef.FocusType;
import org.ashanet.typedef.ProjectType;
import org.ashanet.typedef.State;
import org.ashanet.typedef.Status;

public class TypeMaps {
    public HashMap<Integer, Chapter> chapters;
    public HashMap<String, Chapter> chaptersByOid;
    public HashMap<Integer, FocusType> focusTypes;
    public HashMap<Integer, ProjectType> projectTypes;
    public HashMap<Integer, State> states;
    public HashMap<Integer, Status> statuses;

    public TypeMaps() {}

    public Chapter getChapter(String oid) {
        return chaptersByOid.get(oid);
    }
    public ProjectType getProjectType(int projectTypeId) {
        return projectTypes.get(projectTypeId);
    }
    public FocusType getFocusType(int focusTypeId) {
        return focusTypes.get(focusTypeId);
    }

    public void setChapters(List<ParseObject> chapters) {
        this.chapters = new HashMap<Integer, Chapter>();
        this.chaptersByOid = new HashMap<String, Chapter>();
        for (ParseObject po: chapters) {
            Chapter c = (Chapter) po;
            this.chapters.put(c.getId(), c);
            this.chaptersByOid.put(c.getObjectId(), c);
        }
    }
    public void setFocusTypes(List<ParseObject> focusTypes) {
        this.focusTypes = new HashMap<Integer, FocusType>();
        for (ParseObject po: focusTypes) {
            FocusType f = (FocusType) po;
            this.focusTypes.put(f.getId(), f);
        }
    }
    public void setProjectTypes(List<ParseObject> projectTypes) {
        this.projectTypes = new HashMap<Integer, ProjectType>();
        for (ParseObject po: projectTypes) {
            ProjectType pt = (ProjectType) po;
            this.projectTypes.put(pt.getId(), pt);
        }
    }
    public void setStates(List<ParseObject> states) {
        this.states = new HashMap<Integer, State>();
        for (ParseObject po: states) {
            State s = (State) po;
            this.states.put(s.getId(), s);
        }
    }
    public void setStatuses(List<ParseObject> statuses) {
        this.statuses = new HashMap<Integer, Status>();
        for (ParseObject po: statuses) {
            Status s = (Status) po;
            this.statuses.put(s.getId(), s);
        }
    }
}
