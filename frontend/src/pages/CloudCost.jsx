import { useEffect, useState } from 'react'
import {
  getCloudDashboardSummary,
  getCloudRecords,
  syncCloudCosts,
} from '../services/api'
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  CartesianGrid,
} from 'recharts'

export default function CloudCost() {
  const [summary, setSummary] = useState(null)
  const [records, setRecords] = useState([])
  const [loading, setLoading] = useState(true)
  const [syncing, setSyncing] = useState(false)
  const [error, setError] = useState('')

  const loadData = async () => {
    try {
      setLoading(true)
      setError('')
      const [summaryData, recordsData] = await Promise.all([
        getCloudDashboardSummary(),
        getCloudRecords(),
      ])
      setSummary(summaryData)
      setRecords(recordsData)
    } catch (err) {
      setError(err.message || 'Error loading cloud costs')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadData()
  }, [])

  const handleSync = async () => {
    try {
      setSyncing(true)
      setError('')
      await syncCloudCosts()
      await loadData()
    } catch (err) {
      setError(err.message || 'Sync failed')
    } finally {
      setSyncing(false)
    }
  }

  const chartData = summary?.costByProvider
    ? Object.entries(summary.costByProvider).map(([provider, cost]) => ({
        provider,
        cost: Number(cost) || 0,
      }))
    : []

  return (
    <div style={{ maxWidth: '950px', margin: '2rem auto', padding: '0 1rem' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <div>
          <h1 style={{ margin: 0 }}>Cloud Cost Monitoring</h1>
          <p style={{ margin: '0.25rem 0 0', color: '#666' }}>
            Multi-cloud infrastructure expense and credit consumption tracking
          </p>
        </div>
        <button
          onClick={handleSync}
          disabled={syncing}
          style={{
            padding: '0.6rem 1.2rem',
            backgroundColor: syncing ? '#aaa' : '#0070f3',
            color: '#fff',
            border: 'none',
            borderRadius: '6px',
            cursor: syncing ? 'not-allowed' : 'pointer',
            fontWeight: 'bold',
          }}
        >
          {syncing ? 'Syncing...' : 'Sync Cloud Provider Data'}
        </button>
      </div>

      {error && (
        <div style={{ padding: '0.8rem', backgroundColor: '#ffebe6', color: '#de350b', borderRadius: '6px', marginBottom: '1rem' }}>
          {error}
        </div>
      )}

      {loading ? (
        <p>Loading cloud cost records...</p>
      ) : (
        <>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '1rem', marginBottom: '2rem' }}>
            <div style={{ padding: '1.2rem', background: '#f9f9f9', borderRadius: '8px', border: '1px solid #e2e8f0' }}>
              <div style={{ fontSize: '0.85rem', color: '#666' }}>Total Cloud Spending</div>
              <div style={{ fontSize: '1.8rem', fontWeight: 'bold', color: '#1a202c', marginTop: '0.3rem' }}>
                ₹{summary?.totalCloudCost ? Number(summary.totalCloudCost).toFixed(2) : '0.00'}
              </div>
            </div>

            <div style={{ padding: '1.2rem', background: '#f9f9f9', borderRadius: '8px', border: '1px solid #e2e8f0' }}>
              <div style={{ fontSize: '0.85rem', color: '#666' }}>Remaining Cloud Credits</div>
              <div style={{ fontSize: '1.8rem', fontWeight: 'bold', color: '#00875a', marginTop: '0.3rem' }}>
                ₹{summary?.totalRemainingCredits ? Number(summary.totalRemainingCredits).toFixed(2) : '0.00'}
              </div>
            </div>

            <div style={{ padding: '1.2rem', background: '#f9f9f9', borderRadius: '8px', border: '1px solid #e2e8f0' }}>
              <div style={{ fontSize: '0.85rem', color: '#666' }}>Active Services Monitored</div>
              <div style={{ fontSize: '1.8rem', fontWeight: 'bold', color: '#0052cc', marginTop: '0.3rem' }}>
                {records.length}
              </div>
            </div>
          </div>

          <div style={{ background: '#fff', padding: '1.5rem', borderRadius: '8px', border: '1px solid #e2e8f0', marginBottom: '2rem' }}>
            <h3 style={{ marginTop: 0 }}>Spending by Cloud Provider</h3>
            {chartData.length > 0 ? (
              <div style={{ width: '100%', height: 260 }}>
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={chartData} margin={{ top: 10, right: 20, left: 0, bottom: 5 }}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="provider" />
                    <YAxis />
                    <Tooltip formatter={(value) => ['₹' + Number(value).toFixed(2), 'Cost']} />
                    <Bar dataKey="cost" fill="#0070f3" radius={[4, 4, 0, 0]} />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            ) : (
              <p style={{ color: '#777' }}>No provider data to display yet.</p>
            )}
          </div>

          <div style={{ background: '#fff', padding: '1.5rem', borderRadius: '8px', border: '1px solid #e2e8f0' }}>
            <h3 style={{ marginTop: 0 }}>Cloud Resource Usage Details</h3>
            {records.length === 0 ? (
              <p style={{ color: '#777' }}>No cloud records found. Click "Sync Cloud Provider Data" to fetch usage records.</p>
            ) : (
              <div style={{ overflowX: 'auto' }}>
                <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
                  <thead>
                    <tr style={{ borderBottom: '2px solid #e2e8f0', color: '#4a5568' }}>
                      <th style={{ padding: '0.75rem 0.5rem' }}>Provider</th>
                      <th style={{ padding: '0.75rem 0.5rem' }}>Service</th>
                      <th style={{ padding: '0.75rem 0.5rem' }}>Usage</th>
                      <th style={{ padding: '0.75rem 0.5rem' }}>Billing Date</th>
                      <th style={{ padding: '0.75rem 0.5rem' }}>Credits Used</th>
                      <th style={{ padding: '0.75rem 0.5rem' }}>Cost</th>
                    </tr>
                  </thead>
                  <tbody>
                    {records.map((r) => (
                      <tr key={r.id} style={{ borderBottom: '1px solid #edf2f7' }}>
                        <td style={{ padding: '0.75rem 0.5rem', fontWeight: 'bold' }}>{r.provider}</td>
                        <td style={{ padding: '0.75rem 0.5rem' }}>{r.serviceName}</td>
                        <td style={{ padding: '0.75rem 0.5rem', color: '#718096' }}>{r.usageQuantity || 'N/A'}</td>
                        <td style={{ padding: '0.75rem 0.5rem', color: '#718096' }}>{r.billingDate}</td>
                        <td style={{ padding: '0.75rem 0.5rem', color: '#718096' }}>₹{Number(r.creditsUsed || 0).toFixed(2)}</td>
                        <td style={{ padding: '0.75rem 0.5rem', fontWeight: 'bold' }}>₹{Number(r.cost).toFixed(2)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </>
      )}
    </div>
  )
}