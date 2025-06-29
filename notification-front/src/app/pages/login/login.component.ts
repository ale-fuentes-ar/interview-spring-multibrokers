import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule],
  template: `
    <div class="auth-container">
      <h2>Iniciar Sesión</h2>
      <form (ngSubmit)="onLogin()">
        <input type="text" [(ngModel)]="username" name="username" placeholder="Usuario" required>
        <input type="password" [(ngModel)]="password" name="password" placeholder="Contraseña" required>
        <button type="submit">Login</button>
      </form>
      <a routerLink="/register">¿No tienes cuenta? Regístrate</a>
    </div>
  `,
  styles: [`.auth-container { max-width: 400px; margin: 50px auto; padding: 20px; border: 1px solid #ccc; border-radius: 5px; }`]
})
export class LoginComponent {
  username = '';
  password = '';

  constructor(private authService: AuthService, private router: Router) {}

  onLogin() {
    this.authService.login({ username: this.username, password: this.password }).subscribe({
      next: () => this.router.navigate(['/notifications']),
      error: (err) => console.error('Error en el login', err)
    });
  }
}