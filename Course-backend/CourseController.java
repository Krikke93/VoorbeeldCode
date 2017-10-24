package com.cegeka.courseka.course;

import com.cegeka.courseka.clazz.ClazzRepository;
import com.cegeka.courseka.pagination.Pager;
import com.cegeka.courseka.targetgroup.TargetGroup;
import com.cegeka.courseka.targetgroup.TargetGroupRepository;
import com.cegeka.courseka.trainer.Trainer;
import com.cegeka.courseka.trainer.TrainerRepository;
import com.cegeka.courseka.util.StringTrimmer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/courses")
public class CourseController {

    private Pager pager = new Pager();

    private CourseRepository courseRepository;

    @Autowired
    private ClazzRepository clazzRepository;
    @Autowired
    private TrainerRepository trainerRepository;
    @Autowired
    private TargetGroupRepository targetGroupRepository;

    public CourseController(@Autowired CourseRepository courseRepository)
    {
        this.courseRepository = courseRepository;
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public List<Course> getCoursesFromPage(@RequestParam(name = "page", required = false)Integer page,
                                           @RequestParam(name = "count", required = false)Integer count)
    {
        List<Course> courses = new ArrayList<>();
        if(page != null && page >= 0 && count != null)
            courses = courseRepository.findByActiveTrueOrderByNameAsc(pager.getPageRequest(page,count,"name")).getContent();
        else
            courses = courseRepository.findByActiveTrueOrderByNameAsc();
        return courses;
    }

    @RequestMapping(value = "/amount",  method = RequestMethod.GET)
    public Integer getAmountOfCourses() {
        return (int) courseRepository.count();
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    public Course getCourse(@PathVariable Integer id) {
        return courseRepository.findOne(id);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    public void addCourse(@RequestBody Course course)
    {
        if (course == null)
            throw new NullPointerException("course to be inserted is null");
        course.setActive(true);
        courseRepository.saveAndFlush(course);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON)
    public void editCourse(@RequestBody Course course)
    {
        if(courseRepository.exists(course.getId()));
        {
            if(course.getTrainers() == null)
            {
                Course dbCourse = courseRepository.findOne(course.getId());
                course.getTrainers().addAll(dbCourse.getTrainers());
            }
            if(course.getTrainers().size() == 0)
            {
                Course dbCourse = courseRepository.findOne(course.getId());
                course.getTrainers().addAll(dbCourse.getTrainers());
            }
            courseRepository.saveAndFlush(course);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteCourse(@PathVariable Integer id)
    {
        if(courseRepository.exists(id))
        {
            Course course = courseRepository.findOne(id);
            course.setActive(false);
            courseRepository.saveAndFlush(course);
        }
    }

    @RequestMapping(value = "/{id}/listOfTrainers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<Trainer> getListOfTrainersByCourse(@PathVariable Integer id)
    {
        Course course = courseRepository.findOne(id);
        return course.getTrainers();
    }

    @RequestMapping(value = "/{id}/seniors", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<Trainer> getSeniorsByCourse(@PathVariable Integer id)
    {
        List<Trainer> allTrainers = getListOfTrainersByCourse(id);
        List<Trainer> seniors = new ArrayList<>();
        for(Trainer trainer : allTrainers) {
            Long amountGiven = clazzRepository.getAmountOfCertainCourseGivenByCertainTrainer(trainer.getId(), id);
            if(amountGiven >= 2) seniors.add(trainer);
        }
        return seniors;
    }

    @RequestMapping(value = "/exists", method = RequestMethod.POST)
    public boolean getCourseExists(@RequestBody String name)
    {
        return !courseRepository.findByName(StringTrimmer.removeDoubleSpacesAndSpacesAtBeginningAndEnd(name)).isEmpty();
    }

    @RequestMapping(value = "/{id}/availableTrainers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<Trainer> getTrainersThatACourseDoesNotHave(@PathVariable("id") Integer id)
    {
        Course course = courseRepository.findOne(id);
        List<Integer> ids = new ArrayList<>();
        for(Trainer trainer : course.getTrainers())
            ids.add(trainer.getId());
       if(!ids.isEmpty())
            return trainerRepository.findByActiveTrueAndIdNotIn(ids);
       else return trainerRepository.findByActiveTrueOrderBySurnameAsc();
    }


    @RequestMapping(value = "/{id}/availableTargetGroups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<TargetGroup> getTargetGroupsThatACourseDoesNotHave(@PathVariable("id") Integer id)
    {
        Course course = courseRepository.findOne(id);
        List<Integer> ids = new ArrayList<>();
        for(TargetGroup targetGroup : course.getTargetGroups())
            ids.add(targetGroup.getId());
        if(!ids.isEmpty())
            return targetGroupRepository.findByActiveTrueAndIdNotInOrderByNameAsc(ids);
        else return targetGroupRepository.findByActiveTrueOrderByNameAsc();
    }

}
