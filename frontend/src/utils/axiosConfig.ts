import axios, { AxiosInstance } from 'axios';
import { baseUrl } from './constants';

const axiosInstance: AxiosInstance = axios.create({
  baseURL: baseUrl,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

axiosInstance.interceptors.response.use(
  (response) => {
    return response.data; 
  },
  (error) => {
    if (error.response?.status === 401) {
      console.log(error.message)
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;
