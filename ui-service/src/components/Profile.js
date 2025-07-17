import React, { useContext, useEffect, useState } from 'react';
import { Card, Alert } from 'react-bootstrap';
import axios from 'axios';
import { AuthContext } from '../App';

const API_BASE = process.env.REACT_APP_API_BASE || 'http://localhost:8080';

function decodeJwt(token) {
  if (!token) return {};
  try {
    const payload = token.split('.')[1];
    return JSON.parse(atob(payload));
  } catch {
    return {};
  }
}

export default function Profile() {
  const { auth } = useContext(AuthContext);
  const [user, setUser] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    // Try to fetch user info from backend if endpoint exists, else use JWT
    const fetchUser = async () => {
      try {
        const res = await axios.get(`${API_BASE}/api/users/me`, {
          headers: { Authorization: `Bearer ${auth.token}` },
        });
        setUser(res.data);
      } catch {
        // fallback to JWT
        setUser(decodeJwt(auth?.token));
      }
    };
    fetchUser();
  }, [auth]);

  if (!auth) return <Alert variant="warning">Not logged in.</Alert>;
  if (!user) return <div>Loading...</div>;

  return (
    <Card style={{ maxWidth: 400, margin: '0 auto' }}>
      <Card.Body>
        <h3 className="mb-3">Profile</h3>
        <p><strong>Username:</strong> {user.username || user.sub}</p>
        <p><strong>Email:</strong> {user.email || '-'}</p>
        <p><strong>Role:</strong> {user.role}</p>
        {user.userId && <p><strong>User ID:</strong> {user.userId}</p>}
      </Card.Body>
    </Card>
  );
} 