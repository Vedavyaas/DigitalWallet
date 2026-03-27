import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';

export default function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await axios.post('http://localhost:8085/AUTHENTICATIONSERVICE/api/user/login', {
        username,
        password,
      });
      const token = response.data ? (response.data.token || response.data) : null;
      if (token && typeof token === 'string') {
        localStorage.setItem('jwt_token', token);
        navigate('/dashboard');
      } else {
        setError('Login failed. Invalid token received.');
      }
    } catch (err: any) {
      const errMsg = typeof err.response?.data === 'string'
        ? err.response.data
        : err.response?.data?.message || err.response?.data?.error || 'Invalid credentials. Please try again.';
      setError(errMsg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-wrap">
      <div className="auth-card">
        <div className="auth-icon">🏦</div>
        <h1 className="auth-title">Welcome back</h1>
        <p className="auth-subtitle">Sign in to your SecureBank account</p>

        {error && <div className="alert alert-error">⚠️ {error}</div>}

        <form onSubmit={handleLogin}>
          <div className="field">
            <label className="field-label" htmlFor="username">Username</label>
            <input
              id="username"
              type="text"
              className="field-input"
              placeholder="Enter your username"
              value={username}
              onChange={e => setUsername(e.target.value)}
              required
              autoComplete="username"
            />
          </div>

          <div className="field">
            <label className="field-label" htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              className="field-input"
              placeholder="Enter your password"
              value={password}
              onChange={e => setPassword(e.target.value)}
              required
              autoComplete="current-password"
            />
          </div>

          <button type="submit" className="btn-primary" disabled={loading}>
            {loading ? 'Signing in…' : 'Sign In'}
          </button>
        </form>

        <div className="auth-footer">
          Don't have an account?{' '}
          <Link to="/create-account">Create one</Link>
        </div>
      </div>
    </div>
  );
}
