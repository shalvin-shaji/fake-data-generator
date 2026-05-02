import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { modelsApi } from '../api/client'
import type { FieldDefinition, FieldType, ModelDefinition } from '../api/types'
import FieldConfigPanel from '../components/FieldConfigPanel'

const FIELD_TYPE_CATEGORIES: Record<string, FieldType[]> = {
  'Identifiers': ['UUID', 'SEQUENCE', 'REFERENCE'],
  'Primitives': ['INTEGER', 'DECIMAL', 'BOOLEAN', 'STRING'],
  'Personal': ['FIRST_NAME', 'LAST_NAME', 'FULL_NAME', 'EMAIL', 'PHONE', 'DATE_OF_BIRTH'],
  'Address': ['STREET', 'CITY', 'STATE', 'COUNTRY', 'POSTAL_CODE'],
  'Finance': ['IBAN', 'ACCOUNT_NUMBER', 'CURRENCY', 'CUSIP', 'ISIN', 'TICKER'],
  'Date / Time': ['DATE', 'TIMESTAMP'],
  'Choice': ['ENUM', 'WEIGHTED_ENUM'],
  'Pattern': ['REGEX'],
}

const emptyField = (): FieldDefinition => ({
  name: '',
  type: 'UUID',
  config: {},
  nullProbability: 0,
})

export default function ModelBuilderPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const isEdit = Boolean(id)

  const [name, setName] = useState('')
  const [kafkaTopic, setKafkaTopic] = useState('')
  const [keyField, setKeyField] = useState('')
  const [fields, setFields] = useState<FieldDefinition[]>([emptyField()])
  const [expandedRow, setExpandedRow] = useState<number | null>(null)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!id) return
    modelsApi.get(id).then(m => {
      setName(m.name)
      setKafkaTopic(m.kafkaTopic)
      setKeyField(m.keyField ?? '')
      setFields(m.fields.length ? m.fields : [emptyField()])
    }).catch(() => setError('Failed to load model'))
  }, [id])

  const updateField = (i: number, patch: Partial<FieldDefinition>) => {
    setFields(prev => prev.map((f, idx) => idx === i ? { ...f, ...patch } : f))
  }

  const addField = () => {
    const next = fields.length
    setFields(prev => [...prev, emptyField()])
    setExpandedRow(next)
  }

  const removeField = (i: number) => {
    setFields(prev => prev.filter((_, idx) => idx !== i))
    if (expandedRow === i) setExpandedRow(null)
  }

  const moveField = (i: number, dir: -1 | 1) => {
    const j = i + dir
    if (j < 0 || j >= fields.length) return
    setFields(prev => {
      const next = [...prev]
      ;[next[i], next[j]] = [next[j], next[i]]
      return next
    })
    setExpandedRow(j)
  }

  const handleSave = async () => {
    if (!name.trim()) { setError('Model name is required'); return }
    if (fields.some(f => !f.name.trim())) { setError('All fields must have a name'); return }
    setSaving(true)
    setError('')
    const model: ModelDefinition = {
      name: name.trim(),
      kafkaTopic: kafkaTopic.trim(),
      keyField: keyField.trim() || undefined,
      fields,
    }
    try {
      if (isEdit && id) {
        await modelsApi.update(id, model)
      } else {
        await modelsApi.create(model)
      }
      navigate('/')
    } catch {
      setError('Failed to save model')
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="p-8 max-w-4xl">
      <div className="flex items-center gap-3 mb-6">
        <button onClick={() => navigate('/')} className="text-slate-400 hover:text-slate-600 text-sm">
          ← Models
        </button>
        <span className="text-slate-300">/</span>
        <h1 className="text-2xl font-bold text-slate-900">
          {isEdit ? 'Edit Model' : 'New Model'}
        </h1>
      </div>

      {error && (
        <div className="mb-4 px-4 py-3 bg-red-50 border border-red-200 rounded-lg text-sm text-red-700">
          {error}
        </div>
      )}

      {/* Model metadata */}
      <div className="bg-white rounded-xl border border-slate-200 p-5 mb-5 shadow-sm">
        <h2 className="text-sm font-semibold text-slate-700 mb-4">Model Settings</h2>
        <div className="grid grid-cols-3 gap-4">
          <label className="flex flex-col gap-1">
            <span className="text-xs font-medium text-slate-600">Model Name *</span>
            <input
              value={name}
              onChange={e => setName(e.target.value)}
              placeholder="e.g. Customer"
              className="px-3 py-2 text-sm border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-300"
            />
          </label>
          <label className="flex flex-col gap-1">
            <span className="text-xs font-medium text-slate-600">Kafka Topic</span>
            <input
              value={kafkaTopic}
              onChange={e => setKafkaTopic(e.target.value)}
              placeholder="e.g. customers"
              className="px-3 py-2 text-sm border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-300"
            />
          </label>
          <label className="flex flex-col gap-1">
            <span className="text-xs font-medium text-slate-600">Key Field</span>
            <input
              value={keyField}
              onChange={e => setKeyField(e.target.value)}
              placeholder="e.g. customerId"
              className="px-3 py-2 text-sm border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-300"
            />
          </label>
        </div>
      </div>

      {/* Fields */}
      <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden mb-5">
        <div className="flex items-center justify-between px-5 py-4 border-b border-slate-100">
          <h2 className="text-sm font-semibold text-slate-700">
            Fields <span className="ml-1 text-xs font-normal text-slate-400">({fields.length})</span>
          </h2>
        </div>

        {/* Header row */}
        <div className="grid grid-cols-[2rem_1fr_1fr_6rem_6rem_2rem] gap-2 px-4 py-2 bg-slate-50 border-b border-slate-100 text-xs font-semibold text-slate-500 uppercase tracking-wide">
          <div />
          <div>Field Name</div>
          <div>Type</div>
          <div>Null %</div>
          <div>Config</div>
          <div />
        </div>

        {fields.map((field, i) => (
          <div key={i}>
            {/* Row */}
            <div className="grid grid-cols-[2rem_1fr_1fr_6rem_6rem_2rem] gap-2 items-center px-4 py-2 border-b border-slate-100 hover:bg-slate-50/50">
              {/* Reorder */}
              <div className="flex flex-col items-center gap-0.5">
                <button type="button" onClick={() => moveField(i, -1)} disabled={i === 0}
                  className="text-slate-300 hover:text-slate-500 disabled:opacity-20 text-xs leading-none">▲</button>
                <button type="button" onClick={() => moveField(i, 1)} disabled={i === fields.length - 1}
                  className="text-slate-300 hover:text-slate-500 disabled:opacity-20 text-xs leading-none">▼</button>
              </div>

              {/* Name */}
              <input
                value={field.name}
                onChange={e => updateField(i, { name: e.target.value })}
                placeholder="fieldName"
                className="px-2 py-1.5 text-sm border border-slate-200 rounded-lg font-mono focus:outline-none focus:ring-2 focus:ring-indigo-200"
              />

              {/* Type */}
              <select
                value={field.type}
                onChange={e => updateField(i, { type: e.target.value as FieldType, config: {} })}
                className="px-2 py-1.5 text-sm border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-200 bg-white"
              >
                {Object.entries(FIELD_TYPE_CATEGORIES).map(([cat, types]) => (
                  <optgroup key={cat} label={cat}>
                    {types.map(t => <option key={t} value={t}>{t}</option>)}
                  </optgroup>
                ))}
              </select>

              {/* Null probability */}
              <div className="flex items-center gap-1">
                <input
                  type="number"
                  min="0" max="1" step="0.05"
                  value={field.nullProbability}
                  onChange={e => updateField(i, { nullProbability: Number(e.target.value) })}
                  className="w-16 px-2 py-1.5 text-xs border border-slate-200 rounded-lg focus:outline-none"
                />
              </div>

              {/* Config toggle */}
              <button
                type="button"
                onClick={() => setExpandedRow(expandedRow === i ? null : i)}
                className={`px-2 py-1.5 text-xs rounded-lg font-medium transition-colors ${
                  expandedRow === i
                    ? 'bg-indigo-100 text-indigo-700'
                    : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                }`}
              >
                {expandedRow === i ? 'Hide' : 'Config'}
              </button>

              {/* Remove */}
              <button
                type="button"
                onClick={() => removeField(i)}
                className="text-slate-300 hover:text-red-500 transition-colors"
              >
                ✕
              </button>
            </div>

            {/* Config panel */}
            {expandedRow === i && (
              <div className="px-12 py-3 bg-indigo-50/50 border-b border-slate-100">
                <FieldConfigPanel
                  field={field}
                  onChange={config => updateField(i, { config })}
                />
              </div>
            )}
          </div>
        ))}

        <div className="px-4 py-3">
          <button
            type="button"
            onClick={addField}
            className="text-sm text-indigo-600 hover:text-indigo-800 font-medium"
          >
            + Add Field
          </button>
        </div>
      </div>

      {/* Actions */}
      <div className="flex items-center gap-3">
        <button
          onClick={handleSave}
          disabled={saving}
          className="px-5 py-2.5 bg-indigo-600 text-white text-sm font-medium rounded-lg hover:bg-indigo-700 disabled:opacity-50 transition-colors"
        >
          {saving ? 'Saving…' : isEdit ? 'Save Changes' : 'Create Model'}
        </button>
        <button
          onClick={() => navigate('/')}
          className="px-5 py-2.5 text-sm font-medium text-slate-600 hover:text-slate-900 transition-colors"
        >
          Cancel
        </button>
      </div>
    </div>
  )
}
