if (!requireUserAuth()) {}

const billetUser = getUserSession();

function renderBilletSidebar() {
  const zone = document.getElementById("sidebarContainer");
  if (zone) zone.innerHTML = createSidebar("billets");
}

async function chargerBillets() {
  renderBilletSidebar();
  const zone = document.getElementById("billetsList");

  try {
    const billets = await fetchJsonOrEmpty(`${API_BASE}/afficherTousLesBillets`);
    const filtres = billets.filter(b => b?.reservation?.utilisateur?.id == billetUser.id);

    if (!filtres.length) {
      zone.innerHTML = `<div class="empty-state">Aucun billet disponible.</div>`;
      return;
    }

    zone.innerHTML = filtres.map(b => `
      <div class="card billet-card">
        <div class="reservation-header">
          <h3>${escapeHtml(b.numeroBillet)}</h3>
          <span class="badge info">${escapeHtml(b.classeVoyage)}</span>
        </div>
        <p><strong>Trajet :</strong> ${escapeHtml(b.vol?.aeroportDepart?.ville)} → ${escapeHtml(b.vol?.aeroportArrivee?.ville)}</p>
        <p><strong>Vol :</strong> ${escapeHtml(b.vol?.numeroVol)}</p>
        <p><strong>Date :</strong> ${escapeHtml(b.vol?.dateDepart)} à ${escapeHtml(b.vol?.heureDepart)}</p>
        <p><strong>Siège :</strong> ${escapeHtml(b.siege)}</p>
        <p><strong>Prix :</strong> ${formatMoney(b.prixBillet)}</p>
        <div class="qr-box" id="qr${b.id}"></div>
        <div class="actions">
          <button class="btn btn-primary" onclick="telechargerBillet('${escapeHtml(b.numeroBillet)}','${escapeHtml(b.vol?.numeroVol)}','${escapeHtml(b.vol?.aeroportDepart?.ville)}','${escapeHtml(b.vol?.aeroportArrivee?.ville)}','${escapeHtml(b.siege)}','${formatMoney(b.prixBillet)}')">Télécharger</button>
        </div>
      </div>
    `).join('');

    setTimeout(() => {
      filtres.forEach(b => {
        const qrZone = document.getElementById(`qr${b.id}`);
        if (qrZone) {
          qrZone.innerHTML = '';
          new QRCode(qrZone, {
            text: `${b.numeroBillet} | ${b.vol?.numeroVol} | ${b.vol?.aeroportDepart?.ville} -> ${b.vol?.aeroportArrivee?.ville}`,
            width: 110,
            height: 110
          });
        }
      });
    }, 100);
  } catch (error) {
    zone.innerHTML = `<div class="empty-state">${escapeHtml(toErrorMessage(error))}</div>`;
  }
}

function telechargerBillet(num, vol, dep, arr, siege, prix) {
  const contenu = `BILLET SKYRESERVE\n\nNuméro : ${num}\nVol : ${vol}\nTrajet : ${dep} -> ${arr}\nSiège : ${siege}\nPrix : ${prix}`;
  const blob = new Blob([contenu], { type: 'text/plain;charset=utf-8' });
  const a = document.createElement('a');
  a.href = URL.createObjectURL(blob);
  a.download = `${num}.txt`;
  a.click();
}

chargerBillets();
