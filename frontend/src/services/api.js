import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// ===== Auth Endpoints =====

export const login = async (credentials) => {
  const response = await api.post('/api/auth/login', credentials);
  return response.data;
};

export const register = async (data) => {
  const response = await api.post('/api/auth/register', data);
  return response.data;
};

export const verifyToken = async (token) => {
  const response = await api.get('/api/auth/verify', {
    headers: { Authorization: `Bearer ${token}` }
  });
  return response.data;
};

// ===== Risk Endpoints =====

export const getRisks = async (params = {}) => {
  const response = await api.get('/api/risk/all', { params });
  return response.data;
};

export const getRiskById = async (id) => {
  const response = await api.get(`/api/risk/${id}`);
  return response.data;
};

export const createRisk = async (riskData) => {
  const response = await api.post('/api/risk', riskData);
  return response.data;
};

export const updateRisk = async (id, riskData) => {
  const response = await api.put(`/api/risk/${id}`, riskData);
  return response.data;
};

export const deleteRisk = async (id) => {
  const response = await api.delete(`/api/risk/${id}`);
  return response.data;
};

export const getStats = async () => {
  const response = await api.get('/api/risk/stats');
  return response.data;
};

export const searchRisks = async (query) => {
  const response = await api.get('/api/risk/search', { params: { q: query } });
  return response.data;
};

export const exportCSV = async () => {
  const response = await api.get('/api/risk/export/csv', { responseType: 'blob' });
  return response.data;
};

// ===== AI Endpoints (through backend proxy) =====

export const getAIDescription = async (riskId) => {
  const response = await api.post(`/api/risk/${riskId}/ai/describe`);
  return response.data;
};

export const getAIRecommendations = async (riskId) => {
  const response = await api.post(`/api/risk/${riskId}/ai/recommend`);
  return response.data;
};

export const getAICategorise = async (riskId) => {
  const response = await api.post(`/api/risk/${riskId}/ai/categorise`);
  return response.data;
};

export const askAIQuery = async (riskId, question) => {
  const response = await api.post(`/api/risk/${riskId}/ai/query`, { question });
  return response.data;
};

export const analyseDocument = async (text) => {
  const response = await api.post('/api/risk/ai/analyse-document', { input: text });
  return response.data;
};

export const batchProcessRisks = async (items) => {
  const response = await api.post('/api/risk/ai/batch-process', { items });
  return response.data;
};

export const getAIHealth = async () => {
  const response = await api.get('/api/risk/ai/health');
  return response.data;
};

export const getStreamingReport = () => {
  const token = localStorage.getItem('token');
  const url = `${API_BASE_URL}/api/risk/ai/report/stream?token=${token}`;
  return new EventSource(url);
};

export default api;