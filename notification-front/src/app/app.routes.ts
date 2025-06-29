import { Routes } from "@angular/router";
import { NotificationsComponent } from "./pages/notifications/notifications.component";
import { LoginComponent } from "./pages/login/login.component";
import { RegisterComponent } from "./pages/register/register.component";
import { authGuard } from "./guards/auth-guard";

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  { path: 'notifications', component: NotificationsComponent, canActivate: [authGuard] },

  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/login' },
];