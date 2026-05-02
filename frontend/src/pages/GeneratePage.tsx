import { useEffect, useRef, useState } from 'react'
import { useLocation } from 'react-router-dom'
import { generateApi, modelsApi, produceApi } from '../api/client'
import type { ModelDefinition, ProduceResult } from '../api/types'

export default function GeneratePage() {
  const location = useLocation()
  const preselected = (location.state as { modelId?: string } | null)?.modelId

  const [models, setModels] = useState<ModelDefinition[]>([])
  const [genModelId, setGenModelId] = useState(preselected ?? '')
  const [genCount, setGenCount] = useState(10)
  const [genRecords, setGenRecords] = useState<Record<string, unknown>[] | null>(null)
  const [genLoading, setGenLoading] = useState(false)
  const [genError, setGenError] = useState('')

  const [prodModelId, setProdModelId] = useState(preselected ?? '')
  const [prodCount, setProdCount] = useState(100)
  const [prodTopic, setProdTopic] = useState('')
  const [prodResult, setProdResult] = useState<ProduceResult | null>(null)
  const [prodLoading, setProdLoading] = useState(false)
  const [prodError, setProdError] = useState('')

  const jsonRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    modelsApi.list().then(setModels)
  }, [])

  const handleGenerate = async () => {
    if (!genModelId) { setGenError('Select a model'); return }
    setGenLoading(true)
    setGenError('')
    setGenRecords(null)
    try {
      const data = await generateApi.generate(genModelId, genCount)
      setGenRecords(data)
      setTimeout(() => jsonRef.current?.scrollIntoView({ behavior: 'smooth' }), 50)
    } catch {
      setGenError('Generation failed — is the backend running?')
    } finally {
      setGenLoading(false)
    }
  }

  const handleProduce = async () => {
    if (!prodModelId) { setProdError('Select a model'); return }
    setProdLoading(true)
    setProdError('')
    setProdResult(null)
    try {
      const result = await produceApi.produce(
        prodModelId,
        prodCount,
        prodTopic.trim() || undefined,
      )
      setProdResult(result)
    } catch {
      setProdError('Produce failed — is Kafka reachable?')
    } finally {
      setProdLoading(false)
    }
  }

  const modelSelect = (value: string, onChange: (v: string) => void) => (
    <select
      value={value}
      onChange={e => onChange(e.target.value)}
      className="w-full px-3 py-2 text-sm border border-slate-200 rounded-lg bg-white focus:outline-none focus:ring-2 focus:ring-indigo-300"
    >
      <option value="">— Select model —</option>
      {models.map(m => (
        <option key={m.id} value={m.id!}>{m.name}</option>
      ))}
    </select>
  )

  return (
    <div className="p-8">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-900">Generate & Produce</h1>
        <p className="text-sm text-slate-500 mt-1">Preview generated data or stream it straight to Kafka</p>
      </div>

      <div className="grid grid-cols-2 gap-6 mb-8">
        {/* Generate panel */}
        <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-5">
          <div className="flex items-center gap-2 mb-4">
            <span className="text-lg">⚡</span>
            <h2 className="text-base font-semibold text-slate-800">Generate Preview</h2>
          </div>
          <p className="text-xs text-slate-500 mb-4">Returns records as JSON — no Kafka required.</p>

          <div className="space-y-3">
            <div>
              <label className="block text-xs font-medium text-slate-600 mb-1">Model</label>
              {modelSelect(genModelId, setGenModelId)}
            </div>
            <div>
              <label className="block text-xs font-medium text-slate-600 mb-1">Count (max 10 000)</label>
              <input
                type="number"
                min="1" max="10000"
                value={genCount}
                onChange={e => setGenCount(Number(e.target.value))}
                className="w-full px-3 py-2 text-sm border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-300"
              />
            </div>
          </div>

          {genError && <p className="mt-3 text-xs text-red-600">{genError}</p>}

          <button
            onClick={handleGenerate}
            disabled={genLoading}
            className="mt-4 w-full px-4 py-2.5 bg-emerald-600 text-white text-sm font-medium rounded-lg hover:bg-emerald-700 disabled:opacity-50 transition-colors"
          >
            {genLoading ? 'Generating…' : 'Generate'}
          </button>
        </div>

        {/* Produce panel */}
        <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-5">
          <div className="flex items-center gap-2 mb-4">
            <span className="text-lg">📨</span>
            <h2 className="text-base font-semibold text-slate-800">Produce to Kafka</h2>
          </div>
          <p className="text-xs text-slate-500 mb-4">Sends records to the model's configured topic (or override below).</p>

          <div className="space-y-3">
            <div>
              <label className="block text-xs font-medium text-slate-600 mb-1">Model</label>
              {modelSelect(prodModelId, setProdModelId)}
            </div>
            <div>
              <label className="block text-xs font-medium text-slate-600 mb-1">Count (max 100 000)</label>
              <input
                type="number"
                min="1" max="100000"
                value={prodCount}
                onChange={e => setProdCount(Number(e.target.value))}
                className="w-full px-3 py-2 text-sm border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-300"
              />
            </div>
            <div>
              <label className="block text-xs font-medium text-slate-600 mb-1">
                Topic override <span className="font-normal text-slate-400">(optional)</span>
              </label>
              <input
                type="text"
                value={prodTopic}
                onChange={e => setProdTopic(e.target.value)}
                placeholder="Leave blank to use model's topic"
                className="w-full px-3 py-2 text-sm border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-300"
              />
            </div>
          </div>

          {prodError && <p className="mt-3 text-xs text-red-600">{prodError}</p>}

          {prodResult && (
            <div className="mt-3 px-3 py-2.5 bg-emerald-50 border border-emerald-200 rounded-lg text-xs">
              <span className="text-emerald-700 font-semibold">✓ {prodResult.sent} records</span>
              <span className="text-emerald-600"> sent to </span>
              <code className="font-mono text-emerald-800">{prodResult.topic}</code>
            </div>
          )}

          <button
            onClick={handleProduce}
            disabled={prodLoading}
            className="mt-4 w-full px-4 py-2.5 bg-indigo-600 text-white text-sm font-medium rounded-lg hover:bg-indigo-700 disabled:opacity-50 transition-colors"
          >
            {prodLoading ? 'Sending…' : 'Produce to Kafka'}
          </button>
        </div>
      </div>

      {/* Results table */}
      {genRecords && genRecords.length > 0 && (
        <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden" ref={jsonRef as React.RefObject<HTMLDivElement>}>
          <div className="flex items-center justify-between px-5 py-3 border-b border-slate-100">
            <h2 className="text-sm font-semibold text-slate-700">
              Generated Records
              <span className="ml-2 text-xs font-normal text-slate-400">{genRecords.length} rows</span>
            </h2>
            <button
              onClick={() => setGenRecords(null)}
              className="text-xs text-slate-400 hover:text-slate-600"
            >
              Clear
            </button>
          </div>

          <RecordsTable records={genRecords} />
        </div>
      )}
    </div>
  )
}

function RecordsTable({ records }: { records: Record<string, unknown>[] }) {
  const [view, setView] = useState<'table' | 'json'>('table')
  const keys = Object.keys(records[0] ?? {})

  return (
    <div>
      <div className="flex gap-2 px-5 py-2 border-b border-slate-100 bg-slate-50">
        <button
          onClick={() => setView('table')}
          className={`px-3 py-1 text-xs rounded-md font-medium transition-colors ${view === 'table' ? 'bg-white border border-slate-200 text-slate-800 shadow-sm' : 'text-slate-500 hover:text-slate-700'}`}
        >
          Table
        </button>
        <button
          onClick={() => setView('json')}
          className={`px-3 py-1 text-xs rounded-md font-medium transition-colors ${view === 'json' ? 'bg-white border border-slate-200 text-slate-800 shadow-sm' : 'text-slate-500 hover:text-slate-700'}`}
        >
          JSON
        </button>
      </div>

      {view === 'table' ? (
        <div className="overflow-x-auto max-h-[480px] overflow-y-auto">
          <table className="w-full text-xs">
            <thead className="bg-slate-50 sticky top-0">
              <tr>
                {keys.map(k => (
                  <th key={k} className="text-left px-3 py-2 font-semibold text-slate-600 border-b border-slate-200 whitespace-nowrap">
                    {k}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {records.map((row, i) => (
                <tr key={i} className={i % 2 === 0 ? 'bg-white' : 'bg-slate-50/60'}>
                  {keys.map(k => (
                    <td key={k} className="px-3 py-1.5 border-b border-slate-100 font-mono text-slate-700 whitespace-nowrap max-w-xs truncate">
                      {row[k] == null
                        ? <span className="text-slate-300 italic">null</span>
                        : String(row[k])}
                    </td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : (
        <pre className="text-xs font-mono p-4 overflow-auto max-h-[480px] text-slate-700 leading-relaxed">
          {JSON.stringify(records, null, 2)}
        </pre>
      )}
    </div>
  )
}
