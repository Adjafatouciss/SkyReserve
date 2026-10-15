if (!requireUserAuth()) {}

const reservationId = localStorage.getItem('reservationId');
const montant = localStorage.getItem('montantPaiement');

function remplirInfosPaiement() {
  const inputMontant = document.getElementById('montantAffiche');
  if (inputMontant) inputMontant.value = montant ? formatMoney(montant) : '';
}

async function payer() {
  const msg = document.getElementById('paiementMessage');
  const nom = document.getElementById('nomCarte').value.trim();
  const numero = document.getElementById('numeroCarte').value.trim();
  const cvv = document.getElementById('cvv').value.trim();
  const expiration = document.getElementById('expiration')?.value.trim() || '';

  msg.textContent = '';

  if (!reservationId || !montant) {
    msg.textContent = 'Aucune réservation à payer.';
    return;
  }

  if (!nom || !numero || !cvv || !expiration) {
    msg.textContent = 'Veuillez remplir tous les champs du paiement.';
    return;
  }

  if (numero.replace(/\s/g, '').length < 8) {
    msg.textContent = 'Numéro de carte invalide.';
    return;
  }

  try {
    const paiements = await fetchJsonOrEmpty(`${API_BASE}/afficherTousLesPaiements`);
    const dejaPaye = paiements.find(p => p?.reservation?.id == reservationId);

    if (!dejaPaye) {
      await fetchJson(`${API_BASE}/ajouterPaiement`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          montant: parseFloat(montant),
          modePaiement: 'Carte bancaire',
          datePaiement: new Date().toISOString().split('T')[0],
          statutPaiement: 'Payé',
          reservation: { id: parseInt(reservationId, 10) }
        })
      });
    }

    const reservation = await fetchJson(`${API_BASE}/reservation/${reservationId}`);
    const billets = await fetchJsonOrEmpty(`${API_BASE}/afficherTousLesBillets`);
    const billetExistant = billets.find(b => b?.reservation?.id == reservationId);

    if (!billetExistant) {
      const numeroBillet = `BL-${reservationId}-${Date.now()}`;
      const siege = `A${Math.floor(Math.random() * 30) + 1}`;
      await fetchJson(`${API_BASE}/ajouterBillet`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          numeroBillet,
          prixBillet: parseFloat(montant),
          siege,
          classeVoyage: 'Économique',
          reservation: { id: parseInt(reservationId, 10) },
          vol: { id: reservation.vol.id }
        })
      });
    }

    msg.style.color = '#bbf7d0';
    msg.textContent = 'Paiement effectué avec succès. Votre billet a été généré.';

    localStorage.removeItem('reservationId');
    localStorage.removeItem('montantPaiement');

    setTimeout(() => {
      window.location.href = 'mesBillets.html';
    }, 1200);
  } catch (error) {
    msg.style.color = '#ffd2d2';
    msg.textContent = toErrorMessage(error);
  }
}

remplirInfosPaiement();
