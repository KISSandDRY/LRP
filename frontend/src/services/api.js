import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json'
    }
});

export const getVehicles = () => api.get('/vehicles');
export const addVehicle = (vehicle) => api.post('/vehicles', vehicle);

export const getCargo = () => api.get('/cargo');
export const addCargo = (cargo) => api.post('/cargo', cargo);

export const getRoutes = () => api.get('/routes');

export const runOptimization = (fuelPrice) => api.post(`/optimization/run?fuelPrice=${fuelPrice}`);

export default api;
