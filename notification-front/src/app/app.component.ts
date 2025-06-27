import { CommonModule } from '@angular/common'; // <-- Necesario para *ngIf, *ngFor, etc.
import { HttpClient } from '@angular/common/http'; // <-- Importado para la inyección
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms'; // <-- Necesario para ngModel
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

// Definimos la interfaz aquí mismo
interface Notification {
  id: number;
  message: string;
  timestamp: string;
}

@Component({
  selector: 'app-root',
  standalone: true, // <-- ¡CLAVE! El componente es autónomo.
  imports: [
    CommonModule, // <-- ¡CLAVE! Provee directivas como *ngIf y *ngFor.
    FormsModule, // <-- ¡CLAVE! Provee la directiva ngModel.
  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  // --- Lógica de la aplicación (la misma que antes) ---
  title = 'Gestor de Notificaciones';
  notifications: Notification[] = [];
  newMessage: string = '';
  apiUrl = 'http://localhost:8080/api/notifications';

  newNotificationCount = 0;
  activeToasts: Notification[] = [];

  private stompClient: Client;

  // ¡OJO! HttpClient se inyecta gracias a `provideHttpClient` en app.config.ts
  constructor(private http: HttpClient) {
    this.stompClient = new Client({
      // ¡Ajuste! La URL de SockJS debe ser la completa.
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      debug: (str) => {
        /* console.log(new Date(), str); */
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
}
