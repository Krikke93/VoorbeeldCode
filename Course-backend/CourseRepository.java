package com.cegeka.courseka.course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer>
{
    List<Course> findByActiveTrueAndIdNotInOrderByNameAsc(List<Integer> ids);
    List<Course> findByActiveTrueOrderByNameAsc();
    List<Course> findByName(String name);
    @Transactional
    List<Course> removeByName(String name);

    Page<Course> findByActiveTrueOrderByNameAsc(Pageable pageRequest);

    List<Course> findByActiveFalseOrderByNameAsc();
}
