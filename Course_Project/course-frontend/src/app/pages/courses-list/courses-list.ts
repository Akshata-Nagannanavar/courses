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

  // pagination
  pageSize = 5;
  page = 0;
  totalPages = 0;
  totalElements = 0;

  // filters + search
  searchTerm = '';
  filterBoards: string[] = [];
  filterMediums: string[] = [];
  filterGrades: string[] = [];
  filterSubjects: string[] = [];

  selectedBoard: string = '';
  selectedMediums = new Set<string>();
  selectedGrades = new Set<string>();
  selectedSubjects = new Set<string>();

  // dropdown flags
  openBoardDropdown = false;
  openMediumDropdown = false;
  openGradeDropdown = false;
  openSubjectDropdown = false;

  constructor(private router: Router, private courseService: Course) {}

  ngOnInit(): void {
    // first load all courses (for filters)
    this.courseService.getAllForFilters().subscribe({
      next: (all) => {
        this.buildFiltersFromAll(all || []);
        this.loadPage(0);
      },
      error: (err) => {
        console.warn('Failed to fetch all courses for filters:', err);
        this.buildFiltersFromAll([]);
        this.loadPage(0);
      }
    });
  }

  // ✅ Extract unique filters from all courses
  buildFiltersFromAll(allCourses: CourseModel[]) {
    const boards = new Set<string>();
    const mediums = new Set<string>();
    const grades = new Set<string>();
    const subjects = new Set<string>();

    for (const c of allCourses) {
      if (c.board) boards.add(c.board);
      //(c.medium || []).forEach(m => m && mediums.add(m));
      (c.medium || []).forEach(m => m && mediums.add(m.trim()));

      (c.grade || []).forEach(g => g && grades.add(g));
      (c.subject || []).forEach(s => s && subjects.add(s));
    }

    this.filterBoards = Array.from(boards).sort();
    this.filterMediums = Array.from(mediums).sort();
    this.filterGrades = Array.from(grades).sort();
    this.filterSubjects = Array.from(subjects).sort();
  }

  // ✅ Paginated fetch with filters
  loadPage(pageIndex: number = 0) {
  this.loading = true;
  this.error = null;

  const filters = {
    search: this.searchTerm || undefined,
    board: this.selectedBoard || undefined,
    medium: Array.from(this.selectedMediums),
    grade: Array.from(this.selectedGrades),
    subject: Array.from(this.selectedSubjects)
  };

  // First, fetch ALL courses (not paginated) to apply frontend filtering
  this.courseService.getAllForFilters().subscribe({
    next: (allCourses) => {
      let filtered = allCourses;

      // 🔍 Apply filters manually in frontend
      if (this.selectedBoard)
        filtered = filtered.filter(c => c.board === this.selectedBoard);

      if (this.selectedMediums.size > 0)
        filtered = filtered.filter(c =>
          Array.isArray(c.medium) &&
          c.medium.some((m: string) =>
            Array.from(this.selectedMediums).map(x => x.toLowerCase()).includes(m.toLowerCase())
          )
        );

      if (this.selectedGrades.size > 0)
        filtered = filtered.filter(c =>
          Array.isArray(c.grade) &&
          c.grade.some((g: string) =>
            Array.from(this.selectedGrades).map(x => x.toLowerCase()).includes(g.toLowerCase())
          )
        );

      if (this.selectedSubjects.size > 0)
        filtered = filtered.filter(c =>
          Array.isArray(c.subject) &&
          c.subject.some((s: string) =>
            Array.from(this.selectedSubjects).map(x => x.toLowerCase()).includes(s.toLowerCase())
          )
        );

      if (this.searchTerm)
        filtered = filtered.filter(c =>
          c.name?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
          c.description?.toLowerCase().includes(this.searchTerm.toLowerCase())
        );

      // 🧮 Pagination after filtering
      this.totalElements = filtered.length;
      this.totalPages = Math.ceil(this.totalElements / this.pageSize);
      const start = pageIndex * this.pageSize;
      const end = start + this.pageSize;
      this.courses = filtered.slice(start, end);

      this.page = pageIndex;
      this.loading = false;
    },
    error: (err) => {
      console.error('Error loading courses:', err);
      this.error = 'Failed to load courses';
      this.loading = false;
    }
  });
}


  // 🔍 Search
  onSearchChange(): void {
    this.page = 0;
    this.loadPage(0);
  }

  // 🎯 Dropdown toggles
  toggleDropdown(name: 'board' | 'medium' | 'grade' | 'subject') {
    // close others when one is open
    this.openBoardDropdown = name === 'board' ? !this.openBoardDropdown : false;
    this.openMediumDropdown = name === 'medium' ? !this.openMediumDropdown : false;
    this.openGradeDropdown = name === 'grade' ? !this.openGradeDropdown : false;
    this.openSubjectDropdown = name === 'subject' ? !this.openSubjectDropdown : false;
  }

  // ✅ Board select (single)
  selectBoard(board: string): void {
    this.selectedBoard = this.selectedBoard === board ? '' : board;
    this.openBoardDropdown = false;
    this.page = 0;
    this.loadPage(0);
  }
  toggleSet(set: Set<string>, value: string): void {
  const normalized = value.trim();
  if (set.has(normalized)) set.delete(normalized);
  else set.add(normalized);

  // Close only the corresponding dropdown when updated
  if (set === this.selectedMediums) this.openMediumDropdown = false;
  if (set === this.selectedGrades) this.openGradeDropdown = false;
  if (set === this.selectedSubjects) this.openSubjectDropdown = false;

  this.page = 0;
  this.loadPage(0);
}


  // 🔄 Clear filters
  clearAllFilters(): void {
    this.selectedBoard = '';
    this.selectedMediums.clear();
    this.selectedGrades.clear();
    this.selectedSubjects.clear();
    this.searchTerm = '';
    this.page = 0;
    this.loadPage(0);
  }

  // pagination controls
  prevPage(): void {
    if (this.page > 0) this.loadPage(this.page - 1);
  }

  nextPage(): void {
    if (this.page < this.totalPages - 1) this.loadPage(this.page + 1);
  }

  goToPage(i: number): void {
    if (i >= 0 && i < this.totalPages) this.loadPage(i);
  }

  // navigation
  goToCreateCourse(): void {
    this.router.navigate(['/createCourse']);
  }

  viewCourse(courseId?: string): void {
    if (!courseId) return;
    this.router.navigate(['/course-details', courseId]);
  }

  editCourse(courseId?: string): void {
    if (!courseId) return;
    this.router.navigate(['/editCourse', courseId]);
  }

  deleteCourse(courseId?: string, courseName?: string): void {
    if (!courseId) return;
    const ok = window.confirm(`Delete course "${courseName || ''}"? This cannot be undone.`);
    if (!ok) return;

    this.courseService.deleteCourse(courseId).subscribe({
      next: () => this.loadPage(this.page),
      error: (err) => {
        console.error('Failed to delete', err);
        window.alert('Failed to delete course.');
      }
    });
  }

  // course details navigation
  goToCourseDetails(id: string | undefined): void {
    if (id) this.router.navigate(['/course', id]);
  }
}
