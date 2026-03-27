import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';

export default function CreateAccount() {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('USER');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleCreateAccount = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);

    try {
      await axios.post('http://localhost:8085/AUTHENTICATIONSERVICE/api/user/create', {
        username,
        email,
        password,
        role
      });
      setSuccess('Account created! Redirecting to login…');
      setTimeout(() => navigate('/login'), 2000);
    } catch (err: any) {
      const errMsg = typeof err.response?.data === 'string'
        ? err.response.data
        : err.response?.data?.message || err.response?.data?.error || 'Failed to create account. Please try again.';
      setError(errMsg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-wrap">
      <div className="auth-card">
        <div className="auth-icon">✨</div>
        <h1 className="auth-title">Create account</h1>
        <p className="auth-subtitle">Join SecureBank — your digital wallet</p>

        {error   && <div className="alert alert-error">⚠️ {error}</div>}
        {success && <div className="alert alert-success">✓ {success}</div>}

        <form onSubmit={handleCreateAccount}>
          <div className="field">
            <label className="field-label" htmlFor="username">Username</label>
            <input id="username" type="text" className="field-input"
              placeholder="Choose a username"
              value={username} onChange={e => setUsername(e.target.value)} required />
          </div>

          <div className="field">
            <label className="field-label" htmlFor="email">Email</label>
            <input id="email" type="email" className="field-input"
              placeholder="Enter your email"
              value={email} onChange={e => setEmail(e.target.value)} required />
          </div>

          <div className="field">
            <label className="field-label" htmlFor="password">Password</label>
            <input id="password" type="password" className="field-input"
              placeholder="Create a strong password"
              value={password} onChange={e => setPassword(e.target.value)} required />
          </div>

          <div className="field">
            <label className="field-label" htmlFor="role">Role</label>
            <select id="role" className="field-select"
              value={role} onChange={e => setRole(e.target.value)}>
              <option value="USER">User</option>
              <option value="MANAGER">Manager</option>
              <option value="ADMIN">Admin</option>
            </select>
          </div>

          <button type="submit" className="btn-primary" disabled={loading}>
            {loading ? 'Creating account…' : 'Create Account'}
          </button>
        </form>

        <div className="auth-footer">
          Already have an account? <Link to="/login">Sign in</Link>
        </div>
      </div>
    </div>
  );
}
