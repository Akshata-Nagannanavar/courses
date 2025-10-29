import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Course } from '../../services/course';
import type { Course as CourseModel, Unit } from '../../services/course';

@Component({
  selector: 'app-course-details',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './course-details.html',
  styleUrls: ['./course-details.scss']
})
export class CourseDetailsComponent implements OnInit {
  courseId: string | null = null;
  courseData!: CourseModel;
  editMode = false;
  courseForm!: FormGroup;

  // ✅ For Unit management
  units: Unit[] = [];
  editingUnitIndex: number | null = null;
  unitEditForm!: FormGroup;
  newUnitForm!: FormGroup;
  addingUnit = false;

  // ✅ Dynamic filter data
  boards: string[] = [];
  mediums: string[] = [];
  grades: string[] = [];
  subjects: string[] = [];

  selectedMediums: string[] = [];
  selectedGrades: string[] = [];
  selectedSubjects: string[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder,
    private courseService: Course
  ) {}

  ngOnInit(): void {
    this.courseId = this.route.snapshot.paramMap.get('id');
    if (this.courseId) {
      this.loadCourse();
      this.loadUnits();
    }

    this.courseService.getAllForFilters().subscribe({
      next: (all) => this.buildFiltersFromAll(all),
      error: (err) => {
        console.warn('Failed to fetch filter options:', err);
        this.buildFiltersFromAll([]);
      }
    });

    // Initialize new unit form
    this.newUnitForm = this.fb.group({
      title: ['', Validators.required],
      content: ['', Validators.required]
    });
  }

  private buildFiltersFromAll(allCourses: CourseModel[]) {
    const b = new Set<string>(), m = new Set<string>(), g = new Set<string>(), s = new Set<string>();
    for (const c of allCourses || []) {
      if (c.board) b.add(c.board);
      (c.medium || []).forEach(x => x && m.add(x));
      (c.grade || []).forEach(x => x && g.add(x));
      (c.subject || []).forEach(x => x && s.add(x));
    }
    this.boards = Array.from(b).sort();
    this.mediums = Array.from(m).sort();
    this.grades = Array.from(g).sort();
    this.subjects = Array.from(s).sort();
  }

  loadCourse(): void {
    this.courseService.getCourseById(this.courseId!).subscribe({
      next: (data) => {
        this.courseData = data;
        this.selectedMediums = data.medium || [];
        this.selectedGrades = data.grade || [];
        this.selectedSubjects = data.subject || [];
        this.initializeForm(data);
      },
      error: (err) => {
        console.error('Failed to load course:', err);
        alert('Failed to load course details!');
      }
    });
  }

  loadUnits(): void {
    this.courseService.getUnitsForCourse(this.courseId!).subscribe({
      next: (units) => (this.units = units || []),
      error: (err) => {
        console.error('Failed to load units:', err);
        this.units = [];
      }
    });
  }

  initializeForm(course: CourseModel): void {
    this.courseForm = this.fb.group({
      name: [course.name, Validators.required],
      description: [course.description, Validators.required],
      board: [course.board, Validators.required]
    });
  }

  toggleEdit(): void {
    this.editMode = !this.editMode;
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

    if (checked && !selectedArray.includes(value)) {
      selectedArray.push(value);
    } else if (!checked) {
      const index = selectedArray.indexOf(value);
      if (index > -1) selectedArray.splice(index, 1);
    }
  }

  onSave(): void {
    if (!this.courseForm.valid) {
      alert('Please fill all required fields.');
      return;
    }

    const updatedCourse = {
      ...this.courseForm.value,
      medium: this.selectedMediums,
      grade: this.selectedGrades,
      subject: this.selectedSubjects
    };

    this.courseService.updateCourse(this.courseId!, updatedCourse).subscribe({
      next: () => {
        alert('✅ Course updated successfully!');
        this.editMode = false;
        this.loadCourse();
      },
      error: (err) => {
        console.error('Update failed:', err);
        alert('❌ Failed to update course!');
      }
    });
  }

  onCancel(): void {
    this.editMode = false;
    this.initializeForm(this.courseData);
  }

  goBack(): void {
    this.router.navigate(['/courses']);
  }

  // ===========================
  // ✅ UNIT MANAGEMENT SECTION
  // ===========================

  startEditUnit(index: number): void {
    const unit = this.units[index];
    this.editingUnitIndex = index;
    this.unitEditForm = this.fb.group({
      title: [unit.title, Validators.required],
      content: [unit.content, Validators.required]
    });
  }

  saveUnitEdit(): void {
    if (!this.courseId || this.editingUnitIndex === null) return;
    if (this.unitEditForm.invalid) return;

    const updatedUnit = this.unitEditForm.value;
    const unitId = this.units[this.editingUnitIndex].id!;

    this.courseService.updateUnit(this.courseId, unitId, updatedUnit).subscribe({
      next: () => {
        alert('✅ Unit updated successfully!');
        this.editingUnitIndex = null;
        this.loadUnits();
      },
      error: (err) => {
        console.error('Unit update failed:', err);
        alert('❌ Failed to update unit!');
      }
    });
  }

  cancelUnitEdit(): void {
    this.editingUnitIndex = null;
  }

  deleteUnit(index: number): void {
    const unit = this.units[index];
    if (!this.courseId || !unit.id) return;

    if (!confirm(`Delete unit "${unit.title}"?`)) return;

    this.courseService.deleteUnit(this.courseId, unit.id).subscribe({
      next: () => {
        alert('🗑️ Unit deleted!');
        this.loadUnits();
      },
      error: (err) => {
        console.error('Unit deletion failed:', err);
        alert('❌ Failed to delete unit!');
      }
    });
  }

  addUnit(): void {
    if (!this.courseId) return;
    if (this.newUnitForm.invalid) {
      alert('Please fill all unit fields.');
      return;
    }

    const newUnit = this.newUnitForm.value;

    this.courseService.addUnitToCourse(this.courseId, newUnit).subscribe({
      next: () => {
        alert('✅ Unit added successfully!');
        this.newUnitForm.reset();
        this.addingUnit = false;
        this.loadUnits();
      },
      error: (err) => {
        console.error('Add unit failed:', err);
        alert('❌ Failed to add unit!');
      }
    });
  }
}
