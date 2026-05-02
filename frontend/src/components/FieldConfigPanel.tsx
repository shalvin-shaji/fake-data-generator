import type { FieldDefinition, FieldType } from '../api/types'

interface Props {
  field: FieldDefinition
  onChange: (config: Record<string, unknown>) => void
}

/** Renders the type-specific config inputs for a field. */
export default function FieldConfigPanel({ field, onChange }: Props) {
  const cfg = field.config
  const set = (key: string, value: unknown) => onChange({ ...cfg, [key]: value })

  const num = (key: string, def: number) =>
    <input
      type="number"
      value={cfg[key] as number ?? def}
      onChange={e => set(key, Number(e.target.value))}
      className="w-28 px-2 py-1 text-xs border border-slate-200 rounded"
    />

  const str = (key: string, placeholder = '') =>
    <input
      type="text"
      value={cfg[key] as string ?? ''}
      placeholder={placeholder}
      onChange={e => set(key, e.target.value)}
      className="flex-1 px-2 py-1 text-xs border border-slate-200 rounded"
    />

  const row = (label: string, input: React.ReactNode) => (
    <label className="flex items-center gap-2 text-xs text-slate-600">
      <span className="w-24 shrink-0 text-right">{label}</span>
      {input}
    </label>
  )

  const type: FieldType = field.type

  if (type === 'INTEGER') return (
    <div className="space-y-1">
      {row('min', num('min', 0))}
      {row('max', num('max', 1000000))}
    </div>
  )

  if (type === 'DECIMAL') return (
    <div className="space-y-1">
      {row('min', num('min', 0))}
      {row('max', num('max', 1000))}
      {row('scale', num('scale', 2))}
    </div>
  )

  if (type === 'BOOLEAN') return (
    <div className="space-y-1">
      {row('trueProbability', num('trueProbability', 0.5))}
    </div>
  )

  if (type === 'STRING') return (
    <div className="space-y-1">
      {row('minLength', num('minLength', 5))}
      {row('maxLength', num('maxLength', 20))}
    </div>
  )

  if (type === 'SEQUENCE') return (
    <div className="space-y-1">
      {row('start', num('start', 1))}
      {row('prefix', str('prefix', 'e.g. TXN-'))}
    </div>
  )

  if (type === 'REFERENCE') return (
    <div className="space-y-1">
      {row('modelName', str('modelName', 'Parent model name'))}
      {row('field', str('field', 'Field name in parent'))}
    </div>
  )

  if (type === 'DATE') return (
    <div className="space-y-1">
      <label className="flex items-center gap-2 text-xs text-slate-600">
        <span className="w-24 shrink-0 text-right">mode</span>
        <select
          value={cfg['mode'] as string ?? 'range'}
          onChange={e => set('mode', e.target.value)}
          className="px-2 py-1 text-xs border border-slate-200 rounded"
        >
          <option value="range">range</option>
          <option value="today">today</option>
        </select>
      </label>
      {(cfg['mode'] ?? 'range') === 'range' && <>
        {row('daysBack', num('daysBack', 365))}
        {row('daysForward', num('daysForward', 0))}
      </>}
    </div>
  )

  if (type === 'TIMESTAMP') return (
    <div className="space-y-1">
      <label className="flex items-center gap-2 text-xs text-slate-600">
        <span className="w-24 shrink-0 text-right">mode</span>
        <select
          value={cfg['mode'] as string ?? 'now'}
          onChange={e => set('mode', e.target.value)}
          className="px-2 py-1 text-xs border border-slate-200 rounded"
        >
          <option value="now">now</option>
          <option value="range">range</option>
        </select>
      </label>
      {(cfg['mode']) === 'range' && <>
        {row('secondsBack', num('secondsBack', 86400))}
        {row('secondsForward', num('secondsForward', 0))}
      </>}
    </div>
  )

  if (type === 'DATE_OF_BIRTH') return (
    <div className="space-y-1">
      {row('minAge', num('minAge', 18))}
      {row('maxAge', num('maxAge', 90))}
    </div>
  )

  if (type === 'ACCOUNT_NUMBER') return (
    <div className="space-y-1">
      {row('length', num('length', 10))}
    </div>
  )

  if (type === 'TICKER') return (
    <div className="space-y-1">
      <label className="flex flex-col gap-1 text-xs text-slate-600">
        <span>Custom tickers (comma-separated, leave blank for defaults)</span>
        <textarea
          rows={2}
          value={(cfg['values'] as string[] ?? []).join(', ')}
          onChange={e => {
            const vals = e.target.value.split(',').map(v => v.trim()).filter(Boolean)
            set('values', vals.length ? vals : undefined)
          }}
          placeholder="AAPL, MSFT, GOOGL, ..."
          className="px-2 py-1 text-xs border border-slate-200 rounded resize-none"
        />
      </label>
    </div>
  )

  if (type === 'REGEX') return (
    <div className="space-y-1">
      {row('pattern', str('pattern', 'e.g. [A-Z]{2}[0-9]{4}'))}
    </div>
  )

  if (type === 'ENUM') return (
    <div className="space-y-1">
      <label className="flex flex-col gap-1 text-xs text-slate-600">
        <span>Values (comma-separated)</span>
        <textarea
          rows={2}
          value={(cfg['values'] as string[] ?? []).join(', ')}
          onChange={e => {
            const vals = e.target.value.split(',').map(v => v.trim()).filter(Boolean)
            set('values', vals)
          }}
          placeholder="ACTIVE, DORMANT, CLOSED"
          className="px-2 py-1 text-xs border border-slate-200 rounded resize-none"
        />
      </label>
    </div>
  )

  if (type === 'WEIGHTED_ENUM') return (
    <WeightedEnumConfig cfg={cfg} onChange={onChange} />
  )

  // Types with no config (UUID, personal, address, finance defaults)
  return <span className="text-xs text-slate-400 italic">No configuration needed</span>
}

// ---------- Weighted enum sub-component ----------

interface WeightedItem { value: string; weight: number }

function WeightedEnumConfig({
  cfg,
  onChange,
}: {
  cfg: Record<string, unknown>
  onChange: (c: Record<string, unknown>) => void
}) {
  const items: WeightedItem[] = (cfg['values'] as WeightedItem[]) ?? [
    { value: '', weight: 1 },
  ]

  const update = (next: WeightedItem[]) => onChange({ ...cfg, values: next })

  const setItem = (i: number, patch: Partial<WeightedItem>) => {
    const next = items.map((item, idx) => idx === i ? { ...item, ...patch } : item)
    update(next)
  }

  return (
    <div className="space-y-1">
      <div className="text-xs text-slate-500 mb-1">Value → weight</div>
      {items.map((item, i) => (
        <div key={i} className="flex items-center gap-2">
          <input
            type="text"
            value={item.value}
            placeholder="value"
            onChange={e => setItem(i, { value: e.target.value })}
            className="flex-1 px-2 py-1 text-xs border border-slate-200 rounded"
          />
          <input
            type="number"
            value={item.weight}
            step="0.05"
            min="0"
            max="1"
            onChange={e => setItem(i, { weight: Number(e.target.value) })}
            className="w-20 px-2 py-1 text-xs border border-slate-200 rounded"
          />
          <button
            type="button"
            onClick={() => update(items.filter((_, idx) => idx !== i))}
            className="text-red-400 hover:text-red-600 text-xs px-1"
          >
            ✕
          </button>
        </div>
      ))}
      <button
        type="button"
        onClick={() => update([...items, { value: '', weight: 0.1 }])}
        className="text-xs text-indigo-600 hover:text-indigo-800 mt-1"
      >
        + Add option
      </button>
    </div>
  )
}
