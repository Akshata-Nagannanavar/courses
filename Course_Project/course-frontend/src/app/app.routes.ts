import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: '/courses', pathMatch: 'full' },

  {
    path: 'courses',
    loadComponent: () =>
      import('./pages/courses-list/courses-list').then(m => m.CoursesListComponent)
  },
  {
    path: 'course/:id',
    loadComponent: () =>
      import('./pages/course-details/course-details').then(m => m.CourseDetailsComponent)
  },
  {
    path: 'editCourse/:id',
    loadComponent: () =>
      import('./pages/course-details/course-details').then(m => m.CourseDetailsComponent)
  },
  {
    path: 'createCourse',
    loadComponent: () =>
      import('./pages/create-course/create-course').then(m => m.CreateCourseComponent)
  },
  {
    path: 'createUnit',
    loadComponent: () =>
      import('./pages/create-unit/create-unit').then(m => m.CreateUnitComponent)
  },
  { path: '**', redirectTo: '/courses' }
];
