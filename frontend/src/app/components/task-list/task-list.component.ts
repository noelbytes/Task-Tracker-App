import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TaskService } from '../../services/task.service';
import { AuthService } from '../../services/auth.service';
import { Task, TaskStatus, TaskPriority } from '../../models/task.model';
import { TaskFormComponent } from '../task-form/task-form.component';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [CommonModule, FormsModule, TaskFormComponent],
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.css']
})
export class TaskListComponent implements OnInit {
  tasks: Task[] = [];
  filteredTasks: Task[] = [];
  selectedTask: Task | null = null;
  showTaskForm: boolean = false;
  searchTerm: string = '';
  filterStatus: string = 'ALL';
  filterPriority: string = 'ALL';
  currentUser: string = '';

  TaskStatus = TaskStatus;
  TaskPriority = TaskPriority;

  constructor(
    private taskService: TaskService,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.currentUser = this.authService.currentUserValue?.username || '';
    this.loadTasks();
  }

  loadTasks(): void {
    this.taskService.getAllTasks().subscribe({
      next: (tasks) => {
        this.tasks = tasks;
        this.applyFilters();
      },
      error: (error) => {
        console.error('Error loading tasks:', error);
      }
    });
  }

  applyFilters(): void {
    this.filteredTasks = this.tasks.filter(task => {
      const matchesSearch = task.title.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
                           (task.description?.toLowerCase().includes(this.searchTerm.toLowerCase()) || false);
      const matchesStatus = this.filterStatus === 'ALL' || task.status === this.filterStatus;
      const matchesPriority = this.filterPriority === 'ALL' || task.priority === this.filterPriority;
      return matchesSearch && matchesStatus && matchesPriority;
    });
  }

  onSearchChange(): void {
    this.applyFilters();
  }

  onFilterChange(): void {
    this.applyFilters();
  }

  openAddTaskForm(): void {
    this.selectedTask = null;
    this.showTaskForm = true;
  }

  openEditTaskForm(task: Task): void {
    this.selectedTask = { ...task };
    this.showTaskForm = true;
  }

  closeTaskForm(): void {
    this.showTaskForm = false;
    this.selectedTask = null;
  }

  onTaskSaved(): void {
    this.closeTaskForm();
    this.loadTasks();
  }

  deleteTask(task: Task): void {
    if (confirm(`Are you sure you want to delete "${task.title}"?`)) {
      this.taskService.deleteTask(task.id!).subscribe({
        next: () => {
          this.loadTasks();
        },
        error: (error) => {
          console.error('Error deleting task:', error);
          alert('Failed to delete task');
        }
      });
    }
  }

  getStatusClass(status: TaskStatus): string {
    switch (status) {
      case TaskStatus.TODO: return 'status-todo';
      case TaskStatus.IN_PROGRESS: return 'status-in-progress';
      case TaskStatus.DONE: return 'status-done';
      default: return '';
    }
  }

  getPriorityClass(priority: TaskPriority): string {
    switch (priority) {
      case TaskPriority.LOW: return 'priority-low';
      case TaskPriority.MEDIUM: return 'priority-medium';
      case TaskPriority.HIGH: return 'priority-high';
      default: return '';
    }
  }

  navigateToAnalytics(): void {
    this.router.navigate(['/analytics']);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}

