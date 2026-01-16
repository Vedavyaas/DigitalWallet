(() => {
  const $ = (id) => document.getElementById(id);

  const alertBox = $("alert");
  const authSection = $("authSection");
  const appSection = $("appSection");
  const subtitle = $("subtitle");
  const logoutBtn = $("logoutBtn");

  const TOKEN_KEY = "DW_TOKEN";

  function setAlert(message, type = "") {
    alertBox.style.display = "block";
    alertBox.className = `alert ${type}`.trim();
    alertBox.textContent = message;
  }

  function clearAlert() {
    alertBox.style.display = "none";
    alertBox.textContent = "";
    alertBox.className = "alert";
  }

  function getToken() {
    return sessionStorage.getItem(TOKEN_KEY);
  }

  function setToken(token) {
    sessionStorage.setItem(TOKEN_KEY, token);
  }

  function clearToken() {
    sessionStorage.removeItem(TOKEN_KEY);
  }

  function setLoggedInUi(isLoggedIn) {
    authSection.style.display = isLoggedIn ? "none" : "block";
    appSection.style.display = isLoggedIn ? "block" : "none";
    logoutBtn.style.display = isLoggedIn ? "inline-flex" : "none";
    subtitle.textContent = isLoggedIn
      ? "You are logged in."
      : "Login or create an account.";
  }

  async function apiFetch(path, opts = {}) {
    const token = getToken();
    const headers = new Headers(opts.headers || {});

    if (token) {
      headers.set("Authorization", `Bearer ${token}`);
    }

    return fetch(path, {
      ...opts,
      headers,
    });
  }

  async function login() {
    clearAlert();
    const username = $("loginUsername").value.trim();
    const password = $("loginPassword").value;

    if (!username || !password) {
      setAlert("Username and password are required", "error");
      return;
    }

    const res = await fetch("/login/account", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, password }),
    });

    if (!res.ok) {
      setAlert("Login failed", "error");
      return;
    }

    const data = await res.json();
    if (!data || !data.token) {
      setAlert("Login failed (no token)", "error");
      return;
    }

    setToken(data.token);
    setLoggedInUi(true);
    setAlert("Logged in", "success");
  }

  async function register() {
    clearAlert();
    const username = $("regUsername").value.trim();
    const password = $("regPassword").value;

    if (!username || !password) {
      setAlert("Username and password are required", "error");
      return;
    }

    const res = await fetch("/create/account", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, password }),
    });

    const text = await res.text();
    if (!res.ok) {
      setAlert(text || "Create account failed", "error");
      return;
    }

    setAlert(text || "User created", "success");
  }

  async function deposit() {
    clearAlert();
    const amount = parseFloat($("depositAmount").value);

    if (!Number.isFinite(amount) || amount <= 0) {
      setAlert("Amount must be positive", "error");
      return;
    }

    const res = await apiFetch(`/deposit?amount=${encodeURIComponent(amount)}`, {
      method: "POST",
    });

    const text = await res.text();
    if (!res.ok) {
      setAlert(text || "Deposit failed", "error");
      return;
    }

    setAlert(text || "Deposited", "success");
  }

  async function withdraw() {
    clearAlert();
    const amount = parseFloat($("withdrawAmount").value);

    if (!Number.isFinite(amount) || amount <= 0) {
      setAlert("Amount must be positive", "error");
      return;
    }

    const res = await apiFetch(`/withdraw?amount=${encodeURIComponent(amount)}`, {
      method: "POST",
    });

    const text = await res.text();
    if (!res.ok) {
      setAlert(text || "Withdraw failed", "error");
      return;
    }

    setAlert(text || "Withdrawn", "success");
  }

  async function refreshBalance() {
    clearAlert();

    const res = await apiFetch("/get/balance", { method: "GET" });
    const text = await res.text();

    if (!res.ok) {
      setAlert(text || "Failed to fetch balance", "error");
      return;
    }

    $("balanceValue").textContent = text;
    setAlert("Balance updated", "success");
  }

  function logout() {
    clearToken();
    setLoggedInUi(false);
    setAlert("Logged out", "");
  }

  // Wire events
  $("loginBtn").addEventListener("click", login);
  $("registerBtn").addEventListener("click", register);
  $("depositBtn").addEventListener("click", deposit);
  $("withdrawBtn").addEventListener("click", withdraw);
  $("balanceBtn").addEventListener("click", refreshBalance);
  logoutBtn.addEventListener("click", logout);

  // Initial state
  setLoggedInUi(!!getToken());
})();
