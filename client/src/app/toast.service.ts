import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';

export interface ToastEvent {
  id: string;
  message: string;
  duration?: number;
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  private subject = new Subject<ToastEvent>();

  onToast(): Observable<ToastEvent> {
    return this.subject.asObservable();
  }

  show(message: string, duration = 8000): string {
    const id = Date.now().toString(36) + Math.random().toString(36).slice(2);
    this.subject.next({ id, message, duration });
    return id;
  }
}