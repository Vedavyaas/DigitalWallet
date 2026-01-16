(() => {
  const $ = (id) => document.getElementById(id);

  const TOKEN_KEY = "DW_TOKEN";
  const USER_KEY = "DW_USER";

  const SERVICE_PATH_CANDIDATES = [
    "/WALLETSERVICE",
    "/WalletService",
    "/walletservice",
  ];

  function getToken() {
    return sessionStorage.getItem(TOKEN_KEY);
  }

  function setToken(token) {
    sessionStorage.setItem(TOKEN_KEY, token);
  }

  function clearToken() {
    sessionStorage.removeItem(TOKEN_KEY);
  }

  function setUser(username) {
    sessionStorage.setItem(USER_KEY, username);
  }

  function getUser() {
    return sessionStorage.getItem(USER_KEY);
  }

  function clearUser() {
    sessionStorage.removeItem(USER_KEY);
  }

  function setAlert(message, type = "") {
    const alertBox = $("alert");
    if (!alertBox) return;
    alertBox.style.display = "block";
    alertBox.className = `alert ${type}`.trim();
    alertBox.textContent = message;
  }

  function clearAlert() {
    const alertBox = $("alert");
    if (!alertBox) return;
    alertBox.style.display = "none";
    alertBox.textContent = "";
    alertBox.className = "alert";
  }

  async function jsonPost(path, body) {
    return fetch(path, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });
  }

  async function apiFetch(path, opts = {}) {
    const token = getToken();
    const headers = new Headers(opts.headers || {});
    if (token) headers.set("Authorization", `Bearer ${token}`);

    return fetch(path, { ...opts, headers });
  }

  async function walletFetch(pathAfterService, opts) {
    let lastResponse;
    for (const prefix of SERVICE_PATH_CANDIDATES) {
      const res = await apiFetch(`${prefix}${pathAfterService}`, opts);
      if (res.status !== 404) return res;
      lastResponse = res;
    }
    return lastResponse;
  }

  async function handleLogin() {
    clearAlert();
    const username = $("username")?.value?.trim();
    const password = $("password")?.value;

    if (!username || !password) {
      setAlert("Username and password are required", "error");
      return;
    }

    const res = await jsonPost("/login/account", { username, password });
    if (!res.ok) {
      setAlert("Login failed", "error");
      return;
    }

    const data = await res.json();
    if (!data?.token) {
      setAlert("Login failed (no token)", "error");
      return;
    }

    setToken(data.token);
    setUser(username);
    window.location.href = "/ui/dashboard";
  }

  async function handleRegister() {
    clearAlert();
    const username = $("username")?.value?.trim();
    const password = $("password")?.value;

    if (!username || !password) {
      setAlert("Username and password are required", "error");
      return;
    }

    const res = await jsonPost("/create/account", { username, password });
    const text = await res.text();
    if (!res.ok) {
      setAlert(text || "Create account failed", "error");
      return;
    }

    setAlert(text || "User created", "success");
    setTimeout(() => (window.location.href = "/ui/login"), 600);
  }

  function requireTokenOrRedirect() {
    if (!getToken()) {
      window.location.href = "/ui/login";
      return false;
    }
    return true;
  }

  async function handleDeposit() {
    clearAlert();
    if (!requireTokenOrRedirect()) return;
    const amount = parseFloat($("depositAmount")?.value);
    if (!Number.isFinite(amount) || amount <= 0) {
      setAlert("Amount must be positive", "error");
      return;
    }

    const res = await walletFetch(`/deposit?amount=${encodeURIComponent(amount)}`, {
      method: "POST",
    });

    const text = await res.text();
    if (!res.ok) {
      setAlert(text || "Deposit failed", "error");
      return;
    }

    setAlert(text || "Deposited", "success");
  }

  async function handleWithdraw() {
    clearAlert();
    if (!requireTokenOrRedirect()) return;
    const amount = parseFloat($("withdrawAmount")?.value);
    if (!Number.isFinite(amount) || amount <= 0) {
      setAlert("Amount must be positive", "error");
      return;
    }

    const res = await walletFetch(`/withdraw?amount=${encodeURIComponent(amount)}`, {
      method: "POST",
    });

    const text = await res.text();
    if (!res.ok) {
      setAlert(text || "Withdraw failed", "error");
      return;
    }

    setAlert(text || "Withdrawn", "success");
  }

  async function handleBalance() {
    clearAlert();
    if (!requireTokenOrRedirect()) return;
    const res = await walletFetch("/get/balance", { method: "GET" });
    const text = await res.text();

    if (!res.ok) {
      setAlert(text || "Failed to fetch balance", "error");
      return;
    }

    const el = $("balanceValue");
    if (el) el.textContent = text;
    setAlert("Balance updated", "success");
  }

  function handleLogout() {
    clearToken();
    clearUser();
    window.location.href = "/ui/login";
  }

  function init() {
    const page = window.DW_PAGE;

    if (page === "login") {
      $("loginBtn")?.addEventListener("click", handleLogin);
      return;
    }

    if (page === "register") {
      $("registerBtn")?.addEventListener("click", handleRegister);
      return;
    }

    if (page === "dashboard") {
      if (!requireTokenOrRedirect()) return;
      $("depositBtn")?.addEventListener("click", handleDeposit);
      $("withdrawBtn")?.addEventListener("click", handleWithdraw);
      $("balanceBtn")?.addEventListener("click", handleBalance);
      $("logoutBtn")?.addEventListener("click", handleLogout);

      const who = $("whoami");
      if (who) {
        who.textContent = getUser() ? `User: ${getUser()}` : "Logged in";
      }
    }
  }

  document.addEventListener("DOMContentLoaded", init);
})();
