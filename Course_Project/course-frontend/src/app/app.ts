import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterOutlet } from '@angular/router';


@Component({
  selector: 'app-root',
  imports: [RouterOutlet,FormsModule],
  //,CoursesList,CreateCourse,CreateUnit
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
// export class App {
//   protected readonly title = signal('course-frontend');
// }
export class App{

}
