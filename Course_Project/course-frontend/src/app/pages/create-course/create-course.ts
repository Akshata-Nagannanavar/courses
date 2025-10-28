import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Course } from '../../services/course';
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

  filterBoards: string[] = [];
  filterMediums: string[] = [];
  filterGrades: string[] = [];
  filterSubjects: string[] = [];

  selectedMediums: string[] = [];
  selectedGrades: string[] = [];
  selectedSubjects: string[] = [];

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private courseService: Course
  ) {
    this.courseForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      board: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    this.courseService.getAllForFilters().subscribe({
      next: (courses) => this.buildFiltersFromAll(courses),
      error: (err) => console.error('Failed to fetch filter data:', err)
    });
  }

  private buildFiltersFromAll(allCourses: CourseModel[]) {
    const b = new Set<string>();
    const m = new Set<string>();
    const g = new Set<string>();
    const s = new Set<string>();

    for (const c of allCourses || []) {
      if (c.board) b.add(c.board);
      (c.medium || []).forEach(x => x && m.add(x));
      (c.grade || []).forEach(x => x && g.add(x));
      (c.subject || []).forEach(x => x && s.add(x));
    }

    this.filterBoards = Array.from(b).sort();
    this.filterMediums = Array.from(m).sort();
    this.filterGrades = Array.from(g).sort();
    this.filterSubjects = Array.from(s).sort();
  }

  onCheckboxChange(event: any, field: 'medium' | 'grade' | 'subject') {
    const value = event.target.value;
    const checked = event.target.checked;
    const selectedArray =
      field === 'medium' ? this.selectedMediums :
      field === 'grade' ? this.selectedGrades : this.selectedSubjects;

    if (checked && !selectedArray.includes(value)) {
      selectedArray.push(value);
    } else if (!checked) {
      const index = selectedArray.indexOf(value);
      if (index > -1) selectedArray.splice(index, 1);
    }
  }

  onSubmit() {
    if (this.courseForm.valid) {
      const newCourse: CourseModel = {
        ...this.courseForm.value,
        medium: this.selectedMediums,
        grade: this.selectedGrades,
        subject: this.selectedSubjects
      };

      this.courseService.createCourse(newCourse).subscribe({
        next: (res) => {
          alert('✅ Course created successfully!');
          this.router.navigate(['/courses']);
        },
        error: (err) => {
          console.error('Error creating course:', err);
          alert('❌ Failed to create course!');
        }
      });
    } else {
      alert('Please fill all required fields.');
    }
  }

  goToCreateUnit() {
    this.router.navigate(['/createUnit']);
  }
}
