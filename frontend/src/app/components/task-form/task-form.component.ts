import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TaskService } from '../../services/task.service';
import { AIService } from '../../services/ai.service';
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

  // AI features
  naturalLanguageInput: string = '';
  showNaturalLanguageInput: boolean = false;
  isParsingAI: boolean = false;
  aiAvailable: boolean = false;

  TaskStatus = TaskStatus;
  TaskPriority = TaskPriority;

  constructor(
    private taskService: TaskService,
    private aiService: AIService
  ) { }

  ngOnInit(): void {
    if (this.task) {
      this.isEditMode = true;
      this.formData = { ...this.task };
    }

    // Check if AI is available
    this.checkAIAvailability();
  }

  /**
   * Checks if AI service is available for natural language features.
   */
  checkAIAvailability(): void {
    this.aiService.getAIStatus().subscribe({
      next: (status: any) => {
        this.aiAvailable = status.available;
      },
      error: () => {
        this.aiAvailable = false;
      }
    });
  }

  /**
   * Toggles natural language input mode.
   */
  toggleNaturalLanguageInput(): void {
    this.showNaturalLanguageInput = !this.showNaturalLanguageInput;
    if (this.showNaturalLanguageInput) {
      this.naturalLanguageInput = '';
    }
  }

  /**
   * Parses natural language input using AI and populates form fields.
   */
  parseWithAI(): void {
    if (!this.naturalLanguageInput.trim()) {
      return;
    }

    this.isParsingAI = true;
    this.aiService.parseNaturalLanguageTask(this.naturalLanguageInput).subscribe({
      next: (parsed: any) => {
        // AI returns JSON string, parse it
        let parsedData;
        try {
          parsedData = typeof parsed === 'string' ? JSON.parse(parsed) : parsed;
        } catch (e) {
          parsedData = parsed;
        }

        // Populate form with AI-parsed data
        this.formData.title = parsedData.title || this.naturalLanguageInput;
        this.formData.description = parsedData.description || '';
        this.formData.priority = parsedData.priority || TaskPriority.MEDIUM;

        this.isParsingAI = false;
        this.showNaturalLanguageInput = false;
      },
      error: (error: any) => {
        console.error('AI parsing failed:', error);
        // Fallback: use input as title
        this.formData.title = this.naturalLanguageInput;
        this.isParsingAI = false;
        this.showNaturalLanguageInput = false;
      }
    });
  }

  /**
   * Gets AI recommendation for priority based on current title/description.
   */
  getAIPriorityRecommendation(): void {
    if (!this.formData.title.trim()) {
      return;
    }

    this.aiService.recommendPriority(this.formData.title, this.formData.description).subscribe({
      next: (result: any) => {
        // Update priority with AI recommendation
        this.formData.priority = result.recommendedPriority as TaskPriority;
      },
      error: (error: any) => {
        console.error('AI priority recommendation failed:', error);
      }
    });
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

