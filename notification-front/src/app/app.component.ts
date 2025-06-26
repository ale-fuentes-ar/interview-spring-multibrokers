import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

interface Notification {
  id: number;
  message: string;
  timestamp: string;
}

@Component({
  selector: "app-root",
  templateUrl: "./app.component.html",
  styleUrls: ["./app.component.css"],
})
export class AppComponent implements OnInit {
  title = "Gestor de Notificaciones";
  notifications: Notification[] = [];
  newMessage: string = "";
  apiUrl = "http://localhost:8080/api/notifications";

  constructor(private http: HttpClient) {}

  ngOnInit() {}

  loadNotifications() {
    this.http.get<Notification[]>(this.apiUrl).subscribe((data) => {
      this.notifications = data;
    });
  }

  sendNotification() {
    if (!this.newMessage.trim()) return;

    this.http
      .post(this.apiUrl, { message: this.newMessage })
      .subscribe(() => {
        this.newMessage = '';
        this.loadNotifications();
      });
  }
}
