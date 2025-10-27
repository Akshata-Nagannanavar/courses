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
  board: string[];
  medium: string[];
  grade: string[];
  subject: string;
  units?: Unit[];
}

export interface CoursePage {
  data: Course[];
  totalPages: number;
  totalElements: number;
  currentPage: number;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class Course {
  private baseUrl = 'http://localhost:8080/api/courses';

  constructor(private http: HttpClient) {}

  /** Server-side paginated fetch with filters */
  getAllCourses(
    page: number = 0,
    size: number = 10,
    filters: {
      search?: string;
      board?: string[];   // multiple allowed
      medium?: string[];  // multiple allowed
      grade?: string[];   // multiple allowed
      subject?: string;   // single
    } = {}
  ): Observable<CoursePage> {
    let params = new HttpParams()
      .set('page', String(page))
      .set('size', String(size))
      .set('search', filters.search || '')
      .set('orderBy', 'name')
      .set('direction', 'asc');

    // For backend that expects comma-separated multiple values:
    if (filters.board && filters.board.length) params = params.set('board', filters.board.join(','));
    if (filters.medium && filters.medium.length) params = params.set('medium', filters.medium.join(','));
    if (filters.grade && filters.grade.length) params = params.set('grade', filters.grade.join(','));
    if (filters.subject) params = params.set('subject', filters.subject);

    return this.http.get<any>(`${this.baseUrl}`, { params }).pipe(
      map(response => ({
        data: response?.result?.data || [],
        totalPages: response?.result?.totalPages ?? 0,
        totalElements: response?.result?.totalElements ?? 0,
        currentPage: response?.result?.currentPage ?? 0,
        message: response?.result?.message || 'Courses fetched successfully'
      }))
    );
  }

  /**
   * Fetch all courses (single call) to build filter lists.
   * NOTE: this asks backend for a very large page size; backend must allow this or provide an /all endpoint.
   * If your backend exposes an explicit endpoint for all courses, replace this URL accordingly.
   */
  getAllForFilters(): Observable<Course[]> {
    const params = new HttpParams().set('page', '0').set('size', String(1000000));
    return this.http.get<any>(`${this.baseUrl}`, { params }).pipe(
      map(resp => resp?.result?.data || [])
    );
  }

  createCourse(course: Course) {
    return this.http.post<any>(`${this.baseUrl}`, course).pipe(map(r => r?.result?.data || null));
  }

  getCourseById(id: string) {
    return this.http.get<any>(`${this.baseUrl}/${id}`).pipe(map(r => r?.result?.data || null));
  }

  updateCourse(id: string, course: Course) {
    return this.http.put<any>(`${this.baseUrl}/${id}`, course).pipe(map(r => r?.result?.data || null));
  }

  patchCourse(id: string, updates: any) {
    return this.http.patch<any>(`${this.baseUrl}/${id}`, updates).pipe(map(r => r?.result?.data || null));
  }

  deleteCourse(id: string) {
    return this.http.delete<any>(`${this.baseUrl}/${id}`).pipe(map(r => r?.result?.message || 'Course deleted'));
  }
}
