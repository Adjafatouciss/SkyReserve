if (!requireUserAuth()) {}

const reservationUser = getUserSession();

function renderReservationSidebar() {
  const zone = document.getElementById("sidebarContainer");
  if (zone) zone.innerHTML = createSidebar("reservations");
}

async function chargerReservations() {
  renderReservationSidebar();
  const zone = document.getElementById("reservationsList");

  try {
    const [reservations, paiements, billets] = await Promise.all([
      fetchJsonOrEmpty(`${API_BASE}/reservations/utilisateur/${reservationUser.id}`),
      fetchJsonOrEmpty(`${API_BASE}/afficherTousLesPaiements`),
      fetchJsonOrEmpty(`${API_BASE}/afficherTousLesBillets`)
    ]);

    const paiementsUser = paiements.filter(p => p?.reservation?.utilisateur?.id == reservationUser.id);
    const billetsUser = billets.filter(b => b?.reservation?.utilisateur?.id == reservationUser.id);

    if (!reservations.length) {
      zone.innerHTML = `<div class="empty-state">Aucune réservation enregistrée.</div>`;
      return;
    }

    zone.innerHTML = reservations.map(r => {
      const paiement = paiementsUser.find(p => p?.reservation?.id === r.id);
      const billet = billetsUser.find(b => b?.reservation?.id === r.id);
      const annulee = (r.statutReservation || '').toLowerCase() === 'annulée';
      return `
        <div class="card reservation-card">
          <div class="reservation-header">
            <div>
              <h3>${escapeHtml(r.vol?.numeroVol || 'Vol')}</h3>
              <p>${escapeHtml(r.vol?.aeroportDepart?.ville)} → ${escapeHtml(r.vol?.aeroportArrivee?.ville)}</p>
            </div>
            <span class="badge ${annulee ? 'warning' : 'success'}">${escapeHtml(r.statutReservation)}</span>
          </div>
          <p><strong>Date du vol :</strong> ${escapeHtml(r.vol?.dateDepart)} à ${escapeHtml(r.vol?.heureDepart)}</p>
          <p><strong>Prix :</strong> ${formatMoney(r.vol?.prix)}</p>
          <p><strong>Paiement :</strong> ${paiement ? escapeHtml(paiement.statutPaiement) : 'Non payé'}</p>
          <p><strong>Billet :</strong> ${billet ? escapeHtml(billet.numeroBillet) : 'Pas encore généré'}</p>
          <div class="actions">
            ${!annulee ? `<button class="btn btn-danger" onclick="annulerReservation(${r.id})">Annuler</button>` : ''}
            ${!paiement && !annulee ? `<button class="btn btn-primary" onclick="payerReservation(${r.id}, ${Number(r.vol?.prix || 0)})">Payer</button>` : ''}
            ${billet ? `<a class="btn btn-secondary" href="mesBillets.html">Voir billet</a>` : ''}
          </div>
        </div>`;
    }).join('');
  } catch (error) {
    zone.innerHTML = `<div class="empty-state">${escapeHtml(toErrorMessage(error))}</div>`;
  }
}

function payerReservation(reservationId, montant) {
  localStorage.setItem('reservationId', reservationId);
  localStorage.setItem('montantPaiement', montant);
  window.location.href = 'paiement.html';
}

async function annulerReservation(id) {
  if (!confirm('Voulez-vous annuler cette réservation ?')) return;

  try {
    const reservation = await fetchJson(`${API_BASE}/reservation/${id}`);
    await fetchJson(`${API_BASE}/modifierReservation/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        dateReservation: reservation.dateReservation,
        statutReservation: 'Annulée',
        utilisateur: { id: reservation.utilisateur.id },
        vol: { id: reservation.vol.id }
      })
    });

    chargerReservations();
  } catch (error) {
    alert(toErrorMessage(error));
  }
}

chargerReservations();
