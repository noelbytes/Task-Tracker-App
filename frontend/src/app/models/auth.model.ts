export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  username: string;
  email: string;
  message: string;
}

export interface User {
  username: string;
  email: string;
  token: string;
}

