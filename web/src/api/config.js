// src/api/api.js
import axios from "axios";

const api = axios.create({
  baseURL: "http://ec2-3-149-24-90.us-east-2.compute.amazonaws.com",
  headers: {
    "Content-Type": "application/json",
  },
});

// Variable local en memoria para el token
let token = null;

// Permite setear el token desde otro lugar
export const setAuthToken = (newToken) => {
  token = newToken;
};

// Interceptor que agrega el token dinámicamente a cada request
api.interceptors.request.use((config) => {
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;
