import React, { useState, useEffect, createContext } from 'react';
import { BrowserRouter as Router, Routes, Route, Link, useNavigate } from 'react-router-dom';
import { Container, Navbar, Nav, Button } from 'react-bootstrap';
import Register from './components/Register';
import Login from './components/Login';
import Products from './components/Products';
import Orders from './components/Orders';
import Profile from './components/Profile';

export const AuthContext = createContext();

function Home() {
  return <h2>Welcome to Microservices Shop</h2>;
}

function App() {
  const [auth, setAuth] = useState(() => {
    const token = localStorage.getItem('token');
    return token ? { token } : null;
  });

  const logout = () => {
    localStorage.removeItem('token');
    setAuth(null);
  };

  return (
    <AuthContext.Provider value={{ auth, setAuth }}>
      <Router>
        <Navbar bg="dark" variant="dark" expand="lg">
          <Container>
            <Navbar.Brand as={Link} to="/">Microservices Shop</Navbar.Brand>
            <Navbar.Toggle aria-controls="basic-navbar-nav" />
            <Navbar.Collapse id="basic-navbar-nav">
              <Nav className="me-auto">
                <Nav.Link as={Link} to="/">Home</Nav.Link>
                {!auth && <Nav.Link as={Link} to="/register">Register</Nav.Link>}
                {!auth && <Nav.Link as={Link} to="/login">Login</Nav.Link>}
                <Nav.Link as={Link} to="/products">Products</Nav.Link>
                {auth && <Nav.Link as={Link} to="/orders">Orders</Nav.Link>}
                {auth && <Nav.Link as={Link} to="/profile">Profile</Nav.Link>}
              </Nav>
              {auth && <Button variant="outline-light" onClick={logout}>Logout</Button>}
            </Navbar.Collapse>
          </Container>
        </Navbar>
        <Container className="mt-4">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/register" element={<Register />} />
            <Route path="/login" element={<Login />} />
            <Route path="/products" element={<Products />} />
            <Route path="/orders" element={<Orders />} />
            <Route path="/profile" element={<Profile />} />
          </Routes>
        </Container>
      </Router>
    </AuthContext.Provider>
  );
}

export default App;
