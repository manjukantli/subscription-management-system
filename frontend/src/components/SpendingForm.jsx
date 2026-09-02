import { useState } from 'react'
import { addSpending } from '../services/api'

function SpendingForm({ subscriptions, onSpendingAdded }) {
  const [subscriptionId, setSubscriptionId] = useState(
    subscriptions.length > 0 ? subscriptions[0].id : ''
  )
  const [amount, setAmount] = useState('')
  const [spentDate, setSpentDate] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (event) => {
    event.preventDefault()

    setError('')
    setLoading(true)

    try {
      const spending = {
        amount: Number(amount),
        spentDate,
        subscriptionId: Number(subscriptionId),
      }

      const newSpending = await addSpending(spending)

      onSpendingAdded(newSpending)

      setAmount('')
      setSpentDate('')
    } catch (error) {
      setError(error.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className="spending-form-section">
      <div className="form-header">
        <h2>Record Expense</h2>
        <p>Manually record a subscription payment.</p>
      </div>

      {subscriptions.length === 0 ? (
        <div className="empty-form-state">
          <p>
            Add a subscription before recording an expense.
          </p>
        </div>
      ) : (
        <form onSubmit={handleSubmit}>
          <div>
            <label>Subscription</label>

            <select
              value={subscriptionId}
              onChange={(event) =>
                setSubscriptionId(event.target.value)
              }
              required
            >
              {subscriptions.map((subscription) => (
                <option
                  key={subscription.id}
                  value={subscription.id}
                >
                  {subscription.serviceName}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label>Amount</label>

            <input
              type="number"
              step="0.01"
              min="0.01"
              value={amount}
              onChange={(event) =>
                setAmount(event.target.value)
              }
              placeholder="500"
              required
            />
          </div>

          <div>
            <label>Spent Date</label>

            <input
              type="date"
              value={spentDate}
              onChange={(event) =>
                setSpentDate(event.target.value)
              }
              required
            />
          </div>

          {error && (
            <div className="form-error">
              {error}
            </div>
          )}

          <button type="submit" disabled={loading}>
            {loading
              ? 'Recording...'
              : 'Record Expense'}
          </button>
        </form>
      )}
    </section>
  )
}

export default SpendingForm