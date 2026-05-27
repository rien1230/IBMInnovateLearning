package model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
@Entity
public class Course {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long ID;
    private String courseName;
    private String tags;
    private String duration;
    private String languages;

    public Long getID(){
        return this.ID;
    }

    public void setID(Long id){
        this.ID= id;
    }

    public String getCourseName(){
        return this.courseName;
    }

    public void setCourseName(String courseName){
        this.courseName= courseName;
    }
    public String getTags(){
        return this.tags;
    }

    public void setTags(String tags){
        this.tags= tags;
    }

    public String getDuration(){
        return this.duration= duration;
    }

    public void setDuration(String duration){
        this.duration= duration;
    }

    public String getLanguages(){
        return this.languages;
    }

    public void setLanguages(String languages){
        this.languages= languages;
    }

    @Override
    public String toString() {
        return "Course{" +
                "ID=" + ID +
                ", courseName='" + courseName + '\'' +
                ", tags='" + tags + '\'' +
                ", duration='" + duration + '\'' +
                ", languages='" + languages + '\'' +
                '}';
    }
}

