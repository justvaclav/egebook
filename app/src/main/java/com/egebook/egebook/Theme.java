package com.egebook.egebook;

import java.util.ArrayList;
import java.util.Map;

public class Theme {
    Map<String, Object> courseInfo;
    ArrayList<Map<String, Object>> lessonInfo;

    public Theme(Map<String, Object> courseInfo, ArrayList<Map<String, Object>> lessonInfo) {
        this.courseInfo = courseInfo;
        this.lessonInfo = lessonInfo;
    }

    public Theme() {}

    public Map<String, Object> getCourseInfo() {
        return courseInfo;
    }

    public void setCourseInfo(Map<String, Object> courseInfo) {
        this.courseInfo = courseInfo;
    }

    public ArrayList<Map<String, Object>> getLessonInfo() {
        return lessonInfo;
    }

    public void setLessonInfo(ArrayList<Map<String, Object>> lessonInfo) {
        this.lessonInfo = lessonInfo;
    }
}