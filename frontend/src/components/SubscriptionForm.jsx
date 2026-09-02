import { useEffect, useState } from 'react'
import { addSubscription, updateSubscription } from '../services/api'

function SubscriptionForm({
  editingSubscription,
  onSubscriptionAdded,
  onSubscriptionUpdated,
  onCancelEdit,
}) {
  const [serviceName, setServiceName] = useState('')
  const [cost, setCost] = useState('')
  const [billingCycle, setBillingCycle] = useState('MONTHLY')
  const [category, setCategory] = useState('ENTERTAINMENT')
  const [renewalDate, setRenewalDate] = useState('')
  const [notificationDaysBefore, setNotificationDaysBefore] = useState(3)

  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const isEditing = editingSubscription !== null

  useEffect(() => {
    if (editingSubscription) {
      setServiceName(editingSubscription.serviceName)
      setCost(editingSubscription.cost)
      setBillingCycle(editingSubscription.billingCycle)
      setCategory(editingSubscription.category)
      setRenewalDate(editingSubscription.renewalDate)
      setNotificationDaysBefore(
        editingSubscription.notificationDaysBefore ?? 3
      )
    } else {
      setServiceName('')
      setCost('')
      setBillingCycle('MONTHLY')
      setCategory('ENTERTAINMENT')
      setRenewalDate('')
      setNotificationDaysBefore(3)
    }

    setError('')
  }, [editingSubscription])

  const handleSubmit = async (event) => {
    event.preventDefault()

    setError('')
    setLoading(true)

    try {
      const subscription = {
        serviceName,
        cost: Number(cost),
        billingCycle,
        category,
        renewalDate,
        notificationDaysBefore: Number(
          notificationDaysBefore
        ),
      }

      if (isEditing) {
        const updatedSubscription =
          await updateSubscription(
            editingSubscription.id,
            subscription
          )

        onSubscriptionUpdated(updatedSubscription)
      } else {
        const newSubscription =
          await addSubscription(subscription)

        onSubscriptionAdded(newSubscription)
      }

      setServiceName('')
      setCost('')
      setBillingCycle('MONTHLY')
      setCategory('ENTERTAINMENT')
      setRenewalDate('')
      setNotificationDaysBefore(3)
    } catch (error) {
      setError(error.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="subscription-form">
      <div className="form-header">
        <h2>
          {isEditing
            ? 'Edit Subscription'
            : 'Add Subscription'}
        </h2>

        <p>
          {isEditing
            ? 'Update your subscription details.'
            : 'Add a subscription to start tracking it.'}
        </p>
      </div>

      <form onSubmit={handleSubmit}>
        <div className="form-grid">
          <div>
            <label>Service Name</label>

            <input
              type="text"
              value={serviceName}
              onChange={(event) =>
                setServiceName(event.target.value)
              }
              placeholder="Netflix"
              required
            />
          </div>

          <div>
            <label>Cost</label>

            <input
              type="number"
              step="0.01"
              min="0"
              value={cost}
              onChange={(event) =>
                setCost(event.target.value)
              }
              placeholder="499"
              required
            />
          </div>

          <div>
            <label>Billing Cycle</label>

            <select
              value={billingCycle}
              onChange={(event) =>
                setBillingCycle(event.target.value)
              }
            >
              <option value="MONTHLY">Monthly</option>
              <option value="YEARLY">Yearly</option>
            </select>
          </div>

          <div>
            <label>Category</label>

            <select
              value={category}
              onChange={(event) =>
                setCategory(event.target.value)
              }
            >
              <option value="ENTERTAINMENT">
                Entertainment
              </option>

              <option value="PRODUCTIVITY">
                Productivity
              </option>

              <option value="CLOUD_SERVICES">
                Cloud Services
              </option>

              <option value="OTHERS">
                Others
              </option>
            </select>
          </div>

          <div>
            <label>Renewal Date</label>

            <input
              type="date"
              value={renewalDate}
              onChange={(event) =>
                setRenewalDate(event.target.value)
              }
              required
            />
          </div>

          <div>
            <label>
              Notify me before renewal
            </label>

            <div className="notification-input">
              <input
                type="number"
                min="1"
                max="30"
                value={notificationDaysBefore}
                onChange={(event) =>
                  setNotificationDaysBefore(
                    event.target.value
                  )
                }
                required
              />

              <span>days before</span>
            </div>
          </div>
        </div>

        {error && (
          <div className="form-error">
            {error}
          </div>
        )}

        <div className="form-actions">
          <button
            type="submit"
            disabled={loading}
            className="primary-button"
          >
            {loading
              ? isEditing
                ? 'Updating...'
                : 'Adding...'
              : isEditing
                ? 'Update Subscription'
                : 'Add Subscription'}
          </button>

          {isEditing && (
            <button
              type="button"
              onClick={onCancelEdit}
              className="secondary-button"
            >
              Cancel
            </button>
          )}
        </div>
      </form>
    </div>
  )
}

export default SubscriptionForm