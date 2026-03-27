import React, { useEffect, useState, useCallback } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

interface BankAccount {
  id: number;
  name: string;
  accountNumber: string;
  bankName: string;
  accountType: string;
  branch: string;
  cifnumber: string;
  ifsccode: string;
  emailBank: string;
  verified: boolean;
  updated: boolean;
  userUpdateRequest: boolean;
}

const BANK_OPTIONS = ['SBI','UNION_BANK','CENTRAL_BANK','BANK_OF_BARODA','FEDERAL_BANK','HDFC'];
const BANK_LABELS: Record<string, string> = {
  SBI: 'SBI', UNION_BANK: 'Union Bank', CENTRAL_BANK: 'Central Bank',
  BANK_OF_BARODA: 'Bank of Baroda', FEDERAL_BANK: 'Federal Bank', HDFC: 'HDFC'
};
const BANK_EMOJI: Record<string, string> = {
  SBI: '🏦', UNION_BANK: '🏛️', CENTRAL_BANK: '🏢', BANK_OF_BARODA: '🏪', FEDERAL_BANK: '🏗️', HDFC: '💳'
};

export default function Dashboard() {
  const [accounts, setAccounts]     = useState<BankAccount[]>([]);
  const [loading, setLoading]       = useState(false);
  const [error, setError]           = useState('');
  const [message, setMessage]       = useState('');
  const [activeView, setActiveView] = useState<'accounts'|'add'>('accounts');
  const [newAccount, setNewAccount] = useState({
    name: '', accountNumber: '', cifnumber: '', branch: '',
    ifsccode: '', bankName: 'SBI', accountType: 'SAVINGS', emailBank: ''
  });
  const navigate = useNavigate();

  const getConfig = () => ({
    headers: { Authorization: `Bearer ${localStorage.getItem('jwt_token')}` }
  });

  const dismissMsg = () => { setMessage(''); setError(''); };

  const fetchAccounts = useCallback(async (silent = false) => {
    if (!silent) setLoading(true);
    try {
      const res = await axios.get('http://localhost:8085/WALLETSERVICE/get/bank/account', getConfig());
      setAccounts(Array.isArray(res.data) ? res.data : []);
    } catch (err: any) {
      if (err.response?.status === 401) navigate('/login');
      else if (!silent) {
        const msg = typeof err.response?.data === 'string'
          ? err.response.data : err.response?.data?.message || 'Failed to fetch accounts.';
        setError(msg);
      }
    } finally {
      if (!silent) setLoading(false);
    }
  }, [navigate]);

  // Auto-poll every 12s to refresh verification status
  useEffect(() => {
    fetchAccounts();
    const interval = setInterval(() => fetchAccounts(true), 12000);
    return () => clearInterval(interval);
  }, [fetchAccounts]);

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    dismissMsg();
    setLoading(true);
    try {
      const res = await axios.post('http://localhost:8085/WALLETSERVICE/bank/account/register', newAccount, getConfig());
      setMessage(typeof res.data === 'string' ? res.data : 'Account registered successfully!');
      setNewAccount({ name:'', accountNumber:'', cifnumber:'', branch:'', ifsccode:'', bankName:'SBI', accountType:'SAVINGS', emailBank:'' });
      setActiveView('accounts');
      fetchAccounts();
    } catch (err: any) {
      const msg = typeof err.response?.data === 'string'
        ? err.response.data : err.response?.data?.message || err.response?.data?.error || 'Failed to register.';
      setError(msg);
    } finally { setLoading(false); }
  };

  const handleSetDefault = async (id: number) => {
    dismissMsg();
    try {
      const res = await axios.post(`http://localhost:8085/WALLETSERVICE/register/default/account?id=${id}`, {}, getConfig());
      setMessage(typeof res.data === 'string' ? res.data : 'Default account updated!');
      fetchAccounts();
    } catch (err: any) {
      const msg = typeof err.response?.data === 'string'
        ? err.response.data : err.response?.data?.message || err.response?.data?.error || 'Failed to set default.';
      setError(msg);
    }
  };

  const handleVerify = async (id: number) => {
    dismissMsg();
    try {
      const res = await axios.post(`http://localhost:8085/WALLETSERVICE/bank/account/verify?id=${id}`, {}, getConfig());
      setMessage(typeof res.data === 'string' ? res.data : 'Verification initiated! Status will update automatically.');
      fetchAccounts();
    } catch (err: any) {
      const msg = typeof err.response?.data === 'string'
        ? err.response.data : err.response?.data?.message || err.response?.data?.error || 'Verification failed.';
      setError(msg);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('jwt_token');
    navigate('/login');
  };

  const nav = [
    { id: 'accounts', icon: '💳', label: 'Accounts' },
    { id: 'add',      icon: '➕', label: 'Add Account' },
  ];

  return (
    <div className="dash-shell">
      {/* ── SIDEBAR ── */}
      <aside className="sidebar">
        <div className="sidebar-brand">
          <div className="sidebar-logo">🏦</div>
          <div>
            <div className="sidebar-name">SecureBank</div>
            <div className="sidebar-tagline">Digital Wallet</div>
          </div>
        </div>

        <nav className="sidebar-nav">
          {nav.map(item => (
            <button
              key={item.id}
              className={`sidebar-item ${activeView === item.id ? 'active' : ''}`}
              onClick={() => { setActiveView(item.id as any); dismissMsg(); }}
            >
              <span className="sidebar-item-icon">{item.icon}</span>
              <span>{item.label}</span>
            </button>
          ))}
        </nav>

        <div className="sidebar-footer">
          <button className="sidebar-logout" onClick={handleLogout}>
            <span>↩</span> Sign Out
          </button>
        </div>
      </aside>

      {/* ── MAIN CONTENT ── */}
      <main className="dash-main">

        {/* Toasts */}
        {error   && (
          <div className="toast toast-error">
            ⚠️ {error}
            <button className="toast-close" onClick={dismissMsg}>✕</button>
          </div>
        )}
        {message && (
          <div className="toast toast-success">
            ✓ {message}
            <button className="toast-close" onClick={dismissMsg}>✕</button>
          </div>
        )}

        {/* ── ACCOUNTS VIEW ── */}
        {activeView === 'accounts' && (
          <div className="view-container">
            <div className="view-header">
              <div>
                <h2 className="view-title">Your Accounts</h2>
                <p className="view-desc">Status refreshes automatically every 12 seconds</p>
              </div>
              <button className="btn-ghost" onClick={() => fetchAccounts()} disabled={loading}>
                {loading ? '…' : '↻ Refresh'}
              </button>
            </div>

            {accounts.length === 0 && !loading ? (
              <div className="empty-state">
                <div className="empty-icon">🏦</div>
                <p className="empty-text">No bank accounts linked yet</p>
                <p className="empty-sub">Click "Add Account" in the sidebar to get started</p>
              </div>
            ) : (
              <div className="accounts-grid">
                {accounts.map(acc => (
                  <div key={acc.id} className="account-card">
                    <div className="ac-top">
                      <div className="ac-bank-icon">
                        {BANK_EMOJI[acc.bankName] || '🏦'}
                      </div>
                      <div className={`ac-status ${acc.verified ? 'status-verified' : acc.userUpdateRequest ? 'status-pending' : 'status-unverified'}`}>
                        <span className="status-dot" />
                        {acc.verified ? 'Verified' : acc.userUpdateRequest ? 'In Progress' : 'Unverified'}
                      </div>
                    </div>

                    <div className="ac-body">
                      <div className="ac-bankname">{BANK_LABELS[acc.bankName] || acc.bankName}</div>
                      <div className="ac-holder">{acc.name}</div>
                      <div className="ac-number">•••• {acc.accountNumber.slice(-4)}</div>
                      <div className="ac-type">{acc.accountType} · {acc.branch}</div>
                    </div>

                    <div className="ac-actions">
                      <button className="ac-btn ac-btn-default" onClick={() => handleSetDefault(acc.id)}>
                        Set Default
                      </button>
                      {!acc.verified && !acc.userUpdateRequest && (
                        <button className="ac-btn ac-btn-verify" onClick={() => handleVerify(acc.id)}>
                          Verify
                        </button>
                      )}
                      {acc.userUpdateRequest && !acc.verified && (
                        <span className="ac-processing">⏳ Processing…</span>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* ── ADD ACCOUNT VIEW ── */}
        {activeView === 'add' && (
          <div className="view-container">
            <div className="view-header">
              <div>
                <h2 className="view-title">Link Bank Account</h2>
                <p className="view-desc">Enter your bank details below</p>
              </div>
            </div>

            <div className="form-panel">
              <form onSubmit={handleRegister} className="register-form">
                <div className="form-row">
                  <div className="field">
                    <label className="field-label">Full Name</label>
                    <input className="field-input" type="text" placeholder="As on bank records"
                      value={newAccount.name} onChange={e => setNewAccount({...newAccount, name: e.target.value})} required />
                  </div>
                  <div className="field">
                    <label className="field-label">Account Number</label>
                    <input className="field-input" type="text" placeholder="Bank account number"
                      value={newAccount.accountNumber} onChange={e => setNewAccount({...newAccount, accountNumber: e.target.value})} required />
                  </div>
                </div>

                <div className="form-row">
                  <div className="field">
                    <label className="field-label">CIF Number</label>
                    <input className="field-input" type="text" placeholder="Customer ID / CIF"
                      value={newAccount.cifnumber} onChange={e => setNewAccount({...newAccount, cifnumber: e.target.value})} required />
                  </div>
                  <div className="field">
                    <label className="field-label">IFSC Code</label>
                    <input className="field-input" type="text" placeholder="e.g. SBIN0001234"
                      value={newAccount.ifsccode} onChange={e => setNewAccount({...newAccount, ifsccode: e.target.value})} required />
                  </div>
                </div>

                <div className="form-row">
                  <div className="field">
                    <label className="field-label">Branch</label>
                    <input className="field-input" type="text" placeholder="Branch name"
                      value={newAccount.branch} onChange={e => setNewAccount({...newAccount, branch: e.target.value})} required />
                  </div>
                  <div className="field">
                    <label className="field-label">Bank Email</label>
                    <input className="field-input" type="email" placeholder="Email linked to bank"
                      value={newAccount.emailBank} onChange={e => setNewAccount({...newAccount, emailBank: e.target.value})} required />
                  </div>
                </div>

                <div className="form-row">
                  <div className="field">
                    <label className="field-label">Bank</label>
                    <select className="field-select" value={newAccount.bankName}
                      onChange={e => setNewAccount({...newAccount, bankName: e.target.value})}>
                      {BANK_OPTIONS.map(b => <option key={b} value={b}>{BANK_LABELS[b]}</option>)}
                    </select>
                  </div>
                  <div className="field">
                    <label className="field-label">Account Type</label>
                    <select className="field-select" value={newAccount.accountType}
                      onChange={e => setNewAccount({...newAccount, accountType: e.target.value})}>
                      <option value="SAVINGS">Savings</option>
                      <option value="CURRENT">Current</option>
                    </select>
                  </div>
                </div>

                <div className="form-actions">
                  <button type="button" className="btn-ghost" onClick={() => setActiveView('accounts')}>Cancel</button>
                  <button type="submit" className="btn-submit" disabled={loading}>
                    {loading ? 'Registering…' : 'Link Account'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}
