import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Layout from './components/Layout'
import ModelsPage from './pages/ModelsPage'
import ModelBuilderPage from './pages/ModelBuilderPage'
import GeneratePage from './pages/GeneratePage'

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<ModelsPage />} />
          <Route path="models/new" element={<ModelBuilderPage />} />
          <Route path="models/:id/edit" element={<ModelBuilderPage />} />
          <Route path="generate" element={<GeneratePage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}
