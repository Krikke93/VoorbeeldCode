import { Component, OnInit } from '@angular/core';
import { Course } from "../Course";
import { Observable } from 'rxjs/Observable';
import { Router, ActivatedRoute } from '@angular/router';
import { CourseService } from '../course-service/course.service';
import { AppSettings } from '../../app.settings';
import 'rxjs/add/operator/map';

@Component({
  selector: 'course-list',
  templateUrl: './course-list.component.html',
  styleUrls: ['./course-list.component.css'],
  providers: [CourseService]
})
export class CourseListComponent implements OnInit
{
  public allCourses: Observable<Course[]>;
  public courses: Observable<Course[]>;
  public pages: Observable<Number[]>;
  private currentIndex: number;
  public isDeleting: Boolean[];
  public searchResult: Course[];
  public query: string;
  public sortNameAsc: boolean;

  constructor(private courseService: CourseService, private route: ActivatedRoute, private router: Router)
  {

  }


  setPage(index: number)
  {
    this.currentIndex = index;
    this.courses = Observable.create((subscriber) =>
    {
      subscriber.next(this.searchResult.slice(index * AppSettings.ROWS_PER_PAGE, (index + 1) * AppSettings.ROWS_PER_PAGE));
    });
  }

  managePages()
  {
    this.pages = Observable.create((subscriber) => 
    {
      let pageNumbers: number[] = new Array();
      for (var i = 0; i < Math.ceil(this.searchResult.length / AppSettings.ROWS_PER_PAGE); i++) {
        pageNumbers.push(i + 1);
      }
      subscriber.next(pageNumbers);
    }
    );
  }

  init(): void
  {
    this.allCourses = this.courseService.getAllCourses();
    this.allCourses.subscribe(data => 
    {
      this.isDeleting = new Array(data.length + 1);
      this.searchResult = data;
      this.pages = this.courseService.getPagesOfCourses(AppSettings.ROWS_PER_PAGE);
      this.setPage(0);
    });
  }

  ngOnInit()
  {
    this.query = "";
    this.sortNameAsc = true;

    this.init();
  }
  onKey(event: any)
  {
    this.query = event.target.value;
    this.createSearchResult();
  }

  startDelete(index: number)
  {
    this.isDeleting[index] = true;
  }

  cancel(index: number)
  {
    this.isDeleting[index] = false;
  }

  confirmDelete(id: number)
  {
    this.courseService.removeCourse(id).subscribe((response) =>
    {
      this.init();
    });
  }

  createSearchResult()
  {
    this.allCourses.subscribe(courses =>
    {
      this.searchResult = courses;

      this.filterAllCourses();

    });
  }

  filterAllCourses()
  {
    this.filterBySearchQuery();
    this.sortSearchResult();
    this.managePages();
    this.currentIndex = 0;
    this.setPage(this.currentIndex);
  }

  switchSortNameAsc()
  {
    if(this.sortNameAsc == null) this.sortNameAsc = true;
    else this.sortNameAsc = !this.sortNameAsc;
    this.createSearchResult();
  }

  filterBySearchQuery()
  {

    if (this.query != "") {
      for (var i = 0; i < this.searchResult.length; i++) {
        var course = this.searchResult[i];
        if (course.name.toUpperCase().search(this.query.toUpperCase()) == -1) {
          let mustSplice : boolean = true;
          for(let targetGroup of course.targetGroups)
          {
            if(targetGroup.name.toUpperCase().search(this.query.toUpperCase()) != -1) mustSplice = false;
          }
          if(mustSplice)
          {
            this.searchResult.splice(i, 1);
            i--;
          }
        }
      }
    }
  }

  sortSearchResult()
  {
    if (this.sortNameAsc != null) {
      this.searchResult.sort((course1, course2) =>
      {
        if (course1.name.toUpperCase() > course2.name.toUpperCase()) return (this.sortNameAsc ? 1 : -1);
        else if (course1.name.toUpperCase() < course2.name.toUpperCase()) return (this.sortNameAsc ? -1 : 1);
        else return 0;
      });
    }
  }

}
