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

type PaymentAction = 'deposit' | 'withdraw' | 'send';
type ActiveView = 'accounts' | 'add' | 'payment';

const BANK_OPTIONS = ['SBI','UNION_BANK','CENTRAL_BANK','BANK_OF_BARODA','FEDERAL_BANK','HDFC'];
const BANK_LABELS: Record<string, string> = {
  SBI: 'SBI', UNION_BANK: 'Union Bank', CENTRAL_BANK: 'Central Bank',
  BANK_OF_BARODA: 'Bank of Baroda', FEDERAL_BANK: 'Federal Bank', HDFC: 'HDFC'
};
const BANK_EMOJI: Record<string, string> = {
  SBI: '🏦', UNION_BANK: '🏛️', CENTRAL_BANK: '🏢', BANK_OF_BARODA: '🏪', FEDERAL_BANK: '🏗️', HDFC: '💳'
};

const GW = 'http://localhost:8085';

export default function Dashboard() {
  const [accounts, setAccounts]       = useState<BankAccount[]>([]);
  const [loading, setLoading]         = useState(false);
  const [error, setError]             = useState('');
  const [message, setMessage]         = useState('');
  const [activeView, setActiveView]   = useState<ActiveView>('accounts');
  const [newAccount, setNewAccount]   = useState({
    name: '', accountNumber: '', cifnumber: '', branch: '',
    ifsccode: '', bankName: 'SBI', accountType: 'SAVINGS', emailBank: ''
  });

  // payment modal state
  const [selectedAcc, setSelectedAcc] = useState<BankAccount | null>(null);
  const [payAction, setPayAction]     = useState<PaymentAction>('deposit');
  const [payAmount, setPayAmount]     = useState('');
  const [toUser, setToUser]           = useState('');
  const [payLoading, setPayLoading]   = useState(false);
  const [balance, setBalance]         = useState<string | null>(null);

  const navigate = useNavigate();

  const getConfig = () => ({
    headers: { Authorization: `Bearer ${localStorage.getItem('jwt_token')}` }
  });

  const dismissMsg = () => { setMessage(''); setError(''); };

  const fetchAccounts = useCallback(async (silent = false) => {
    if (!silent) setLoading(true);
    try {
      const res = await axios.get(`${GW}/WALLETSERVICE/get/bank/account`, getConfig());
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

  const fetchBalance = useCallback(async () => {
    try {
      const res = await axios.get(`${GW}/payment/get/balance`, getConfig());
      setBalance(res.data !== undefined ? String(res.data) : null);
    } catch {
      setBalance(null);
    }
  }, []);

  useEffect(() => {
    fetchAccounts();
    const interval = setInterval(() => fetchAccounts(true), 12000);
    return () => clearInterval(interval);
  }, [fetchAccounts]);

  useEffect(() => {
    if (activeView === 'payment') fetchBalance();
  }, [activeView, fetchBalance]);

  /* ── account actions ── */
  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault(); dismissMsg(); setLoading(true);
    try {
      const res = await axios.post(`${GW}/WALLETSERVICE/bank/account/register`, newAccount, getConfig());
      setMessage(typeof res.data === 'string' ? res.data : 'Account registered successfully!');
      setNewAccount({ name:'', accountNumber:'', cifnumber:'', branch:'', ifsccode:'', bankName:'SBI', accountType:'SAVINGS', emailBank:'' });
      setActiveView('accounts'); fetchAccounts();
    } catch (err: any) {
      const msg = typeof err.response?.data === 'string'
        ? err.response.data : err.response?.data?.message || err.response?.data?.error || 'Failed to register.';
      setError(msg);
    } finally { setLoading(false); }
  };

  const handleSetDefault = async (id: number) => {
    dismissMsg();
    try {
      const res = await axios.post(`${GW}/WALLETSERVICE/register/default/account?id=${id}`, {}, getConfig());
      setMessage(typeof res.data === 'string' ? res.data : 'Default account updated!');
      fetchAccounts();
    } catch (err: any) {
      const msg = typeof err.response?.data === 'string'
        ? err.response.data : err.response?.data?.message || 'Failed to set default.';
      setError(msg);
    }
  };

  const handleVerify = async (id: number) => {
    dismissMsg();
    try {
      const res = await axios.post(`${GW}/WALLETSERVICE/bank/account/verify?id=${id}`, {}, getConfig());
      setMessage(typeof res.data === 'string' ? res.data : 'Verification initiated! Status will update automatically.');
      fetchAccounts();
    } catch (err: any) {
      const msg = typeof err.response?.data === 'string'
        ? err.response.data : err.response?.data?.message || 'Verification failed.';
      setError(msg);
    }
  };

  const handleLogout = () => { localStorage.removeItem('jwt_token'); navigate('/login'); };

  /* ── payment actions ── */
  const openPayModal = (acc: BankAccount, action: PaymentAction) => {
    setSelectedAcc(acc); setPayAction(action);
    setPayAmount(''); setToUser('');
    dismissMsg();
  };
  const closePayModal = () => { setSelectedAcc(null); };

  const handlePaySubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!payAmount || isNaN(Number(payAmount)) || Number(payAmount) <= 0) {
      setError('Enter a valid amount.'); return;
    }
    setPayLoading(true); dismissMsg();
    try {
      let res;
      if (payAction === 'deposit') {
        res = await axios.post(`${GW}/payment/account/deposit?amount=${payAmount}`, {}, getConfig());
      } else if (payAction === 'withdraw') {
        res = await axios.post(`${GW}/payment/account/withdraw?amount=${payAmount}`, {}, getConfig());
      } else {
        if (!toUser.trim()) { setError('Enter recipient username.'); setPayLoading(false); return; }
        res = await axios.post(`${GW}/payment/transfer/money?amount=${payAmount}&toUser=${toUser.trim()}`, {}, getConfig());
      }
      setMessage(typeof res.data === 'string' ? res.data : 'Transaction successful!');
      setSelectedAcc(null);
      fetchBalance();
    } catch (err: any) {
      const msg = typeof err.response?.data === 'string'
        ? err.response.data : err.response?.data?.message || 'Transaction failed.';
      setError(msg);
    } finally { setPayLoading(false); }
  };

  /* ── sidebar nav ── */
  const nav: { id: ActiveView; icon: string; label: string }[] = [
    { id: 'accounts', icon: '💳', label: 'Accounts' },
    { id: 'payment',  icon: '💸', label: 'Payments' },
    { id: 'add',      icon: '➕', label: 'Add Account' },
  ];

  const actionMeta: Record<PaymentAction, { color: string; icon: string; label: string; btnClass: string }> = {
    deposit:  { color: 'var(--green)', icon: '⬇️', label: 'Deposit',  btnClass: 'pay-btn-deposit'  },
    withdraw: { color: 'var(--amber)', icon: '⬆️', label: 'Withdraw', btnClass: 'pay-btn-withdraw' },
    send:     { color: 'var(--blue)',  icon: '➤',  label: 'Send',     btnClass: 'pay-btn-send'     },
  };

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
              onClick={() => { setActiveView(item.id); dismissMsg(); }}
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

      {/* ── MAIN ── */}
      <main className="dash-main">

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
                <p className="view-desc">Tap a card to deposit, withdraw or send money · Status refreshes every 12s</p>
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
                      <div className="ac-bank-icon">{BANK_EMOJI[acc.bankName] || '🏦'}</div>
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

                    {/* Quick payment actions — show only for verified accounts */}
                    {acc.verified && (
                      <div className="ac-pay-strip">
                        <button className="pay-strip-btn pay-strip-deposit" onClick={() => openPayModal(acc, 'deposit')}>
                          <span>⬇</span> Deposit
                        </button>
                        <button className="pay-strip-btn pay-strip-withdraw" onClick={() => openPayModal(acc, 'withdraw')}>
                          <span>⬆</span> Withdraw
                        </button>
                        <button className="pay-strip-btn pay-strip-send" onClick={() => openPayModal(acc, 'send')}>
                          <span>➤</span> Send
                        </button>
                      </div>
                    )}

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

        {/* ── PAYMENT HUB VIEW ── */}
        {activeView === 'payment' && (
          <div className="view-container">
            <div className="view-header">
              <div>
                <h2 className="view-title">Payments</h2>
                <p className="view-desc">Deposit, withdraw, or send money — choose an account below</p>
              </div>
              <button className="btn-ghost" onClick={fetchBalance}>↻ Refresh Balance</button>
            </div>

            {/* Balance pill */}
            <div className="balance-card">
              <div className="balance-label">Wallet Balance</div>
              <div className="balance-amount">
                {balance !== null ? `₹ ${parseFloat(balance).toLocaleString('en-IN', { minimumFractionDigits: 2 })}` : '—'}
              </div>
            </div>

            {accounts.filter(a => a.verified).length === 0 ? (
              <div className="empty-state" style={{ paddingTop: 48 }}>
                <div className="empty-icon">💸</div>
                <p className="empty-text">No verified accounts</p>
                <p className="empty-sub">Verify an account first before making payments</p>
              </div>
            ) : (
              <>
                <h3 className="pay-section-title">Select Account</h3>
                <div className="pay-account-list">
                  {accounts.filter(a => a.verified).map(acc => (
                    <div key={acc.id} className="pay-account-row">
                      <div className="pay-acc-info">
                        <span className="pay-acc-emoji">{BANK_EMOJI[acc.bankName] || '🏦'}</span>
                        <div>
                          <div className="pay-acc-bank">{BANK_LABELS[acc.bankName] || acc.bankName}</div>
                          <div className="pay-acc-num">•••• {acc.accountNumber.slice(-4)}</div>
                        </div>
                      </div>
                      <div className="pay-acc-actions">
                        <button className="pay-action-btn pay-action-deposit" onClick={() => openPayModal(acc, 'deposit')}>⬇ Deposit</button>
                        <button className="pay-action-btn pay-action-withdraw" onClick={() => openPayModal(acc, 'withdraw')}>⬆ Withdraw</button>
                        <button className="pay-action-btn pay-action-send" onClick={() => openPayModal(acc, 'send')}>➤ Send</button>
                      </div>
                    </div>
                  ))}
                </div>
              </>
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

      {/* ── PAYMENT MODAL ── */}
      {selectedAcc && (
        <div className="modal-overlay" onClick={closePayModal}>
          <div className="modal-sheet" onClick={e => e.stopPropagation()}>
            {/* header */}
            <div className="modal-header">
              <div className="modal-acc-chip">
                <span>{BANK_EMOJI[selectedAcc.bankName] || '🏦'}</span>
                <span>{BANK_LABELS[selectedAcc.bankName] || selectedAcc.bankName} · •••• {selectedAcc.accountNumber.slice(-4)}</span>
              </div>
              <button className="modal-close" onClick={closePayModal}>✕</button>
            </div>

            {/* action tabs */}
            <div className="modal-tabs">
              {(['deposit', 'withdraw', 'send'] as PaymentAction[]).map(a => (
                <button
                  key={a}
                  className={`modal-tab ${payAction === a ? 'modal-tab-active-' + a : ''}`}
                  onClick={() => { setPayAction(a); setPayAmount(''); setToUser(''); dismissMsg(); }}
                >
                  {actionMeta[a].icon} {actionMeta[a].label}
                </button>
              ))}
            </div>

            {/* form */}
            <form onSubmit={handlePaySubmit} className="modal-form">
              {error && <div className="toast toast-error" style={{ marginBottom: 12 }}>⚠️ {error}<button className="toast-close" onClick={dismissMsg}>✕</button></div>}

              <label className="field-label">Amount (₹)</label>
              <input
                className="field-input modal-amount-input"
                type="number"
                min="1"
                step="0.01"
                placeholder="0.00"
                value={payAmount}
                onChange={e => setPayAmount(e.target.value)}
                required
              />

              {payAction === 'send' && (
                <>
                  <label className="field-label" style={{ marginTop: 14, display: 'block' }}>Recipient Username</label>
                  <input
                    className="field-input"
                    type="text"
                    placeholder="Enter username"
                    value={toUser}
                    onChange={e => setToUser(e.target.value)}
                    required
                  />
                </>
              )}

              <button
                type="submit"
                className={`modal-submit-btn modal-submit-${payAction}`}
                disabled={payLoading}
              >
                {payLoading ? 'Processing…' : `Confirm ${actionMeta[payAction].label}`}
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
