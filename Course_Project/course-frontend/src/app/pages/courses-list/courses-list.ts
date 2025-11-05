import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Course } from '../../services/course';
import type { Course as CourseModel } from '../../services/course';
import { EnumsResponse } from '../../services/course';

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
  pageSize = 6;
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
    // Fetch enums for dropdown filters
    this.courseService.getEnums().subscribe({
      next: (data: EnumsResponse) => {
        this.filterBoards = data.boards || [];
        this.filterSubjects = data.subjects || [];
        this.filterMediums = data.mediums || [];
        this.filterGrades = data.grades || [];
      },
      error: (err) => {
        console.error('Failed to fetch enums:', err);
        this.filterBoards = [];
        this.filterSubjects = [];
        this.filterMediums = [];
        this.filterGrades = [];
      }
    });

    // Load all courses for pagination & search
    this.loadPage(0);
  }

  // Paginated fetch with filters
  loadPage(pageIndex: number = 0) {
    this.loading = true;
    this.error = null;

    this.courseService.getAllForFilters().subscribe({
      next: (allCourses) => {
        let filtered = allCourses;

        // Apply board filter
        if (this.selectedBoard) {
          filtered = filtered.filter(c => c.board === this.selectedBoard);
        }

        // Apply medium filter
        if (this.selectedMediums.size > 0) {
          filtered = filtered.filter(c =>
            Array.isArray(c.medium) &&
            c.medium.some(m =>
              Array.from(this.selectedMediums).map(x => x.toLowerCase()).includes(m.toLowerCase())
            )
          );
        }

        // Apply grade filter
        if (this.selectedGrades.size > 0) {
          filtered = filtered.filter(c =>
            Array.isArray(c.grade) &&
            c.grade.some(g =>
              Array.from(this.selectedGrades).map(x => x.toLowerCase()).includes(g.toLowerCase())
            )
          );
        }

        // Apply subject filter
        if (this.selectedSubjects.size > 0) {
          filtered = filtered.filter(c =>
            Array.isArray(c.subject) &&
            c.subject.some(s =>
              Array.from(this.selectedSubjects).map(x => x.toLowerCase()).includes(s.toLowerCase())
            )
          );
        }

        // Apply search
        if (this.searchTerm) {
          const term = this.searchTerm.toLowerCase();
          filtered = filtered.filter(c =>
            c.name?.toLowerCase().includes(term) ||
            c.description?.toLowerCase().includes(term)
          );
        }

        // Pagination
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

  // Search
  onSearchChange(): void {
    this.page = 0;
    this.loadPage(0);
  }

  // Temporary arrays for multi-select ngModel binding
selectedMediumsArray: string[] = [];
selectedGradesArray: string[] = [];
selectedSubjectsArray: string[] = [];

onMediumsChange() {
  this.selectedMediums = new Set(this.selectedMediumsArray);
  this.page = 0;
  this.loadPage(0);
}

onGradesChange() {
  this.selectedGrades = new Set(this.selectedGradesArray);
  this.page = 0;
  this.loadPage(0);
}

onSubjectsChange() {
  this.selectedSubjects = new Set(this.selectedSubjectsArray);
  this.page = 0;
  this.loadPage(0);
}
  toArray(set: Set<string>): string[] {
    return Array.from(set);
  }
  // Dropdown toggles
  toggleDropdown(name: 'board' | 'medium' | 'grade' | 'subject') {
    this.openBoardDropdown = name === 'board' ? !this.openBoardDropdown : false;
    this.openMediumDropdown = name === 'medium' ? !this.openMediumDropdown : false;
    this.openGradeDropdown = name === 'grade' ? !this.openGradeDropdown : false;
    this.openSubjectDropdown = name === 'subject' ? !this.openSubjectDropdown : false;
  }

  // Board select (single)
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

    // Close only the corresponding dropdown
    if (set === this.selectedMediums) this.openMediumDropdown = false;
    if (set === this.selectedGrades) this.openGradeDropdown = false;
    if (set === this.selectedSubjects) this.openSubjectDropdown = false;

    this.page = 0;
    this.loadPage(0);
  }

  // Clear all filters
  clearAllFilters(): void {
    this.selectedBoard = '';
    this.selectedMediums.clear();
    this.selectedGrades.clear();
    this.selectedSubjects.clear();
    this.searchTerm = '';
    this.page = 0;
    this.loadPage(0);
  }

  // Pagination controls
  prevPage(): void {
    if (this.page > 0) this.loadPage(this.page - 1);
  }

  nextPage(): void {
    if (this.page < this.totalPages - 1) this.loadPage(this.page + 1);
  }

  goToPage(i: number): void {
    if (i >= 0 && i < this.totalPages) this.loadPage(i);
  }

  // Navigation
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
      next: () => {
        this.loadPage(this.page);
        window.alert(`Course "${courseName || ''}" deleted successfully.`);
      },
      error: (err) => {
        console.error('Failed to delete', err);
        window.alert('Failed to delete course.');
      }
    });
  }

  goToCourseDetails(id: string | undefined): void {
    if (id) this.router.navigate(['/course', id]);
  }

}
