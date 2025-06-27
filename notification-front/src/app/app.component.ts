import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
// Importaciones para las librerías CLÁSICAS
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';

interface Notification {
  id: number;
  message: string;
  timestamp: string;
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'Gestor de Notificaciones';
  notifications: Notification[] = [];
  newMessage: string = '';
  apiUrl = 'http://localhost:8080/api/notifications';

  newNotificationCount = 0;
  activeToasts: Notification[] = [];

  private stompClient: Stomp.Client;

  constructor(private http: HttpClient) { }

  ngOnInit() {
    this.loadNotifications();
    this.connectToWebSocket();
  }

  connectToWebSocket() {
    const socket = new SockJS('http://localhost:8080/ws');

    this.stompClient = Stomp.over(socket);
    this.stompClient.debug = () => {};
    this.stompClient.connect({}, (frame) => {
      console.log('¡Conectado al Broker WebSocket!: ' + frame);

      // 5. La suscripción a los tópicos se hace DESPUÉS de una conexión exitosa.
      this.stompClient.subscribe('/topic/notifications', (message) => {
        const newNotification = JSON.parse(message.body) as Notification;
        this.handleNewNotification(newNotification);
      });
    });
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
    this.http.get<Notification[]>(this.apiUrl)
      .subscribe(data => {
        this.notifications = data.sort((a, b) =>
          new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime()
        );
      });
  }

  sendNotification() {
    if (!this.newMessage.trim()) return;
    this.http.post(this.apiUrl, { message: this.newMessage })
      .subscribe(() => {
        this.newMessage = '';
      });
  }
}