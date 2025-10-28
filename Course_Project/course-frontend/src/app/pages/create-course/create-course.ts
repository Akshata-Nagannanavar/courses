import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import {  Course } from '../../services/course';

@Component({
  selector: 'app-create-course',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-course.html',
  styleUrls: ['./create-course.scss']
})
export class CreateCourseComponent {
  courseForm: FormGroup;

  boards = ['CBSE', 'ICSE', 'State Board'];
  mediums = ['English', 'Hindi', 'Kannada', 'Tamil', 'Telugu'];
  grades = ['CLASS_1','CLASS_2','CLASS_3','CLASS_4','CLASS_5','CLASS_6','CLASS_7','CLASS_8','CLASS_9','CLASS_10'];
  subjects = ['Mathematics', 'Science', 'English', 'Social_Studies', 'Hindi','Kannada'];

  selectedMediums: string[] = [];
  selectedGrades: string[] = [];
  selectedSubjects: string[] = [];

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private course: Course
  ) {
    this.courseForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      board: ['', Validators.required],
    });
  }

  /** Handles checkbox selections for medium, grade, and subject */
  onCheckboxChange(event: any, field: 'medium' | 'grade' | 'subject') {
    const value = event.target.value;
    const checked = event.target.checked;

    const selectedArray =
      field === 'medium'
        ? this.selectedMediums
        : field === 'grade'
        ? this.selectedGrades
        : this.selectedSubjects;

    if (checked && !selectedArray.includes(value)) {
      selectedArray.push(value);
    } else if (!checked) {
      const index = selectedArray.indexOf(value);
      if (index > -1) selectedArray.splice(index, 1);
    }
  }

  onSubmit() {
    if (this.courseForm.valid) {
      const newCourse: Course = {
        ...this.courseForm.value,
        medium: this.selectedMediums,
        grade: this.selectedGrades,
        subject: this.selectedSubjects
      };

      this.course.createCourse(newCourse).subscribe({
        next: (res) => {
          console.log('Course created:', res);
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
