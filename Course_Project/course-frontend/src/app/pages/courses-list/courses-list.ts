import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Course } from '../../services/course'; // adjust path if needed
import type { Course as CourseModel } from '../../services/course';

@Component({
  selector: 'app-courses-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './courses-list.html',
  styleUrls: ['./courses-list.scss'],
  encapsulation: ViewEncapsulation.Emulated
})
export class CoursesListComponent implements OnInit {
  // listing
  courses: CourseModel[] = [];
  loading = false;
  error: string | null = null;

  // server-side paging
  pageSize = 5;
  page = 0; // zero-based
  totalPages = 0;
  totalElements = 0;

  // search and filters (sent to server)
  searchTerm = '';
  filterBoards: string[] = [];
  filterMediums: string[] = [];
  filterGrades: string[] = [];
  filterSubjects: string[] = [];

  // UI selection state
  selectedBoards = new Set<string>();
  selectedMediums = new Set<string>();
  selectedGrades = new Set<string>();
  selectedSubject: string | null = null; // single-select

  // UI dropdown open states (for simple toggling)
  openBoardDropdown = false;
  openMediumDropdown = false;
  openGradeDropdown = false;
  openSubjectDropdown = false;

  constructor(private router: Router, private courseService: Course) {}

  ngOnInit(): void {
    // Build filter lists using a full fetch (backend must allow a large page size or provide an /all endpoint)
    this.courseService.getAllForFilters().subscribe({
      next: (all) => {
        this.buildFiltersFromAll(all);
        // initial load (server-side)
        this.loadPage(0);
      },
      error: (err) => {
        // if fetching all fails, still proceed to paginated load
        console.warn('Failed to fetch all courses for filters:', err);
        this.buildFiltersFromAll([]); // empty
        this.loadPage(0);
      }
    });
  }

  buildFiltersFromAll(allCourses: CourseModel[]) {
    const b = new Set<string>();
    const m = new Set<string>();
    const g = new Set<string>();
    const s = new Set<string>();

    for (const c of allCourses || []) {
      (c.board || []).forEach(x => x && b.add(x));
      (c.medium || []).forEach(x => x && m.add(x));
      (c.grade || []).forEach(x => x && g.add(x));
      if (c.subject) s.add(c.subject);
    }

    this.filterBoards = Array.from(b).sort();
    this.filterMediums = Array.from(m).sort();
    this.filterGrades = Array.from(g).sort();
    this.filterSubjects = Array.from(s).sort();
  }

  // Compose filter object and call server for the given page
  loadPage(pageIndex: number = 0) {
    this.loading = true;
    this.error = null;
    const filters = {
      search: this.searchTerm || undefined,
      board: Array.from(this.selectedBoards),
      medium: Array.from(this.selectedMediums),
      grade: Array.from(this.selectedGrades),
      subject: this.selectedSubject || undefined
    };

    this.courseService.getAllCourses(pageIndex, this.pageSize, filters).subscribe({
      next: (res) => {
        this.courses = res.data || [];
        this.page = res.currentPage ?? pageIndex;
        this.totalPages = res.totalPages ?? 0;
        this.totalElements = res.totalElements ?? 0;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading paginated courses:', err);
        this.error = 'Failed to load courses';
        this.loading = false;
      }
    });
  }

  // Called when user changes search text (debounce could be added)
  onSearchChange() {
    this.page = 0;
    this.loadPage(0);
  }

  // Toggle selection helpers
  toggleSet(set: Set<string>, value: string) {
    if (set.has(value)) set.delete(value);
    else set.add(value);

    // when filters change, reset to page 0 and reload
    this.page = 0;
    this.loadPage(0);
  }

  // Subject is single-select
  selectSubject(subject: string | null) {
    this.selectedSubject = subject;
    this.page = 0;
    this.loadPage(0);
  }

  clearAllFilters() {
    this.selectedBoards.clear();
    this.selectedMediums.clear();
    this.selectedGrades.clear();
    this.selectedSubject = null;
    this.searchTerm = '';
    this.page = 0;
    this.loadPage(0);
  }

  // Pagination controls
  prevPage() {
    if (this.page > 0) this.loadPage(this.page - 1);
  }

  nextPage() {
    if (this.page < this.totalPages - 1) this.loadPage(this.page + 1);
  }

  goToPage(i: number) {
    if (i >= 0 && i < this.totalPages) this.loadPage(i);
  }

  // Navigation & actions
  goToCreateCourse() {
    this.router.navigate(['/createCourse']);
  }

  editCourse(courseId?: string) {
    if (!courseId) return;
    this.router.navigate(['/editCourse', courseId]);
  }

  deleteCourse(courseId?: string, courseName?: string) {
    if (!courseId) return;
    const ok = window.confirm(`Delete course "${courseName || ''}"? This cannot be undone.`);
    if (!ok) return;
    this.courseService.deleteCourse(courseId).subscribe({
      next: (msg) => {
        window.alert(typeof msg === 'string' ? msg : 'Course deleted');
        // reload current page (server-side)
        this.loadPage(this.page);
      },
      error: (err) => {
        console.error('Failed to delete', err);
        window.alert('Failed to delete course.');
      }
    });
  }

  // small helper for truncating displayed units
  firstUnits(c: CourseModel, n = 2) {
    return (c.units || []).slice(0, n);
  }

  // UI helpers for dropdown toggles (close on outside clicks can be added)
  toggleDropdown(name: 'board' | 'medium' | 'grade' | 'subject') {
    if (name === 'board') this.openBoardDropdown = !this.openBoardDropdown;
    if (name === 'medium') this.openMediumDropdown = !this.openMediumDropdown;
    if (name === 'grade') this.openGradeDropdown = !this.openGradeDropdown;
    if (name === 'subject') this.openSubjectDropdown = !this.openSubjectDropdown;
  }
}
