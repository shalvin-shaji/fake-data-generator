import { NavLink, Outlet } from 'react-router-dom'

const navItems = [
  { to: '/', label: 'Models', icon: '🗂' },
  { to: '/generate', label: 'Generate', icon: '⚡' },
]

export default function Layout() {
  return (
    <div className="flex h-screen overflow-hidden bg-slate-50">
      {/* Sidebar */}
      <aside className="w-52 shrink-0 bg-slate-900 text-slate-100 flex flex-col">
        <div className="px-5 py-5 border-b border-slate-700">
          <div className="text-xs font-semibold uppercase tracking-widest text-slate-400 mb-1">Fake Data</div>
          <div className="text-lg font-bold text-white">Generator</div>
        </div>
        <nav className="flex-1 py-4 space-y-1 px-2">
          {navItems.map(({ to, label, icon }) => (
            <NavLink
              key={to}
              to={to}
              end={to === '/'}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium transition-colors ${
                  isActive
                    ? 'bg-indigo-600 text-white'
                    : 'text-slate-300 hover:bg-slate-800 hover:text-white'
                }`
              }
            >
              <span>{icon}</span>
              {label}
            </NavLink>
          ))}
        </nav>
        <div className="px-5 py-4 border-t border-slate-700 text-xs text-slate-500">
          Phase 6 · API → :8081
        </div>
      </aside>

      {/* Main content */}
      <main className="flex-1 overflow-y-auto">
        <Outlet />
      </main>
    </div>
  )
}
