if (!requireUserAuth()) {}

const currentUser = getUserSession();
const currentUserId = currentUser?.id;
let allVols = [];
let allReservations = [];
let allPaiements = [];
let allBillets = [];

function renderDashboardSidebar() {
  const zone = document.getElementById("sidebarContainer");
  if (zone) zone.innerHTML = createSidebar("dashboard");
}

async function chargerDonneesDashboard() {
  renderDashboardSidebar();

  document.getElementById("welcomeTitle").textContent = `Bienvenue ${escapeHtml(getUserFullName())}`;
  document.getElementById("welcomeSubtitle").textContent = "Recherchez un vol, réservez, payez et suivez tous vos billets depuis un seul espace.";

  try {
    const [vols, reservations, paiements, billets] = await Promise.all([
      fetchJsonOrEmpty(`${API_BASE}/vols`),
      fetchJsonOrEmpty(`${API_BASE}/reservations/utilisateur/${currentUserId}`),
      fetchJsonOrEmpty(`${API_BASE}/afficherTousLesPaiements`),
      fetchJsonOrEmpty(`${API_BASE}/afficherTousLesBillets`)
    ]);

    allVols = Array.isArray(vols) ? vols : [];
    allReservations = Array.isArray(reservations) ? reservations : [];
    allPaiements = (Array.isArray(paiements) ? paiements : []).filter(p => p?.reservation?.utilisateur?.id == currentUserId);
    allBillets = (Array.isArray(billets) ? billets : []).filter(b => b?.reservation?.utilisateur?.id == currentUserId);

    afficherStatistiques();
    afficherVols(allVols);
    afficherReservationsRecentes();
    afficherPaiementsRecents();
  } catch (error) {
    document.getElementById("resultatsVols").innerHTML = `<div class="empty-state">${escapeHtml(toErrorMessage(error))}</div>`;
  }
}

function afficherStatistiques() {
  const reservationsActives = allReservations.filter(r => (r.statutReservation || "").toLowerCase() !== "annulée");
  const totalPaye = allPaiements.reduce((sum, p) => sum + Number(p.montant || 0), 0);

  document.getElementById("countVolsDisponibles").textContent = allVols.length;
  document.getElementById("countUserReservations").textContent = allReservations.length;
  document.getElementById("countBillets").textContent = allBillets.length;
  document.getElementById("countPaiements").textContent = formatMoney(totalPaye);
  document.getElementById("countReservationsActives").textContent = reservationsActives.length;
}

function afficherVols(vols) {
  const zone = document.getElementById("resultatsVols");

  if (!vols || vols.length === 0) {
    zone.innerHTML = `<div class="empty-state">Aucun vol disponible pour cette recherche.</div>`;
    return;
  }

  zone.innerHTML = vols.map(v => `
    <div class="card card-flight">
      <div class="flight-top">
        <span class="badge primary">${escapeHtml(v.compagnieAerienne?.nomCompagnie || v.compagnieAerienne?.nom || "Compagnie")}</span>
        <strong>${escapeHtml(v.numeroVol)}</strong>
      </div>
      <h3>${escapeHtml(v.aeroportDepart?.ville)} → ${escapeHtml(v.aeroportArrivee?.ville)}</h3>
      <p><strong>Départ :</strong> ${escapeHtml(v.dateDepart)} à ${escapeHtml(v.heureDepart)}</p>
      <p><strong>Arrivée :</strong> ${escapeHtml(v.dateArrivee)} à ${escapeHtml(v.heureArrivee)}</p>
      <p><strong>Aéroports :</strong> ${escapeHtml(v.aeroportDepart?.nomAeroport)} → ${escapeHtml(v.aeroportArrivee?.nomAeroport)}</p>
      <div class="card-footer-line">
        <span class="price-tag">${formatMoney(v.prix)}</span>
        <button class="btn btn-primary" onclick="reserverVol(${v.id})">Réserver</button>
      </div>
    </div>
  `).join("");
}

function afficherReservationsRecentes() {
  const zone = document.getElementById("recentReservations");
  if (!zone) return;

  if (!allReservations.length) {
    zone.innerHTML = `<div class="empty-state">Aucune réservation pour le moment.</div>`;
    return;
  }

  zone.innerHTML = allReservations.slice(0, 3).map(r => `
    <div class="card compact-card">
      <h3>${escapeHtml(r.vol?.numeroVol || "Vol")}</h3>
      <p>${escapeHtml(r.vol?.aeroportDepart?.ville)} → ${escapeHtml(r.vol?.aeroportArrivee?.ville)}</p>
      <p>Date réservation : ${escapeHtml(r.dateReservation)}</p>
      <span class="badge ${(r.statutReservation || '').toLowerCase() === 'annulée' ? 'warning' : 'success'}">${escapeHtml(r.statutReservation)}</span>
    </div>
  `).join("");
}

function afficherPaiementsRecents() {
  const zone = document.getElementById("recentPaiements");
  if (!zone) return;

  if (!allPaiements.length) {
    zone.innerHTML = `<div class="empty-state">Aucun paiement enregistré.</div>`;
    return;
  }

  zone.innerHTML = allPaiements.slice(0, 3).map(p => `
    <div class="card compact-card">
      <h3>${formatMoney(p.montant)}</h3>
      <p>Mode : ${escapeHtml(p.modePaiement)}</p>
      <p>Date : ${escapeHtml(p.datePaiement)}</p>
      <span class="badge success">${escapeHtml(p.statutPaiement)}</span>
    </div>
  `).join("");
}

async function chercherVols() {
  const depart = document.getElementById("depart").value.trim().toLowerCase();
  const arrivee = document.getElementById("arrivee").value.trim().toLowerCase();
  const date = document.getElementById("dateRecherche").value.trim().toLowerCase();
  const numeroVol = document.getElementById("numeroVolRecherche").value.trim().toLowerCase();

  const filtres = allVols.filter(v =>
    (!depart || (v.aeroportDepart?.ville || "").toLowerCase().includes(depart)) &&
    (!arrivee || (v.aeroportArrivee?.ville || "").toLowerCase().includes(arrivee)) &&
    (!date || (v.dateDepart || "").toLowerCase().includes(date)) &&
    (!numeroVol || (v.numeroVol || "").toLowerCase().includes(numeroVol))
  );

  afficherVols(filtres);
}

function reinitialiserRecherche() {
  ["depart", "arrivee", "dateRecherche", "numeroVolRecherche"].forEach(id => {
    const el = document.getElementById(id);
    if (el) el.value = "";
  });
  afficherVols(allVols);
}

async function reserverVol(volId) {
  const confirmer = confirm("Confirmer cette réservation ?");
  if (!confirmer) return;

  try {
    const today = new Date().toISOString().split("T")[0];
    const vol = allVols.find(v => v.id === volId) || await fetchJson(`${API_BASE}/vol/${volId}`);

    const reservation = await fetchJson(`${API_BASE}/ajouterReservation`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        dateReservation: today,
        statutReservation: "Confirmée",
        utilisateur: { id: parseInt(currentUserId, 10) },
        vol: { id: volId }
      })
    });

    localStorage.setItem("reservationId", reservation.id);
    localStorage.setItem("montantPaiement", vol.prix);
    window.location.href = "paiement.html";
  } catch (error) {
    alert(toErrorMessage(error));
  }
}

chargerDonneesDashboard();
