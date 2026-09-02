import { useEffect, useState } from 'react'
import {
  getSubscriptions,
  deleteSubscription,
  getSubscriptionSpending,
  confirmRenewal,
  cancelRenewal,
} from '../services/api'
import SubscriptionForm from '../components/SubscriptionForm'
import DashboardStats from '../components/DashboardStats'

function Dashboard() {
  const [subscriptions, setSubscriptions] = useState([])
  const [error, setError] = useState('')
  const [editingSubscription, setEditingSubscription] = useState(null)
  const [selectedSubscription, setSelectedSubscription] = useState(null)
  const [subscriptionHistory, setSubscriptionHistory] = useState([])
  const [historyLoading, setHistoryLoading] = useState(false)

  useEffect(() => {
    const loadSubscriptions = async () => {
      try {
        const data = await getSubscriptions()
        setSubscriptions(data)
      } catch (error) {
        setError(error.message)
      }
    }

    loadSubscriptions()
  }, [])

  const handleSubscriptionAdded = (newSubscription) => {
    setSubscriptions((currentSubscriptions) => [
      ...currentSubscriptions,
      newSubscription,
    ])
  }

  const handleDelete = async (id) => {
    try {
      await deleteSubscription(id)

      setSubscriptions((currentSubscriptions) =>
        currentSubscriptions.filter(
          (subscription) => subscription.id !== id
        )
      )

      if (selectedSubscription?.id === id) {
        setSelectedSubscription(null)
        setSubscriptionHistory([])
      }
    } catch (error) {
      setError(error.message)
    }
  }

  const handleSubscriptionUpdated = (updatedSubscription) => {
    setSubscriptions((currentSubscriptions) =>
      currentSubscriptions.map((subscription) =>
        subscription.id === updatedSubscription.id
          ? updatedSubscription
          : subscription
      )
    )

    if (selectedSubscription?.id === updatedSubscription.id) {
      setSelectedSubscription(updatedSubscription)
    }

    setEditingSubscription(null)
  }

  const handleSubscriptionClick = async (subscription) => {
    if (selectedSubscription?.id === subscription.id) {
      setSelectedSubscription(null)
      setSubscriptionHistory([])
      return
    }

    setSelectedSubscription(subscription)
    setSubscriptionHistory([])
    setHistoryLoading(true)
    setError('')

    try {
      const history = await getSubscriptionSpending(
        subscription.id
      )

      setSubscriptionHistory(history)
    } catch (error) {
      setError(error.message)
    } finally {
      setHistoryLoading(false)
    }
  }

  const getToday = () => {
    const today = new Date()

    const year = today.getFullYear()
    const month = String(today.getMonth() + 1).padStart(2, '0')
    const day = String(today.getDate()).padStart(2, '0')

    return `${year}-${month}-${day}`
  }

  const isRenewalDue = (subscription) => {
    const today = getToday()

    return (
      subscription.status === 'ACTIVE' &&
      subscription.renewalDate <= today
    )
  }

  const handleRenew = async (subscription) => {
    try {
      setError('')

      const updatedSubscription =
        await confirmRenewal(subscription.id)

      setSubscriptions((currentSubscriptions) =>
        currentSubscriptions.map((item) =>
          item.id === updatedSubscription.id
            ? updatedSubscription
            : item
        )
      )

      if (selectedSubscription?.id === updatedSubscription.id) {
        setSelectedSubscription(updatedSubscription)

        const history = await getSubscriptionSpending(
          updatedSubscription.id
        )

        setSubscriptionHistory(history)
      }
    } catch (error) {
      setError(error.message)
    }
  }

  const handleCancelRenewal = async (subscription) => {
    try {
      setError('')

      const updatedSubscription =
        await cancelRenewal(subscription.id)

      setSubscriptions((currentSubscriptions) =>
        currentSubscriptions.map((item) =>
          item.id === updatedSubscription.id
            ? updatedSubscription
            : item
        )
      )

      if (selectedSubscription?.id === updatedSubscription.id) {
        setSelectedSubscription(updatedSubscription)
      }
    } catch (error) {
      setError(error.message)
    }
  }

  return (
    <main className="dashboard">
      <div className="dashboard-header">
        <div>
          <h1>Subscription Dashboard</h1>
          <p>Manage your subscriptions and track your spending.</p>
        </div>
      </div>

      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      <DashboardStats />

      <section className="subscription-form-section">
        <SubscriptionForm
          editingSubscription={editingSubscription}
          onSubscriptionAdded={handleSubscriptionAdded}
          onSubscriptionUpdated={handleSubscriptionUpdated}
          onCancelEdit={() => setEditingSubscription(null)}
        />
      </section>

      <section className="subscriptions-section">
        <div className="section-header">
          <div>
            <h2>My Subscriptions</h2>
            <p>
              {subscriptions.length}{' '}
              {subscriptions.length === 1
                ? 'subscription'
                : 'subscriptions'}
            </p>
          </div>
        </div>

        {subscriptions.length === 0 ? (
          <div className="empty-state">
            <h3>No subscriptions yet</h3>
            <p>
              Add your first subscription using the form above.
            </p>
          </div>
        ) : (
          <div className="subscription-grid">
            {subscriptions.map((subscription) => {
              const isSelected =
                selectedSubscription?.id === subscription.id

              const renewalDue =
                isRenewalDue(subscription)

              return (
                <article
                  className="subscription-card"
                  key={subscription.id}
                >
                  <div className="subscription-card-header">
                    <div>
                      <h3>
                        {subscription.serviceName}
                      </h3>

                      <span className="subscription-category">
                        {subscription.category}
                      </span>
                    </div>

                    <span
                      className={`status-badge ${
                        subscription.status === 'ACTIVE'
                          ? 'status-active'
                          : 'status-cancelled'
                      }`}
                    >
                      {subscription.status}
                    </span>
                  </div>

                  <div className="subscription-price">
                    ₹{subscription.cost}
                    <span>
                      / {subscription.billingCycle.toLowerCase()}
                    </span>
                  </div>

                  <div className="subscription-details">
                    <div className="detail-row">
                      <span>Renewal Date</span>
                      <strong>
                        {subscription.renewalDate}
                      </strong>
                    </div>

                    <div className="detail-row">
                      <span>Date Added</span>
                      <strong>
                        {subscription.dateAdded}
                      </strong>
                    </div>

                    <div className="detail-row">
                      <span>Source</span>
                      <strong>Manual</strong>
                    </div>
                  </div>

                  {renewalDue && (
                    <div className="renewal-box">
                      <strong>Renewal is due</strong>

                      <div className="renewal-actions">
                        <button
                          className="renew-button"
                          onClick={() =>
                            handleRenew(subscription)
                          }
                        >
                          Renewed
                        </button>

                        <button
                          className="cancel-renewal-button"
                          onClick={() =>
                            handleCancelRenewal(
                              subscription
                            )
                          }
                        >
                          Didn't Renew
                        </button>
                      </div>
                    </div>
                  )}

                  <div className="subscription-actions">
                    <button
                      onClick={() =>
                        handleSubscriptionClick(
                          subscription
                        )
                      }
                    >
                      {isSelected
                        ? 'Hide History'
                        : 'View History'}
                    </button>

                    <button
                      onClick={() =>
                        setEditingSubscription(
                          subscription
                        )
                      }
                    >
                      Edit
                    </button>

                    <button
                      onClick={() =>
                        handleDelete(subscription.id)
                      }
                    >
                      Delete
                    </button>
                  </div>

                  {isSelected && (
                    <div className="subscription-history">
                      <h4>Spending History</h4>

                      {historyLoading ? (
                        <p>Loading history...</p>
                      ) : subscriptionHistory.length === 0 ? (
                        <p>No spending history yet.</p>
                      ) : (
                        <div className="history-list">
                          {subscriptionHistory.map(
                            (record) => (
                              <div
                                className="history-item"
                                key={record.id}
                              >
                                <span>
                                  {record.spentDate}
                                </span>

                                <strong>
                                  ₹{record.amount}
                                </strong>
                              </div>
                            )
                          )}
                        </div>
                      )}
                    </div>
                  )}
                </article>
              )
            })}
          </div>
        )}
      </section>
    </main>
  )
}

export default Dashboard