import axios from 'axios'
import type { ModelDefinition, ProduceResult } from './types'

const api = axios.create({ baseURL: '/api' })

export const modelsApi = {
  list: () => api.get<ModelDefinition[]>('/models').then(r => r.data),
  get: (id: string) => api.get<ModelDefinition>(`/models/${id}`).then(r => r.data),
  create: (m: ModelDefinition) => api.post<ModelDefinition>('/models', m).then(r => r.data),
  update: (id: string, m: ModelDefinition) => api.put<ModelDefinition>(`/models/${id}`, m).then(r => r.data),
  delete: (id: string) => api.delete(`/models/${id}`),
}

export const generateApi = {
  generate: (modelId: string, count: number) =>
    api.post<Record<string, unknown>[]>('/generate', { modelId, count }).then(r => r.data),
}

export const produceApi = {
  produce: (modelId: string, count: number, topic?: string) =>
    api.post<ProduceResult>('/produce', { modelId, count, topic }).then(r => r.data),
}
