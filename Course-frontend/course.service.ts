import { OnInit, Injectable } from '@angular/core';
import { Course } from "../Course";
import { HttpModule, JsonpModule, Http, Response, Headers, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { AppSettings } from '../../app.settings';
import { Trainer } from '../../trainer/Trainer';
import { TargetGroup } from '../../targetGroup/TargetGroup';
import { environment } from '../../../environments/environment';

@Injectable()
export class CourseService implements OnInit
{
  constructor(protected http: Http)
  {
 
  }

  ngOnInit() { }

  getAllCourses(): Observable<Course[]>
  {
    return this.http.get(environment.API_ENDPOINT + 'courses').map((responseData) =>
    {
      return responseData.json();
    });
  }

  getAmountOfCourses(): Observable<number>
  {
    return this.http.get(environment.API_ENDPOINT + 'courses/amount').map((responseData) =>
    {
      return responseData.json();
    });
  }

  getPagesOfCourses(rows: number): Observable<Number[]>
  {
    return this.http.get(environment.API_ENDPOINT + 'courses/amount').map((response) =>
    {
      let amount = Number(response.text());
      let nPages = Math.ceil(amount / rows);
      let pages = [];
      for (var i = 0; i < nPages; i++)
        pages[i] = i + 1;
      return pages;
    });
  }

  getCourseById(id: Number): Observable<Course>
  {
    return this.http.get(environment.API_ENDPOINT + 'courses/' + id).map((responseData) =>
    {
      return responseData.json();
    });
  }

  getCourses(page: number, rows: number): Observable<Course[]>
  {
    return this.http.get(environment.API_ENDPOINT + 'courses?page=' + page + '&count=' + rows).map((responseData) =>
    {
      return responseData.json();
    });
  }

  getListOfTrainersFromCourse(id: number): Observable<Trainer[]>
  {
    return this.http.get(environment.API_ENDPOINT + 'courses/' + id + '/listOfTrainers').map((responseData) =>
    {
      return responseData.json();
    });
  }

  addCourse(course: Course): Observable<Response>
  {
    var headers = new Headers();
    headers.append("Content-Type", 'application/json');
    var requestoptions = new RequestOptions({
      headers: headers,
      withCredentials: true
    })
    return this.http.post(environment.API_ENDPOINT + 'courses/add', JSON.stringify(course), requestoptions);
  }

  updateCourse(course: Course): Observable<Response>
  {
    var headers = new Headers();
    headers.append("Content-Type", 'application/json');
    var requestoptions = new RequestOptions({
      headers: headers,
      withCredentials: true
    })
    return this.http.put(environment.API_ENDPOINT + 'courses/'+course.id, JSON.stringify(course), requestoptions);
  }

  removeCourse(id: number): Observable<Response>
  {
    return this.http.delete(environment.API_ENDPOINT + 'courses/' + id);
  }


  getSeniorsForCourse(courseId: number): Observable<Trainer[]>
  {
    return this.http.get(environment.API_ENDPOINT + 'courses/' + courseId + '/seniors').map((responseData) =>
    {
      return responseData.json();
    });

  }

  existsCourse(name: string): Observable<boolean>
  {
    return this.http.post(environment.API_ENDPOINT + 'courses/exists', name).map((response) =>
    {
      return response.json();

    });;
  }

  getAvailableTrainers(id: number): Observable<Trainer[]>
  {
    return this.http.get(environment.API_ENDPOINT + 'courses/' + id + "/availableTrainers").map((response) =>
    {
      return response.json();
    });
  }

  getAvailableTargetGroups(id: number): Observable<TargetGroup[]>
  {
    return this.http.get(environment.API_ENDPOINT + 'courses/' + id + "/availableTargetGroups").map((response) =>
    {
      return response.json();
    });
  }

}
