
const API_BASE = "/Reservation_Billets/api/reservationBillets";
function setAdminSession(admin) {
  if (!admin) return;
  admin.role = admin.role || "ADMIN";
  localStorage.setItem("admin", JSON.stringify(admin));
  localStorage.setItem("adminRole", admin.role);
}

function getAdminSession() {
  const admin = JSON.parse(localStorage.getItem("admin") || "null");
  if (!admin) return null;
  admin.role = admin.role || localStorage.getItem("adminRole") || "ADMIN";
  return admin;
}

function clearAdminSession() {
  ["admin", "adminRole"].forEach(k => localStorage.removeItem(k));
}

function requireAdminAuth() {
  const admin = getAdminSession();
  if (!admin || admin.role !== "ADMIN") {
    clearAdminSession();
    window.location.href = "login.html";
    return false;
  }
  return true;
}

function requireAuth() {
  return requireAdminAuth();
}

function logoutAdmin() {
  clearAdminSession();
  window.location.href = "login.html";
}

function setUserSession(user) {
  if (!user) return;
  user.role = user.role || "UTILISATEUR";
  localStorage.setItem("utilisateur", JSON.stringify(user));
  localStorage.setItem("userId", user.id ?? "");
  localStorage.setItem("userNom", user.nom ?? "");
  localStorage.setItem("userPrenom", user.prenom ?? "");
  localStorage.setItem("userEmail", user.email ?? "");
  localStorage.setItem("userTelephone", user.telephone ?? "");
  localStorage.setItem("userRole", user.role);
}

function clearUserSession() {
  ["utilisateur", "userId", "userNom", "userPrenom", "userEmail", "userTelephone", "userRole", "reservationId", "montantPaiement"].forEach(k => localStorage.removeItem(k));
}

function getUserSession() {
  const raw = JSON.parse(localStorage.getItem("utilisateur") || "null");
  if (raw && raw.id) {
    raw.role = raw.role || localStorage.getItem("userRole") || "UTILISATEUR";
    return raw;
  }
  const userId = localStorage.getItem("userId");
  if (!userId) return null;
  return {
    id: parseInt(userId, 10),
    nom: localStorage.getItem("userNom") || "",
    prenom: localStorage.getItem("userPrenom") || "",
    email: localStorage.getItem("userEmail") || "",
    telephone: localStorage.getItem("userTelephone") || "",
    role: localStorage.getItem("userRole") || "UTILISATEUR"
  };
}

function requireUserAuth() {
  const user = getUserSession();
  if (!user || user.role !== "UTILISATEUR") {
    clearUserSession();
    window.location.href = "LoginUtilisateur.html";
    return false;
  }
  return true;
}

function logout() {
  clearUserSession();
  window.location.href = "LoginUtilisateur.html";
}

function showMessage(elementId, message, isError = true) {
  const el = document.getElementById(elementId);
  if (!el) return;
  el.textContent = message;
  el.style.color = isError ? "#ffd2d2" : "#bbf7d0";
}

function clearMessage(elementId) {
  const el = document.getElementById(elementId);
  if (!el) return;
  el.textContent = "";
}

function safeText(value, fallback = "-") {
  return value === null || value === undefined || value === "" ? fallback : value;
}

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}

function formatMoney(value) {
  const amount = Number(value || 0);
  return `${amount.toFixed(2)} $`;
}

function formatDate(value) {
  if (!value) return "-";
  return value;
}

function serializeForAttr(obj) {
  return JSON.stringify(obj).replace(/'/g, "&apos;");
}

function toErrorMessage(error) {
  return error?.message || "Une erreur est survenue.";
}

async function fetchJson(url, options = {}) {
  const response = await fetch(url, options);
  const text = await response.text();
  let data = null;
  try {
    data = text ? JSON.parse(text) : null;
  } catch {
    data = text;
  }
  if (!response.ok) {
    throw new Error(typeof data === "string" ? data : text || "Erreur serveur.");
  }
  return data;
}

async function fetchJsonOrEmpty(url, options = {}) {
  try {
    return await fetchJson(url, options);
  } catch (error) {
    if ((error?.message || "").toLowerCase().includes("aucun")) {
      return [];
    }
    throw error;
  }
}

function getUserFullName(user = getUserSession()) {
  if (!user) return "Utilisateur";
  return `${user.prenom || ""} ${user.nom || ""}`.trim() || user.nom || "Utilisateur";
}

function createSidebar(activePage) {
  const items = [
    ["dashboardUtilisateur.html", "Dashboard", "dashboard"],
    ["mesReservation.html", "Mes réservations", "reservations"],
    ["mesBillets.html", "Mes billets", "billets"],
    ["mesPaiements.html", "Mes paiements", "paiements"],
    ["monProfil.html", "Mon profil", "profil"]
  ];

  return `
    <aside class="sidebar">
      <div class="brand">
        <h2>SkyReserve</h2>
        <p>Espace utilisateur</p>
      </div>
      <div class="user-mini-card">
        <strong>${escapeHtml(getUserFullName())}</strong>
        <span>${escapeHtml(getUserSession()?.email || "")}</span>
      </div>
      <nav class="nav-links">
        ${items.map(([href, label, key]) => `
          <a href="${href}" class="${activePage === key ? "active" : ""}">${label}</a>
        `).join("")}
        <a href="#" onclick="logout()">Déconnexion</a>
      </nav>
    </aside>`;
}
