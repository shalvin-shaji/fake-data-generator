import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { modelsApi } from '../api/client'
import type { ModelDefinition } from '../api/types'

export default function ModelsPage() {
  const navigate = useNavigate()
  const [models, setModels] = useState<ModelDefinition[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const load = () => {
    setLoading(true)
    modelsApi.list()
      .then(setModels)
      .catch(() => setError('Failed to load models'))
      .finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [])

  const handleDelete = async (id: string, name: string) => {
    if (!confirm(`Delete model "${name}"?`)) return
    await modelsApi.delete(id)
    load()
  }

  return (
    <div className="p-8">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Models</h1>
          <p className="text-sm text-slate-500 mt-1">Define the shape of your fake data</p>
        </div>
        <button
          onClick={() => navigate('/models/new')}
          className="px-4 py-2 bg-indigo-600 text-white text-sm font-medium rounded-lg hover:bg-indigo-700 transition-colors"
        >
          + New Model
        </button>
      </div>

      {loading && <p className="text-slate-400">Loading…</p>}
      {error && <p className="text-red-500">{error}</p>}

      {!loading && models.length === 0 && (
        <div className="text-center py-20 text-slate-400">
          <div className="text-5xl mb-4">🗂</div>
          <p className="text-lg">No models yet.</p>
          <p className="text-sm mt-1">Click <strong>New Model</strong> to define your first data shape.</p>
        </div>
      )}

      {models.length > 0 && (
        <div className="bg-white rounded-xl border border-slate-200 overflow-hidden shadow-sm">
          <table className="w-full text-sm">
            <thead className="bg-slate-50 border-b border-slate-200">
              <tr>
                <th className="text-left px-4 py-3 font-semibold text-slate-600">Name</th>
                <th className="text-left px-4 py-3 font-semibold text-slate-600">Kafka Topic</th>
                <th className="text-left px-4 py-3 font-semibold text-slate-600">Key Field</th>
                <th className="text-left px-4 py-3 font-semibold text-slate-600">Fields</th>
                <th className="px-4 py-3" />
              </tr>
            </thead>
            <tbody>
              {models.map((m, i) => (
                <tr
                  key={m.id}
                  className={`border-b border-slate-100 ${i % 2 === 0 ? 'bg-white' : 'bg-slate-50/50'}`}
                >
                  <td className="px-4 py-3 font-medium text-slate-900">{m.name}</td>
                  <td className="px-4 py-3 text-slate-500 font-mono text-xs">{m.kafkaTopic || '—'}</td>
                  <td className="px-4 py-3 text-slate-500 font-mono text-xs">{m.keyField || '—'}</td>
                  <td className="px-4 py-3">
                    <span className="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-indigo-50 text-indigo-700">
                      {m.fields.length} fields
                    </span>
                  </td>
                  <td className="px-4 py-3">
                    <div className="flex items-center gap-2 justify-end">
                      <button
                        onClick={() => navigate('/generate', { state: { modelId: m.id } })}
                        className="px-3 py-1 text-xs font-medium rounded-md bg-emerald-50 text-emerald-700 hover:bg-emerald-100 transition-colors"
                        title="Generate preview"
                      >
                        Generate
                      </button>
                      <button
                        onClick={() => navigate(`/models/${m.id}/edit`)}
                        className="px-3 py-1 text-xs font-medium rounded-md bg-slate-100 text-slate-700 hover:bg-slate-200 transition-colors"
                      >
                        Edit
                      </button>
                      <button
                        onClick={() => handleDelete(m.id!, m.name)}
                        className="px-3 py-1 text-xs font-medium rounded-md bg-red-50 text-red-600 hover:bg-red-100 transition-colors"
                      >
                        Delete
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
