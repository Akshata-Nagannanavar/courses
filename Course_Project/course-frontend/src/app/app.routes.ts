import { Routes } from '@angular/router';
import { CoursesListComponent } from './pages/courses-list/courses-list';
import { CreateCourseComponent } from './pages/create-course/create-course';
import { CreateUnitComponent } from './pages/create-unit/create-unit';

export const routes: Routes = [
  { path: '', redirectTo: '/courses', pathMatch: 'full' },
  { path: 'courses', component: CoursesListComponent },
  { path: 'createCourse', component: CreateCourseComponent },
  { path: 'createUnit', component: CreateUnitComponent },
  { path: '**', redirectTo: '/courses' }
];
