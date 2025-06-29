import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { AuthService } from '../../services/auth.service';

interface Notification {
  id: number;
  message: string;
  timestamp: string;
}

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './notifications.component.html',
  styleUrl: './notifications.component.css',
})
export class NotificationsComponent implements OnInit {
  title = 'Gestor de Notificaciones';
  notifications: Notification[] = [];
  newMessage: string = '';
  apiUrl = 'http://localhost:8080/api/notifications';

  newNotificationCount = 0;
  activeToasts: Notification[] = [];

  private stompClient: Client;

  constructor(
    private http: HttpClient,
    private authService: AuthService,
  ) {
    this.stompClient = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      debug: (str) => {
        console.log(new Date(), str);
      },
      reconnectDelay: 5000,
    });
  }

  ngOnInit() {
    this.loadNotifications();
    this.connectToWebSocket();
  }

  connectToWebSocket() {
    this.stompClient.onConnect = (frame) => {
      console.log('Conectado al Broker WebSocket: ' + frame);
      this.stompClient.subscribe('/topic/notifications', (message) => {
        const newNotification = JSON.parse(message.body) as Notification;
        this.handleNewNotification(newNotification);
      });
    };
    this.stompClient.activate();
  }

  handleNewNotification(notification: Notification) {
    this.notifications.unshift(notification);
    this.newNotificationCount++;
    this.showToast(notification);
  }

  showToast(notification: Notification) {
    this.activeToasts.push(notification);
    setTimeout(() => {
      this.activeToasts.shift();
    }, 5000);
  }

  resetNotificationCount() {
    this.newNotificationCount = 0;
  }

  loadNotifications() {
    this.http.get<Notification[]>(this.apiUrl).subscribe((data) => {
      this.notifications = data.sort(
        (a, b) =>
          new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime()
      );
    });
  }

  sendNotification() {
    if (!this.newMessage.trim()) return;
    this.http.post(this.apiUrl, { message: this.newMessage }).subscribe(() => {
      this.newMessage = '';
    });
  }

  logout() {
    this.authService.logout();
    if (this.stompClient.connected) {
      this.stompClient.deactivate();
    }
  }
}
