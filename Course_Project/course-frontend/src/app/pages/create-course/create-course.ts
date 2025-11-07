import { Component, OnInit, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Course, EnumsResponse } from '../../services/course';
import type { Course as CourseModel } from '../../services/course';

@Component({
  selector: 'app-create-course',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-course.html',
  styleUrls: ['./create-course.scss']
})
export class CreateCourseComponent implements OnInit {
  courseForm: FormGroup;

  // Filter / Enum lists
  filterBoards: string[] = [];
  filterMediums: string[] = [];
  filterGrades: string[] = [];
  filterSubjects: string[] = [];

  selectedMediums: string[] = [];
  selectedGrades: string[] = [];
  selectedSubjects: string[] = [];

  dropdownOpen = { medium: false, grade: false, subject: false };

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private courseService: Course
  ) {
    // this.courseForm = this.fb.group({
    //   name: ['', Validators.required],
    //   description: ['', Validators.required],
    //   board: ['', Validators.required],
    // });
    this.courseForm = this.fb.group({
  name: [
    '',
    [
      Validators.required,
      Validators.maxLength(30)
    ]
  ],
  description: [
    '',
    [
      Validators.required,
      Validators.maxLength(100)
    ]
  ],
  board: ['', Validators.required],
});

  }

  ngOnInit(): void {
    //  Use backend enums instead of scanning all courses
    this.courseService.getEnums().subscribe({
      next: (data: EnumsResponse) => {
        this.filterBoards = data.boards || [];
        this.filterMediums = data.mediums || [];
        this.filterGrades = data.grades || [];
        this.filterSubjects = data.subjects || [];
      },
      error: (err) => {
        console.error('Failed to fetch enums, falling back to old method:', err);

        // Optional fallback (if backend enums API fails)
        this.courseService.getAllForFilters().subscribe({
          next: (courses) => this.buildFiltersFromAll(courses),
          error: (e) => console.error('Fallback also failed:', e)
        });
      }
    });
  }

  /** Fallback - keep existing buildFiltersFromAll */
  private buildFiltersFromAll(allCourses: CourseModel[]) {
    const b = new Set<string>(), m = new Set<string>(), g = new Set<string>(), s = new Set<string>();

    for (const c of allCourses || []) {
      if (c.board) b.add(c.board);
      (c.medium || []).forEach(x => x && m.add(x));
      (c.grade || []).forEach(x => x && g.add(x));
      (c.subject || []).forEach(x => x && s.add(x));
    }

    this.filterBoards = [...b].sort();
    this.filterMediums = [...m].sort();
    this.filterGrades = [...g].sort();
    this.filterSubjects = [...s].sort();
  }

  get mediumInvalid() {
    return this.courseForm.touched && this.selectedMediums.length === 0;
  }
  get gradeInvalid() {
    return this.courseForm.touched && this.selectedGrades.length === 0;
  }
  get subjectInvalid() {
    return this.courseForm.touched && this.selectedSubjects.length === 0;
  }

  isFormValid(): boolean {
    return (
      this.courseForm.valid &&
      this.selectedMediums.length > 0 &&
      this.selectedGrades.length > 0 &&
      this.selectedSubjects.length > 0
    );
  }

  onCheckboxChange(event: any, field: 'medium' | 'grade' | 'subject') {
    const value = event.target.value;
    const checked = event.target.checked;
    const selectedArray =
      field === 'medium'
        ? this.selectedMediums
        : field === 'grade'
        ? this.selectedGrades
        : this.selectedSubjects;

    if (checked && !selectedArray.includes(value)) selectedArray.push(value);
    else if (!checked) selectedArray.splice(selectedArray.indexOf(value), 1);
  }

  onSubmit() {
    if (!this.isFormValid()) {
      this.courseForm.markAllAsTouched();
      alert(' Please fill all required fields including Medium, Grade, and Subject.');
      return;
    }

    const newCourse: CourseModel = {
      ...this.courseForm.value,
      medium: this.selectedMediums,
      grade: this.selectedGrades,
      subject: this.selectedSubjects
    };

    this.courseService.createCourse(newCourse).subscribe({
      next: () => {
        alert(' Course created successfully!');
        this.router.navigate(['/courses']);
      },
      error: (err) => {
        console.error('Error creating course:', err);
        alert(' Failed to create course!');
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/courses']);
  }

  toggleDropdown(type: 'medium' | 'grade' | 'subject', event: MouseEvent) {
    event.stopPropagation();
    this.dropdownOpen[type] = !this.dropdownOpen[type];
  }

  @HostListener('document:click')
  closeAllDropdowns() {
    this.dropdownOpen = { medium: false, grade: false, subject: false };
  }

  goToCreateUnit() {
    this.router.navigate(['/createUnit']);
  }
}
