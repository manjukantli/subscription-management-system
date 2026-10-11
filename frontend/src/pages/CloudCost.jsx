import { useEffect, useState } from 'react'
import {
  getCloudDashboardSummary,
  getCloudRecords,
  syncCloudCosts,
  addCloudRecord,
  deleteCloudRecord,
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
  const [showAddForm, setShowAddForm] = useState(false)

  const [formData, setFormData] = useState({
    provider: 'AWS',
    serviceName: '',
    usageQuantity: '',
    cost: '',
    creditsUsed: '0',
    creditsRemaining: '500',
    billingDate: new Date().toISOString().split('T')[0],
  })

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

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this cloud service entry?')) return
    try {
      await deleteCloudRecord(id)
      await loadData()
    } catch (err) {
      setError(err.message || 'Failed to delete record')
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!formData.serviceName || !formData.cost) {
      alert('Please provide at least a Service Name and Cost')
      return
    }
    try {
      await addCloudRecord({
        ...formData,
        cost: Number(formData.cost),
        creditsUsed: Number(formData.creditsUsed),
        creditsRemaining: Number(formData.creditsRemaining),
      })
      setShowAddForm(false)
      setFormData({
        provider: 'AWS',
        serviceName: '',
        usageQuantity: '',
        cost: '',
        creditsUsed: '0',
        creditsRemaining: '500',
        billingDate: new Date().toISOString().split('T')[0],
      })
      await loadData()
    } catch (err) {
      setError(err.message || 'Failed to add record')
    }
  }

  const chartData = summary?.costByProvider
    ? Object.entries(summary.costByProvider).map(([provider, cost]) => ({
        provider,
        cost: Number(cost) || 0,
      }))
    : []

  return (
    <div style={{ maxWidth: '980px', margin: '2rem auto', padding: '0 1rem' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem', flexWrap: 'wrap', gap: '1rem' }}>
        <div>
          <h1 style={{ margin: 0 }}>Cloud Cost Monitoring</h1>
          <p style={{ margin: '0.25rem 0 0', color: '#666' }}>
            Multi-cloud infrastructure expense, prediction, and anomaly monitoring
          </p>
        </div>
        <div style={{ display: 'flex', gap: '0.5rem' }}>
          <button
            onClick={() => setShowAddForm(!showAddForm)}
            style={{
              padding: '0.6rem 1.2rem',
              backgroundColor: '#10b981',
              color: '#fff',
              border: 'none',
              borderRadius: '6px',
              cursor: 'pointer',
              fontWeight: 'bold',
            }}
          >
            {showAddForm ? 'Cancel' : '+ Add Service'}
          </button>
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
            {syncing ? 'Syncing...' : 'Sync Cloud Data'}
          </button>
        </div>
      </div>

      {showAddForm && (
        <form onSubmit={handleSubmit} style={{ background: '#f8fafc', padding: '1.25rem', borderRadius: '8px', border: '1px solid #cbd5e1', marginBottom: '1.5rem' }}>
          <h3 style={{ margin: '0 0 1rem' }}>Add Cloud Cost Record</h3>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '0.8rem' }}>
            <div>
              <label style={{ display: 'block', fontSize: '0.85rem', marginBottom: '0.25rem' }}>Provider</label>
              <select
                value={formData.provider}
                onChange={(e) => setFormData({ ...formData, provider: e.target.value })}
                style={{ width: '100%', padding: '0.45rem', borderRadius: '4px', border: '1px solid #ccc' }}
              >
                <option value="AWS">AWS</option>
                <option value="GCP">GCP</option>
                <option value="AZURE">AZURE</option>
              </select>
            </div>
            <div>
              <label style={{ display: 'block', fontSize: '0.85rem', marginBottom: '0.25rem' }}>Service Name</label>
              <input
                type="text"
                placeholder="e.g. AWS Lambda"
                value={formData.serviceName}
                onChange={(e) => setFormData({ ...formData, serviceName: e.target.value })}
                style={{ width: '100%', padding: '0.45rem', borderRadius: '4px', border: '1px solid #ccc' }}
                required
              />
            </div>
            <div>
              <label style={{ display: 'block', fontSize: '0.85rem', marginBottom: '0.25rem' }}>Usage Quantity</label>
              <input
                type="text"
                placeholder="e.g. 5M Invocations"
                value={formData.usageQuantity}
                onChange={(e) => setFormData({ ...formData, usageQuantity: e.target.value })}
                style={{ width: '100%', padding: '0.45rem', borderRadius: '4px', border: '1px solid #ccc' }}
              />
            </div>
            <div>
              <label style={{ display: 'block', fontSize: '0.85rem', marginBottom: '0.25rem' }}>Cost (₹)</label>
              <input
                type="number"
                step="0.01"
                placeholder="e.g. 450.00"
                value={formData.cost}
                onChange={(e) => setFormData({ ...formData, cost: e.target.value })}
                style={{ width: '100%', padding: '0.45rem', borderRadius: '4px', border: '1px solid #ccc' }}
                required
              />
            </div>
            <div>
              <label style={{ display: 'block', fontSize: '0.85rem', marginBottom: '0.25rem' }}>Credits Remaining (₹)</label>
              <input
                type="number"
                step="0.01"
                value={formData.creditsRemaining}
                onChange={(e) => setFormData({ ...formData, creditsRemaining: e.target.value })}
                style={{ width: '100%', padding: '0.45rem', borderRadius: '4px', border: '1px solid #ccc' }}
              />
            </div>
            <div>
              <label style={{ display: 'block', fontSize: '0.85rem', marginBottom: '0.25rem' }}>Billing Date</label>
              <input
                type="date"
                value={formData.billingDate}
                onChange={(e) => setFormData({ ...formData, billingDate: e.target.value })}
                style={{ width: '100%', padding: '0.45rem', borderRadius: '4px', border: '1px solid #ccc' }}
              />
            </div>
          </div>
          <button type="submit" style={{ marginTop: '1rem', padding: '0.5rem 1.2rem', backgroundColor: '#0070f3', color: '#fff', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}>
            Save Service
          </button>
        </form>
      )}

      {error && (
        <div style={{ padding: '0.8rem', backgroundColor: '#ffebe6', color: '#de350b', borderRadius: '6px', marginBottom: '1rem' }}>
          {error}
        </div>
      )}

      {summary?.anomalyAlerts && summary.anomalyAlerts.length > 0 && (
        <div style={{ padding: '1rem', backgroundColor: '#fff4e5', border: '1px solid #ffd599', borderRadius: '8px', marginBottom: '1.5rem' }}>
          <h4 style={{ margin: '0 0 0.5rem', color: '#b76e00' }}>⚠️ Cloud Cost Anomalies Detected</h4>
          <ul style={{ margin: 0, paddingLeft: '1.2rem', color: '#874d00' }}>
            {summary.anomalyAlerts.map((alert, idx) => (
              <li key={idx}>{alert}</li>
            ))}
          </ul>
        </div>
      )}

      {loading ? (
        <p>Loading cloud cost records...</p>
      ) : (
        <>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1rem', marginBottom: '2rem' }}>
            <div style={{ padding: '1.2rem', background: '#f9f9f9', borderRadius: '8px', border: '1px solid #e2e8f0' }}>
              <div style={{ fontSize: '0.85rem', color: '#666' }}>Total Cloud Spending</div>
              <div style={{ fontSize: '1.6rem', fontWeight: 'bold', color: '#1a202c', marginTop: '0.3rem' }}>
                ₹{summary?.totalCloudCost ? Number(summary.totalCloudCost).toFixed(2) : '0.00'}
              </div>
            </div>

            <div style={{ padding: '1.2rem', background: '#f9f9f9', borderRadius: '8px', border: '1px solid #e2e8f0' }}>
              <div style={{ fontSize: '0.85rem', color: '#666' }}>Predicted Next Month (FR-18)</div>
              <div style={{ fontSize: '1.6rem', fontWeight: 'bold', color: '#d97706', marginTop: '0.3rem' }}>
                ₹{summary?.predictedNextMonthCost ? Number(summary.predictedNextMonthCost).toFixed(2) : '0.00'}
              </div>
            </div>

            <div style={{ padding: '1.2rem', background: '#f9f9f9', borderRadius: '8px', border: '1px solid #e2e8f0' }}>
              <div style={{ fontSize: '0.85rem', color: '#666' }}>Remaining Cloud Credits</div>
              <div style={{ fontSize: '1.6rem', fontWeight: 'bold', color: '#00875a', marginTop: '0.3rem' }}>
                ₹{summary?.totalRemainingCredits ? Number(summary.totalRemainingCredits).toFixed(2) : '0.00'}
              </div>
            </div>

            <div style={{ padding: '1.2rem', background: '#f9f9f9', borderRadius: '8px', border: '1px solid #e2e8f0' }}>
              <div style={{ fontSize: '0.85rem', color: '#666' }}>Monitored Services</div>
              <div style={{ fontSize: '1.6rem', fontWeight: 'bold', color: '#0052cc', marginTop: '0.3rem' }}>
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
              <p style={{ color: '#777' }}>No cloud records found. Use "+ Add Service" or click "Sync Cloud Data".</p>
            ) : (
              <div style={{ overflowX: 'auto' }}>
                <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
                  <thead>
                    <tr style={{ borderBottom: '2px solid #e2e8f0', color: '#4a5568' }}>
                      <th style={{ padding: '0.75rem 0.5rem' }}>Provider</th>
                      <th style={{ padding: '0.75rem 0.5rem' }}>Service</th>
                      <th style={{ padding: '0.75rem 0.5rem' }}>Usage</th>
                      <th style={{ padding: '0.75rem 0.5rem' }}>Billing Date</th>
                      <th style={{ padding: '0.75rem 0.5rem' }}>Credits Left</th>
                      <th style={{ padding: '0.75rem 0.5rem' }}>Cost</th>
                      <th style={{ padding: '0.75rem 0.5rem', textAlign: 'center' }}>Action</th>
                    </tr>
                  </thead>
                  <tbody>
                    {records.map((r) => (
                      <tr key={r.id} style={{ borderBottom: '1px solid #edf2f7' }}>
                        <td style={{ padding: '0.75rem 0.5rem', fontWeight: 'bold' }}>{r.provider}</td>
                        <td style={{ padding: '0.75rem 0.5rem' }}>{r.serviceName}</td>
                        <td style={{ padding: '0.75rem 0.5rem', color: '#718096' }}>{r.usageQuantity || 'N/A'}</td>
                        <td style={{ padding: '0.75rem 0.5rem', color: '#718096' }}>{r.billingDate}</td>
                        <td style={{ padding: '0.75rem 0.5rem', color: '#718096' }}>₹{Number(r.creditsRemaining || 0).toFixed(2)}</td>
                        <td style={{ padding: '0.75rem 0.5rem', fontWeight: 'bold' }}>₹{Number(r.cost).toFixed(2)}</td>
                        <td style={{ padding: '0.75rem 0.5rem', textAlign: 'center' }}>
                          <button
                            onClick={() => handleDelete(r.id)}
                            style={{
                              backgroundColor: '#fee2e2',
                              color: '#dc2626',
                              border: 'none',
                              padding: '0.35rem 0.75rem',
                              borderRadius: '4px',
                              cursor: 'pointer',
                              fontWeight: '600',
                            }}
                          >
                            Delete
                          </button>
                        </td>
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