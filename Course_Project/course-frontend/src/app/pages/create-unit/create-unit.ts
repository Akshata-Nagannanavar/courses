import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import {  Course, Unit } from '../../services/course';

@Component({
  selector: 'app-create-unit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-unit.html',
  styleUrls: ['./create-unit.scss']
})
export class CreateUnitComponent implements OnInit {
  unitForm: FormGroup;
  courses: Course[] = [];

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private course: Course
  ) {
    this.unitForm = this.fb.group({
      courseId: ['', Validators.required],
      title: ['', Validators.required],
      content: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadCourses();
  }

  /** Fetch all courses from backend */
  loadCourses(): void {
    this.course.getAllForFilters().subscribe({
      next: (data) => {
        this.courses = data;
      },
      error: (err) => {
        console.error('Failed to fetch courses:', err);
        alert('Failed to load courses. Please try again.');
      }
    });
  }

  /** Submit Unit */
  onSubmit(): void {
    if (this.unitForm.valid) {
      const { courseId, title, content } = this.unitForm.value;
      const newUnit: Unit = { title, content };

      this.course.addUnitToCourse(courseId, newUnit).subscribe({
        next: (res) => {
          console.log('Unit added:', res);
          alert('✅ Unit added successfully!');
          this.unitForm.reset();
        },
        error: (err) => {
          console.error('Error adding unit:', err);
          alert('❌ Failed to add unit!');
        }
      });
    } else {
      alert('Please fill all required fields.');
    }
  }

  goBack(): void {
    this.router.navigate(['/courses']);
  }
}
