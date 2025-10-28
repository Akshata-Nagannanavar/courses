import { Routes } from '@angular/router';
import { CoursesListComponent } from './pages/courses-list/courses-list';
import { CourseDetailsComponent } from './pages/course-details/course-details';
import { CreateCourseComponent } from './pages/create-course/create-course';
import { CreateUnitComponent } from './pages/create-unit/create-unit';

export const routes: Routes = [
  { path: '', redirectTo: '/courses', pathMatch: 'full' },
  { path: 'courses', component: CoursesListComponent },
{
  path: 'course/:id',
  component: CourseDetailsComponent
},
{ path: 'editCourse/:id', component: CourseDetailsComponent },
  { path: 'createCourse', component: CreateCourseComponent },
  { path: 'createUnit', component: CreateUnitComponent },
  { path: '**', redirectTo: '/courses' }
];



