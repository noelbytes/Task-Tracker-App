import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { TaskService } from '../../services/task.service';
import { TaskStats } from '../../models/task.model';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration } from 'chart.js';

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [CommonModule, BaseChartDirective],
  templateUrl: './analytics.component.html',
  styleUrls: ['./analytics.component.css']
})
export class AnalyticsComponent implements OnInit {
  stats: TaskStats | null = null;
  isLoading: boolean = true;


  // Pie Chart - Task Status Distribution
  public pieChartData: ChartConfiguration<'pie'>['data'] = {
    labels: ['To Do', 'In Progress', 'Done'],
    datasets: [{
      data: [0, 0, 0],
      backgroundColor: ['#f5f5f5', '#2196f3', '#4caf50'],
      borderWidth: 2
    }]
  };

  public pieChartOptions: ChartConfiguration<'pie'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'bottom'
      }
    }
  };

  // Bar Chart - Completion Status
  public barChartData: ChartConfiguration<'bar'>['data'] = {
    labels: ['Pending', 'Completed'],
    datasets: [{
      label: 'Tasks',
      data: [0, 0],
      backgroundColor: ['#ff9800', '#4caf50'],
      borderWidth: 2,
      borderColor: ['#f57c00', '#388e3c']
    }]
  };

  public barChartOptions: ChartConfiguration<'bar'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: false
      }
    },
    scales: {
      y: {
        beginAtZero: true,
        ticks: {
          stepSize: 1
        }
      }
    }
  };

  constructor(
    private taskService: TaskService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadStats();
  }

  loadStats(): void {
    this.taskService.getTaskStats().subscribe({
      next: (stats: TaskStats) => {
        this.stats = stats;
        this.updateCharts();
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Error loading stats:', error);
        this.isLoading = false;
      }
    });
  }

  updateCharts(): void {
    if (this.stats) {
      // Update Pie Chart
      this.pieChartData.datasets[0].data = [
        this.stats.todoTasks,
        this.stats.inProgressTasks,
        this.stats.completedTasks
      ];

      // Update Bar Chart
      this.barChartData.datasets[0].data = [
        this.stats.pendingTasks,
        this.stats.completedTasks
      ];
    }
  }

  goBack(): void {
    this.router.navigate(['/tasks']);
  }
}

