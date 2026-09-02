import { useEffect, useState } from 'react'
import { getSpendingHistory } from '../services/api'
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from 'recharts'

function History() {
  const [history, setHistory] = useState([])
  const [error, setError] = useState('')

  useEffect(() => {
    const loadHistory = async () => {
      try {
        const data = await getSpendingHistory()
        setHistory(data)
      } catch (error) {
        setError(error.message)
      }
    }

    loadHistory()
  }, [])

  const chartData = history.map((item) => ({
    month: `${item.year}-${String(item.month).padStart(2, '0')}`,
    total: item.total,
  }))

  return (
    <main className="history-page">
      <div className="history-header">
        <h1>Spending History</h1>
        <p>Track your monthly subscription spending.</p>
      </div>

      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      {history.length === 0 ? (
        <div className="empty-state">
          <h3>No spending history yet</h3>
          <p>
            Your spending records will appear here.
          </p>
        </div>
      ) : (
        <>
          <section className="chart-card">
            <h2>Monthly Spending</h2>

            <div className="chart-container">
              <ResponsiveContainer
                width="100%"
                height={320}
              >
                <LineChart data={chartData}>
                  <CartesianGrid
                    strokeDasharray="3 3"
                  />

                  <XAxis dataKey="month" />

                  <YAxis />

                  <Tooltip
                    formatter={(value) => [
                      `₹${value}`,
                      'Spending',
                    ]}
                  />

                  <Line
                    type="monotone"
                    dataKey="total"
                    strokeWidth={2}
                  />
                </LineChart>
              </ResponsiveContainer>
            </div>
          </section>

          <section className="history-card">
            <h2>History</h2>

            <div className="history-table">
              {history.map((item) => (
                <div
                  className="history-row"
                  key={`${item.year}-${item.month}`}
                >
                  <span>
                    {item.year}-
                    {String(item.month).padStart(2, '0')}
                  </span>

                  <strong>
                    ₹{item.total}
                  </strong>
                </div>
              ))}
            </div>
          </section>
        </>
      )}
    </main>
  )
}

export default History