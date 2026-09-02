import { BrowserRouter, Routes, Route, Link } from 'react-router-dom'
import Login from './pages/Login'
import Register from './pages/Register'
import Dashboard from './pages/Dashboard'
import History from './pages/History'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        <Route
          path="/dashboard"
          element={
            <>
              <nav>
                <Link to="/dashboard">Subscriptions</Link>
                {' | '}
                <Link to="/history">History</Link>
              </nav>

              <Dashboard />
            </>
          }
        />

        <Route
          path="/history"
          element={
            <>
              <nav>
                <Link to="/dashboard">Subscriptions</Link>
                {' | '}
                <Link to="/history">History</Link>
              </nav>

              <History />
            </>
          }
        />
      </Routes>
    </BrowserRouter>
  )
}

export default App