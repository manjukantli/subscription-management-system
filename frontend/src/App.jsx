import { BrowserRouter, Routes, Route, Link } from 'react-router-dom'
import Login from './pages/Login'
import Register from './pages/Register'
import Dashboard from './pages/Dashboard'
import History from './pages/History'
import CloudCost from './pages/CloudCost'

function Navigation() {
  return (
    <nav style={{ padding: '1rem', borderBottom: '1px solid #eaeaea', textAlign: 'center', backgroundColor: '#fdfdfd' }}>
      <Link to="/dashboard" style={{ marginRight: '1rem', textDecoration: 'none', color: '#0070f3', fontWeight: '500' }}>Subscriptions</Link>
      {' | '}
      <Link to="/cloud" style={{ margin: '0 1rem', textDecoration: 'none', color: '#0070f3', fontWeight: '500' }}>Cloud Costs</Link>
      {' | '}
      <Link to="/history" style={{ marginLeft: '1rem', textDecoration: 'none', color: '#0070f3', fontWeight: '500' }}>History</Link>
    </nav>
  )
}

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
              <Navigation />
              <Dashboard />
            </>
          }
        />

        <Route
          path="/cloud"
          element={
            <>
              <Navigation />
              <CloudCost />
            </>
          }
        />

        <Route
          path="/history"
          element={
            <>
              <Navigation />
              <History />
            </>
          }
        />
      </Routes>
    </BrowserRouter>
  )
}

export default App