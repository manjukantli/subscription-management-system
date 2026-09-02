import { useEffect, useState } from 'react'
import { getDashboard } from '../services/api'

function DashboardStats() {
  const [dashboard, setDashboard] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    const loadDashboard = async () => {
      try {
        const data = await getDashboard()
        setDashboard(data)
      } catch (error) {
        setError(error.message)
      }
    }

    loadDashboard()
  }, [])

  if (error) {
    return <p className="error-message">{error}</p>
  }

  if (!dashboard) {
    return <p>Loading dashboard...</p>
  }

  return (
    <section className="dashboard-stats">
      <h2>Overview</h2>

      <div className="stats-grid">
        <div className="stat-card">
          <span className="stat-label">
            Total Subscriptions
          </span>

          <strong className="stat-value">
            {dashboard.totalSubscriptions}
          </strong>
        </div>

        <div className="stat-card">
          <span className="stat-label">
            Monthly Spending
          </span>

          <strong className="stat-value">
            ₹{dashboard.monthlySpending}
          </strong>
        </div>

        <div className="stat-card">
          <span className="stat-label">
            Category Spending
          </span>

          <div className="category-list">
            {Object.entries(
              dashboard.categorySpending || {}
            ).map(([category, amount]) => (
              <div
                className="category-row"
                key={category}
              >
                <span>{category}</span>
                <strong>₹{amount}</strong>
              </div>
            ))}
          </div>
        </div>
      </div>
    </section>
  )
}

export default DashboardStats