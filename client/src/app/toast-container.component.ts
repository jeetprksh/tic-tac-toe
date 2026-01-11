import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService, ToastEvent } from './toast.service';

@Component({
  selector: 'app-toast-container',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toast-container.component.html',
  styleUrls: ['./toast-container.component.css']
})
export class ToastContainerComponent implements OnInit {
  toasts: Array<ToastEvent> = [];

  constructor(private toastService: ToastService) {
    // expose service globally so AppComponent can call it lazily
    try { (window as any).__toastService = this.toastService; } catch (e) { /* ignore */ }
  }

  ngOnInit() {
    this.toastService.onToast().subscribe((t) => {
      // show newest on top
      this.toasts.unshift(t);
      // auto remove
      setTimeout(() => this.remove(t.id), t.duration ?? 8000);
    });
  }

  remove(id: string) {
    const idx = this.toasts.findIndex(t => t.id === id);
    if (idx !== -1) this.toasts.splice(idx, 1);
  }
}