import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { ToastContainerComponent } from './toast-container.component';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    ToastContainerComponent
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
