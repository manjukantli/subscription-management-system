const API_BASE_URL = 'http://localhost:8080/api'

export async function login(email, password) {
  const response = await fetch(`${API_BASE_URL}/auth/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      email,
      password,
    }),
  })

  if (!response.ok) {
    throw new Error('Invalid email or password')
  }

  return response.text()
}

export async function register(name, email, password) {
  const response = await fetch(`${API_BASE_URL}/auth/register`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      name,
      email,
      password,
    }),
  })

  if (!response.ok) {
    throw new Error('Registration failed')
  }

  return response.json()
}

export async function getSubscriptions() {
  const token = localStorage.getItem('token')

  const response = await fetch(`${API_BASE_URL}/subscriptions`, {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${token}`,
    },
  })

  if (!response.ok) {
    throw new Error('Failed to fetch subscriptions')
  }

  return response.json()
}

export async function addSubscription(subscription) {
  const token = localStorage.getItem('token')

  const response = await fetch(`${API_BASE_URL}/subscriptions`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(subscription),
  })

  if (!response.ok) {
    throw new Error('Failed to add subscription')
  }

  return response.json()
}

export async function deleteSubscription(id) {
  const token = localStorage.getItem('token')

  const response = await fetch(`${API_BASE_URL}/subscriptions/${id}`, {
    method: 'DELETE',
    headers: {
      Authorization: `Bearer ${token}`,
    },
  })

  if (!response.ok) {
    throw new Error('Failed to delete subscription')
  }

  return response.text()
}

export async function updateSubscription(id, subscription) {
  const token = localStorage.getItem('token')

  const response = await fetch(`${API_BASE_URL}/subscriptions/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(subscription),
  })

  if (!response.ok) {
    throw new Error('Failed to update subscription')
  }

  return response.json()
}

export async function getDashboard() {
  const token = localStorage.getItem('token')

  const response = await fetch(`${API_BASE_URL}/dashboard`, {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${token}`,
    },
  })

  if (!response.ok) {
    throw new Error('Failed to fetch dashboard')
  }

  return response.json()
}

export async function getSpendingHistory() {
  const token = localStorage.getItem('token')

  const response = await fetch(`${API_BASE_URL}/spending/history`, {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${token}`,
    },
  })

  if (!response.ok) {
    throw new Error('Failed to fetch spending history')
  }

  return response.json()
}

export async function addSpending(spending) {
  const token = localStorage.getItem('token')

  const response = await fetch(`${API_BASE_URL}/spending`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(spending),
  })

  if (!response.ok) {
    throw new Error('Failed to record spending')
  }

  return response.json()
}

export async function getSubscriptionSpending(subscriptionId) {
  const token = localStorage.getItem('token')

  const response = await fetch(
    `${API_BASE_URL}/spending/subscription/${subscriptionId}`,
    {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  )

  if (!response.ok) {
    throw new Error('Failed to fetch subscription spending')
  }

  return response.json()
}

export async function confirmRenewal(id) {
  const token = localStorage.getItem('token')

  const response = await fetch(
    `${API_BASE_URL}/subscriptions/${id}/renew`,
    {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  )

  if (!response.ok) {
    const data = await response.json().catch(() => null)
    throw new Error(
      data?.message || 'Failed to confirm renewal'
    )
  }

  return response.json()
}

export async function cancelRenewal(id) {
  const token = localStorage.getItem('token')

  const response = await fetch(
    `${API_BASE_URL}/subscriptions/${id}/cancel-renewal`,
    {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  )

  if (!response.ok) {
    const data = await response.json().catch(() => null)
    throw new Error(
      data?.message || 'Failed to cancel renewal'
    )
  }

  return response.json()
}