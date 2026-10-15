requireAuth();

async function loadDashboardStats() {
  try {
    const admins = await fetchJson(`${API_BASE}/afficherTousLesAdmins`);
    document.getElementById("countAdmins").textContent = admins.length;

    const users = await fetchJson(`${API_BASE}/afficherTousLesUtilisateurs`);
    document.getElementById("countUsers").textContent = users.length;

    const compagnies = await fetchJson(`${API_BASE}/afficherToutesLesCompagnies`);
    document.getElementById("countCompagnies").textContent = compagnies.length;

    const avions = await fetchJson(`${API_BASE}/afficherTousLesAvions`);
    document.getElementById("countAvions").textContent = avions.length;

    const aeroports = await fetchJson(`${API_BASE}/afficherTousLesAeroports`);
    document.getElementById("countAeroports").textContent = aeroports.length;

    const vols = await fetchJson(`${API_BASE}/afficherTousLesVols`);
    document.getElementById("countVols").textContent = vols.length;

    const reservations = await fetchJson(`${API_BASE}/afficherToutesLesReservations`);
    document.getElementById("countReservations").textContent = reservations.length;

    const billets = await fetchJson(`${API_BASE}/afficherTousLesBillets`);
    document.getElementById("countBillets").textContent = billets.length;

    const paiements = await fetchJson(`${API_BASE}/afficherTousLesPaiements`);
    document.getElementById("countPaiements").textContent = paiements.length;

  } catch (e) {
    console.error("Erreur dashboard :", e);
  }
}
async function loadVols() {
    try {
        const vols = await fetchJson(`${API_BASE}/vols`);

        if (vols.length === 0) {
            document.getElementById("volsList").innerHTML = "<p>Aucun vol disponible.</p>";
            return;
        }

        document.getElementById("volsList").innerHTML = vols.map(v => `
            <div class="vol-item">
                <p><strong>Vol :</strong> ${v.numeroVol}</p>
                <p><strong>Départ :</strong> ${v.aeroportDepart.ville} (${v.aeroportDepart.pays})</p>
                <p><strong>Arrivée :</strong> ${v.aeroportArrivee.ville} (${v.aeroportArrivee.pays})</p>
                <p><strong>Date :</strong> ${v.dateDepart} — ${v.heureDepart}</p>
                <p><strong>Prix :</strong> ${v.prix} $</p>
                <hr>
            </div>
        `).join("");
    } catch (e) {
        document.getElementById("volsList").innerHTML =
            "<p style='color:red;'>Erreur de chargement des vols.</p>";
    }
}

loadVols();

loadDashboardStats();
