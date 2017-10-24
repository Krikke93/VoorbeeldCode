package com.cegeka.courseka.course;


import com.cegeka.courseka.targetgroup.TargetGroup;
import com.cegeka.courseka.trainer.Trainer;
import com.cegeka.courseka.util.StringTrimmer;
import com.fasterxml.jackson.annotation.*;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="COURSE")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Course implements Serializable
{
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "MIN_TRAINEES")
    private int min_trainees;

    @Column(name = "MAX_TRAINEES")
    private int max_trainees;

    @Column(name = "COURSE_DETAILS")
    private String course_details;

    @Column(name = "ACTIVE")
    private Boolean active;

    @ElementCollection
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JoinTable(
            name="COURSE_TRAINERS",
            joinColumns=@JoinColumn(name="ID_COURSE", referencedColumnName="ID"),
            inverseJoinColumns=@JoinColumn(name="ID_TRAINER", referencedColumnName="ID"))
    private List<Trainer> trainers;

    @ElementCollection
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name="COURSE_TARGETGROUPS",
            joinColumns=@JoinColumn(name="ID_COURSE", referencedColumnName="ID"),
            inverseJoinColumns=@JoinColumn(name="ID_TARGETGROUP", referencedColumnName="ID"))
    private List<TargetGroup> targetGroups;


    private Course(){
        trainers = new ArrayList<>();
    }


    public Course(@NotNull String name)
    {
        this();
        setName(name);
    }

    public Course(@NotNull String name, @NotNull int min_trainees, @NotNull int max_trainees)
    {
        this(name);
        setMin_trainees(min_trainees);
        setMax_trainees(max_trainees);
    }

    public Course(@NotNull String name, @NotNull Integer min_trainees, @NotNull Integer max_trainees,
                   Boolean active) {
        this(name, min_trainees, max_trainees);
        setActive(active);
    }
    public Course(@NotNull Integer id,@NotNull String name, @NotNull Integer min_trainees, @NotNull Integer max_trainees,
                   Boolean active, String course_details) {
        this(name,min_trainees,max_trainees,active);
        setCourse_details(course_details);

    }


    public Integer getId()
    {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = StringTrimmer.removeDoubleSpacesAndSpacesAtBeginningAndEnd(name);
    }

    public int getMin_trainees() {
        return min_trainees;
    }

    public void setMin_trainees(int min_trainees) {
        this.min_trainees = min_trainees;
    }

    public int getMax_trainees() {
        return max_trainees;
    }

    public void setMax_trainees(int max_trainees) {
        this.max_trainees = max_trainees;
    }

    public String getCourse_details() {
        return course_details;
    }

    public void setCourse_details(String course_details) {
        this.course_details = course_details;
    }

    public Boolean getActive() { return active; }

    public void setActive(Boolean active) { this.active = active; }

    @Override
    public boolean equals(Object other)
    {
        if(other instanceof Course)
        {
            Course otherCourse = (Course)other;
            return otherCourse.getName().equals(getName());
        }
        return false;
    }

    public List<Trainer> getTrainers() {
        return trainers;
    }

    public void setTrainers(@NotBlank List<Trainer> trainers) {
        this.trainers = trainers;
    }

    @Override
    public String toString()
    {
        return "name: " + name.toString() + ", id: " + id + ", trainers: " + trainers.toString();
    }

    public List<TargetGroup> getTargetGroups() {
        return targetGroups;
    }

    public void setTargetGroups(List<TargetGroup> targetGroups) {
        this.targetGroups = targetGroups;
    }
}
