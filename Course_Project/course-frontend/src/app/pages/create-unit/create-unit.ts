// import { Component } from '@angular/core';

// @Component({
//   selector: 'app-create-unit',
//   imports: [],
//   templateUrl: './create-unit.html',
//   styleUrl: './create-unit.scss',
// })
// export class CreateUnit {

// }
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-create-unit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-unit.html',
  styleUrl: './create-unit.scss'
})
export class CreateUnitComponent {
  unitForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.unitForm = this.fb.group({
      title: ['', Validators.required],
      content: ['']
    });
  }

  onSubmit() {
    if (this.unitForm.valid) {
      console.log('Unit Created:', this.unitForm.value);
      this.unitForm.reset();
    }
  }
}
