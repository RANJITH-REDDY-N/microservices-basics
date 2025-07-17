import React, { useEffect, useState, useContext } from 'react';
import { Card, Button, Row, Col, Form, Alert, Modal } from 'react-bootstrap';
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

export default function Products() {
  const { auth } = useContext(AuthContext);
  const [products, setProducts] = useState([]);
  const [error, setError] = useState(null);
  const [showAdd, setShowAdd] = useState(false);
  const [addForm, setAddForm] = useState({ name: '', description: '', price: '', category: '', stockQuantity: '' });
  const [addError, setAddError] = useState(null);
  const [addSuccess, setAddSuccess] = useState(false);

  const role = decodeJwt(auth?.token)?.role;

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const res = await axios.get(`${API_BASE}/api/products`, {
          headers: auth ? { Authorization: `Bearer ${auth.token}` } : {},
        });
        setProducts(res.data);
      } catch (err) {
        setError('Failed to load products');
      }
    };
    fetchProducts();
  }, [auth]);

  const handleAddChange = e => {
    setAddForm({ ...addForm, [e.target.name]: e.target.value });
  };

  const handleAddProduct = async e => {
    e.preventDefault();
    setAddError(null);
    setAddSuccess(false);
    try {
      await axios.post(`${API_BASE}/api/products`, addForm, {
        headers: { Authorization: `Bearer ${auth.token}` },
      });
      setAddSuccess(true);
      setAddForm({ name: '', description: '', price: '', category: '', stockQuantity: '' });
      setShowAdd(false);
      // Refresh product list
      const res = await axios.get(`${API_BASE}/api/products`, {
        headers: { Authorization: `Bearer ${auth.token}` },
      });
      setProducts(res.data);
    } catch (err) {
      setAddError(err.response?.data?.message || 'Failed to add product');
    }
  };

  return (
    <div>
      <h2>Products</h2>
      {error && <Alert variant="danger">{error}</Alert>}
      {(role === 'ADMIN' || role === 'MANAGER') && (
        <Button className="mb-3" onClick={() => setShowAdd(true)}>Add Product</Button>
      )}
      <Row>
        {products.map(product => (
          <Col md={4} key={product.id} className="mb-4">
            <Card>
              <Card.Body>
                <Card.Title>{product.name}</Card.Title>
                <Card.Text>{product.description}</Card.Text>
                <Card.Text>Price: ${product.price}</Card.Text>
                <Card.Text>Category: {product.category}</Card.Text>
                <Card.Text>Stock: {product.stockQuantity}</Card.Text>
                {role === 'USER' && <Button variant="primary">Order</Button>}
              </Card.Body>
            </Card>
          </Col>
        ))}
      </Row>
      <Modal show={showAdd} onHide={() => setShowAdd(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Add Product</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {addError && <Alert variant="danger">{addError}</Alert>}
          {addSuccess && <Alert variant="success">Product added!</Alert>}
          <Form onSubmit={handleAddProduct}>
            <Form.Group className="mb-2">
              <Form.Label>Name</Form.Label>
              <Form.Control name="name" value={addForm.name} onChange={handleAddChange} required />
            </Form.Group>
            <Form.Group className="mb-2">
              <Form.Label>Description</Form.Label>
              <Form.Control name="description" value={addForm.description} onChange={handleAddChange} required />
            </Form.Group>
            <Form.Group className="mb-2">
              <Form.Label>Price</Form.Label>
              <Form.Control name="price" type="number" value={addForm.price} onChange={handleAddChange} required />
            </Form.Group>
            <Form.Group className="mb-2">
              <Form.Label>Category</Form.Label>
              <Form.Control name="category" value={addForm.category} onChange={handleAddChange} required />
            </Form.Group>
            <Form.Group className="mb-2">
              <Form.Label>Stock Quantity</Form.Label>
              <Form.Control name="stockQuantity" type="number" value={addForm.stockQuantity} onChange={handleAddChange} required />
            </Form.Group>
            <Button type="submit" variant="success" className="w-100">Add</Button>
          </Form>
        </Modal.Body>
      </Modal>
    </div>
  );
} 