import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Course } from '../../services/course';
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
  courses: CourseModel[] = [];
  loading = false;
  error: string | null = null;

  pageSize = 5;
  page = 0;
  totalPages = 0;
  totalElements = 0;

  searchTerm = '';
  filterBoards: string[] = [];
  filterMediums: string[] = [];
  filterGrades: string[] = [];
  filterSubjects: string[] = [];

  // UI selection state
  selectedBoard: string = '';       // single
  selectedMediums = new Set<string>();
  selectedGrades = new Set<string>();
  selectedSubjects = new Set<string>(); // multi-select

  openBoardDropdown = false;
  openMediumDropdown = false;
  openGradeDropdown = false;
  openSubjectDropdown = false;

  constructor(private router: Router, private courseService: Course) {}

  ngOnInit(): void {
    this.courseService.getAllForFilters().subscribe({
      next: (all) => {
        this.buildFiltersFromAll(all);
        this.loadPage(0);
      },
      error: (err) => {
        console.warn('Failed to fetch all courses for filters:', err);
        this.buildFiltersFromAll([]);
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
      if (c.board) b.add(c.board);      // single
      (c.medium || []).forEach(x => x && m.add(x));
      (c.grade || []).forEach(x => x && g.add(x));
      (c.subject || []).forEach(x => x && s.add(x));
    }

    this.filterBoards = Array.from(b).sort();
    this.filterMediums = Array.from(m).sort();
    this.filterGrades = Array.from(g).sort();
    this.filterSubjects = Array.from(s).sort();
  }

  loadPage(pageIndex: number = 0) {
    this.loading = true;
    this.error = null;

    const filters = {
      search: this.searchTerm || undefined,
      board: this.selectedBoard || undefined, // single
      medium: Array.from(this.selectedMediums),
      grade: Array.from(this.selectedGrades),
      subject: Array.from(this.selectedSubjects)
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

  onSearchChange() {
    this.page = 0;
    this.loadPage(0);
  }

  // Toggle multi-select sets
  toggleSet(set: Set<string>, value: string) {
    if (set.has(value)) set.delete(value);
    else set.add(value);

    this.page = 0;
    this.loadPage(0);
  }

  // Single-select board
  selectBoard(board: string) {
    this.selectedBoard = this.selectedBoard === board ? '' : board;
    this.page = 0;
    this.loadPage(0);
  }

  clearAllFilters() {
    this.selectedBoard = '';
    this.selectedMediums.clear();
    this.selectedGrades.clear();
    this.selectedSubjects.clear();
    this.searchTerm = '';
    this.page = 0;
    this.loadPage(0);
  }

  prevPage() {
    if (this.page > 0) this.loadPage(this.page - 1);
  }

  nextPage() {
    if (this.page < this.totalPages - 1) this.loadPage(this.page + 1);
  }

  goToPage(i: number) {
    if (i >= 0 && i < this.totalPages) this.loadPage(i);
  }

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
        this.loadPage(this.page);
      },
      error: (err) => {
        console.error('Failed to delete', err);
        window.alert('Failed to delete course.');
      }
    });
  }

  firstUnits(c: CourseModel, n = 2) {
    return (c.units || []).slice(0, n);
  }

  toggleDropdown(name: 'board' | 'medium' | 'grade' | 'subject') {
    if (name === 'board') this.openBoardDropdown = !this.openBoardDropdown;
    if (name === 'medium') this.openMediumDropdown = !this.openMediumDropdown;
    if (name === 'grade') this.openGradeDropdown = !this.openGradeDropdown;
    if (name === 'subject') this.openSubjectDropdown = !this.openSubjectDropdown;
  }
}
