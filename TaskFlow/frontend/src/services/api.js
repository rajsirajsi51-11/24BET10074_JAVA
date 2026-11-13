import axios from 'axios';

const api = axios.create({ baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api', headers: { 'Content-Type': 'application/json' } });
export const getProjects = () => api.get('/projects').then(r=>r.data);
export const getTasks = () => api.get('/tasks').then(r=>r.data);
export const getSummary = () => api.get('/dashboard/summary').then(r=>r.data);
export const getProgress = () => api.get('/dashboard/project-progress').then(r=>r.data);
export const saveProject = (data,id) => id ? api.put(`/projects/${id}`,data).then(r=>r.data) : api.post('/projects',data).then(r=>r.data);
export const removeProject = id => api.delete(`/projects/${id}`);
export const saveTask = (data,id) => id ? api.put(`/tasks/${id}`,data).then(r=>r.data) : api.post('/tasks',data).then(r=>r.data);
export const removeTask = id => api.delete(`/tasks/${id}`);
export const setTaskStatus = (id,status) => api.put(`/tasks/${id}/status`,{status}).then(r=>r.data);
