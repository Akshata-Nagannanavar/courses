import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Course } from '../../services/course';  // ✅ same import

@Component({
  selector: 'app-create-course',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-course.html',
  styleUrls: ['./create-course.scss']
})
export class CreateCourseComponent {
  courseForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private course: Course
  ) {
    this.courseForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      board: ['', Validators.required],
      medium: ['', Validators.required],
      grade: ['', Validators.required],
      subject: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.courseForm.valid) {
      this.course.createCourse(this.courseForm.value).subscribe({
        next: (res) => {
          console.log('Course created:', res);
          alert('Course created successfully!');
          this.router.navigate(['/courses']);
        },
        error: (err) => {
          console.error('Error creating course:', err);
          alert('Failed to create course!');
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
