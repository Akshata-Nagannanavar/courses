import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';


// Model Interfaces

export interface Unit {
  id?: string;
  title: string;
  content: string;
}

export interface Course {
  id?: string;
  name: string;
  description: string;
  board: string;
  medium: string[];
  grade: string[];
  subject: string[];
  units?: Unit[];
}

export interface CoursePage {
  data: Course[];
  totalPages: number;
  totalElements: number;
  currentPage: number;
  message: string;
}


// Injectable Service

@Injectable({
  providedIn: 'root'
})
export class Course {
  private baseUrl = '/api/courses';
  private baseMetaUrl = '/api/meta';

  constructor(private http: HttpClient) {}

  //  Get paginated & filtered list of courses
  getAllCourses(
    page: number = 0,
    size: number = 10,
    filters: {
      search?: string;
      board?: string;
      medium?: string[];
      grade?: string[];
      subject?: string[];
    } = {}
  ): Observable<CoursePage> {
    let params = new HttpParams()
      .set('page', String(page))
      .set('size', String(size))
      .set('search', filters.search || '')
      .set('orderBy', 'name')
      .set('direction', 'asc');

    if (filters.board) params = params.set('board', filters.board);
    if (filters.medium?.length) params = params.set('medium', filters.medium.join(','));
    if (filters.grade?.length) params = params.set('grade', filters.grade.join(','));
    if (filters.subject?.length) params = params.set('subject', filters.subject.join(','));

    return this.http.get<any>(this.baseUrl, { params }).pipe(
      map(response => ({
        data: response?.result?.data || [],
        totalPages: response?.result?.totalPages ?? 0,
        totalElements: response?.result?.totalElements ?? 0,
        currentPage: response?.result?.currentPage ?? 0,
        message: response?.result?.message || 'Courses fetched successfully'
      }))
    );
  }

  //  Fetch all courses (no pagination, for dropdowns)
  getAllForFilters(): Observable<Course[]> {
    const params = new HttpParams().set('page', '0').set('size', String(1000000));
    return this.http.get<any>(this.baseUrl, { params }).pipe(
      map(resp => resp?.result?.data || [])
    );
  }

  //  CRUD for Courses
  createCourse(course: Course): Observable<Course> {
    return this.http.post<any>(this.baseUrl, course).pipe(map(r => r?.result?.data || null));
  }

  getCourseById(id: string): Observable<Course> {
    return this.http.get<any>(`${this.baseUrl}/${id}`).pipe(map(r => r?.result?.data || null));
  }

  updateCourse(id: string, course: Course): Observable<Course> {
    return this.http.put<any>(`${this.baseUrl}/${id}`, course).pipe(map(r => r?.result?.data || null));
  }

  patchCourse(id: string, updates: any): Observable<Course> {
    return this.http.patch<any>(`${this.baseUrl}/${id}`, updates).pipe(map(r => r?.result?.data || null));
  }

  deleteCourse(id: string): Observable<string> {
    return this.http.delete<any>(`${this.baseUrl}/${id}`).pipe(map(r => r?.result?.message || 'Course deleted'));
  }


  //  Units CRUD (per Course)


  /** Add Unit to a Course */
  addUnitToCourse(courseId: string, unit: Unit): Observable<Unit> {
    return this.http.post<any>(`${this.baseUrl}/${courseId}/units`, unit).pipe(
      map(r => r?.result?.data || null)
    );
  }

  /** Get Units for a Course */
  getUnitsForCourse(courseId: string, page: number = 0, size: number = 10): Observable<Unit[]> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<any>(`${this.baseUrl}/${courseId}/units`, { params }).pipe(
      map(r => r?.result?.data || [])
    );
  }

  /** Update a Unit */
  updateUnit(courseId: string, unitId: string, unit: Unit): Observable<Unit> {
    return this.http.put<any>(`${this.baseUrl}/${courseId}/units/${unitId}`, unit).pipe(
      map(r => r?.result?.data || null)
    );
  }

  /** Patch Unit */
  patchUnit(courseId: string, unitId: string, updates: any): Observable<Unit> {
    return this.http.patch<any>(`${this.baseUrl}/${courseId}/units/${unitId}`, updates).pipe(
      map(r => r?.result?.data || null)
    );
  }

  /** Delete Unit */
  deleteUnit(courseId: string, unitId: string): Observable<string> {
    return this.http.delete<any>(`${this.baseUrl}/${courseId}/units/${unitId}`).pipe(
      map(r => r?.result?.message || 'Unit deleted')
    );
  }

  /** - Dropdown Meta Data - */
  getBoards(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseMetaUrl}/boards`);
  }

  getMediums(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseMetaUrl}/mediums`);
  }

  getGrades(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseMetaUrl}/grades`);
  }

  getSubjects(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseMetaUrl}/subjects`);
  }
}
