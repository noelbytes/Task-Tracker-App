import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TaskService } from '../../services/task.service';
import { Task, TaskStatus, TaskPriority } from '../../models/task.model';

@Component({
  selector: 'app-task-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './task-form.component.html',
  styleUrls: ['./task-form.component.css']
})
export class TaskFormComponent implements OnInit {
  @Input() task: Task | null = null;
  @Output() taskSaved = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  formData: Task = {
    title: '',
    description: '',
    status: TaskStatus.TODO,
    priority: TaskPriority.MEDIUM
  };

  isEditMode: boolean = false;
  isSubmitting: boolean = false;

  TaskStatus = TaskStatus;
  TaskPriority = TaskPriority;

  constructor(private taskService: TaskService) { }

  ngOnInit(): void {
    if (this.task) {
      this.isEditMode = true;
      this.formData = { ...this.task };
    }
  }

  onSubmit(): void {
    this.isSubmitting = true;

    if (this.isEditMode && this.task?.id) {
      this.taskService.updateTask(this.task.id, this.formData).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.taskSaved.emit();
        },
        error: (error: any) => {
          this.isSubmitting = false;
          console.error('Error updating task:', error);
          alert('Failed to update task');
        }
      });
    } else {
      this.taskService.createTask(this.formData).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.taskSaved.emit();
        },
        error: (error: any) => {
          this.isSubmitting = false;
          console.error('Error creating task:', error);
          alert('Failed to create task');
        }
      });
    }
  }

  onCancel(): void {
    this.cancelled.emit();
  }
}

