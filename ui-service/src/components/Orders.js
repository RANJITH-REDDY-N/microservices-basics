import React, { useEffect, useState, useContext } from 'react';
import { Card, Button, Row, Col, Alert, Modal, Form } from 'react-bootstrap';
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

export default function Orders() {
  const { auth } = useContext(AuthContext);
  const [orders, setOrders] = useState([]);
  const [products, setProducts] = useState([]);
  const [error, setError] = useState(null);
  const [showOrder, setShowOrder] = useState(false);
  const [orderForm, setOrderForm] = useState({ items: [] });
  const [orderError, setOrderError] = useState(null);
  const [orderSuccess, setOrderSuccess] = useState(false);
  const [loading, setLoading] = useState(false);

  const role = decodeJwt(auth?.token)?.role;
  const username = decodeJwt(auth?.token)?.sub;

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        const res = await axios.get(`${API_BASE}/api/orders`, {
          headers: { Authorization: `Bearer ${auth.token}` },
        });
        setOrders(res.data);
      } catch (err) {
        setError('Failed to load orders');
      }
    };
    const fetchProducts = async () => {
      try {
        const res = await axios.get(`${API_BASE}/api/products`, {
          headers: { Authorization: `Bearer ${auth.token}` },
        });
        setProducts(res.data);
      } catch {}
    };
    fetchOrders();
    fetchProducts();
  }, [auth]);

  const handleOrderChange = (idx, field, value) => {
    const items = [...orderForm.items];
    items[idx][field] = value;
    setOrderForm({ ...orderForm, items });
  };

  const handleAddItem = () => {
    setOrderForm({ ...orderForm, items: [...(orderForm.items || []), { productId: '', quantity: 1 }] });
  };

  const handleRemoveItem = idx => {
    const items = [...orderForm.items];
    items.splice(idx, 1);
    setOrderForm({ ...orderForm, items });
  };

  const handlePlaceOrder = async e => {
    e.preventDefault();
    setOrderError(null);
    setOrderSuccess(false);
    setLoading(true);
    try {
      await axios.post(`${API_BASE}/api/orders`, orderForm, {
        headers: { Authorization: `Bearer ${auth.token}` },
      });
      setOrderSuccess(true);
      setOrderForm({ items: [] });
      setShowOrder(false);
      // Refresh orders
      const res = await axios.get(`${API_BASE}/api/orders`, {
        headers: { Authorization: `Bearer ${auth.token}` },
      });
      setOrders(res.data);
    } catch (err) {
      setOrderError(err.response?.data?.message || 'Failed to place order');
    } finally {
      setLoading(false);
    }
  };

  const handleOrderAction = async (orderId, action) => {
    try {
      if (action === 'cancel') {
        await axios.put(`${API_BASE}/api/orders/${orderId}/status`, { status: 'CANCELLED' }, {
          headers: { Authorization: `Bearer ${auth.token}` },
        });
      } else if (action === 'approve') {
        await axios.put(`${API_BASE}/api/orders/${orderId}/status`, { status: 'DELIVERED' }, {
          headers: { Authorization: `Bearer ${auth.token}` },
        });
      } else if (action === 'reject') {
        await axios.put(`${API_BASE}/api/orders/${orderId}/status`, { status: 'CANCELLED' }, {
          headers: { Authorization: `Bearer ${auth.token}` },
        });
      }
      // Refresh orders
      const res = await axios.get(`${API_BASE}/api/orders`, {
        headers: { Authorization: `Bearer ${auth.token}` },
      });
      setOrders(res.data);
    } catch (err) {
      setError('Failed to update order');
    }
  };

  return (
    <div>
      <h2>Orders</h2>
      {error && <Alert variant="danger">{error}</Alert>}
      {role === 'USER' && (
        <Button className="mb-3" onClick={() => setShowOrder(true)}>Place New Order</Button>
      )}
      <Row>
        {orders.map(order => (
          <Col md={6} key={order.id} className="mb-4">
            <Card>
              <Card.Body>
                <Card.Title>Order #{order.id}</Card.Title>
                <Card.Text>Status: {order.status}</Card.Text>
                <Card.Text>Total: ${order.totalAmount}</Card.Text>
                <Card.Text>Items:
                  <ul>
                    {order.orderItems?.map(item => (
                      <li key={item.id}>{item.productName} x {item.quantity}</li>
                    ))}
                  </ul>
                </Card.Text>
                {role === 'USER' && order.status === 'PENDING' && (
                  <Button variant="danger" onClick={() => handleOrderAction(order.id, 'cancel')}>Cancel</Button>
                )}
                {role === 'ADMIN' && order.status === 'PENDING' && (
                  <>
                    <Button variant="success" className="me-2" onClick={() => handleOrderAction(order.id, 'approve')}>Approve</Button>
                    <Button variant="danger" onClick={() => handleOrderAction(order.id, 'reject')}>Reject</Button>
                  </>
                )}
              </Card.Body>
            </Card>
          </Col>
        ))}
      </Row>
      <Modal show={showOrder} onHide={() => setShowOrder(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Place New Order</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {orderError && <Alert variant="danger">{orderError}</Alert>}
          {orderSuccess && <Alert variant="success">Order placed!</Alert>}
          <Form onSubmit={handlePlaceOrder}>
            {(orderForm.items || []).map((item, idx) => (
              <Row key={idx} className="mb-2">
                <Col>
                  <Form.Select name="productId" value={item.productId} onChange={e => handleOrderChange(idx, 'productId', e.target.value)} required>
                    <option value="">Select Product</option>
                    {products.map(p => (
                      <option key={p.id} value={p.id}>{p.name}</option>
                    ))}
                  </Form.Select>
                </Col>
                <Col>
                  <Form.Control name="quantity" type="number" min={1} value={item.quantity} onChange={e => handleOrderChange(idx, 'quantity', e.target.value)} required />
                </Col>
                <Col xs="auto">
                  <Button variant="outline-danger" onClick={() => handleRemoveItem(idx)}>-</Button>
                </Col>
              </Row>
            ))}
            <Button variant="secondary" className="mb-2" onClick={handleAddItem}>Add Item</Button>
            <Button type="submit" variant="success" className="w-100" disabled={loading}>{loading ? 'Placing...' : 'Place Order'}</Button>
          </Form>
        </Modal.Body>
      </Modal>
    </div>
  );
} 