requireAuth();

function resetPaiementForm() {
  ["paiementId","montant","modePaiement","datePaiement","statutPaiement","reservationId"].forEach(id => document.getElementById(id).value = "");
  clearMessage("paiementMessage");
}

function editPaiement(encoded) {
  const paiement = JSON.parse(encoded.replaceAll("&apos;", "'"));
  document.getElementById("paiementId").value = paiement.id;
  document.getElementById("montant").value = paiement.montant;
  document.getElementById("modePaiement").value = paiement.modePaiement;
  document.getElementById("datePaiement").value = paiement.datePaiement;
  document.getElementById("statutPaiement").value = paiement.statutPaiement;
  document.getElementById("reservationId").value = paiement.reservation?.id || "";
  showMessage("paiementMessage", `Mode modification activé pour le paiement #${paiement.id}`, false);
}

async function loadPaiements() {
  const tbody = document.getElementById("paiementsTableBody");
  tbody.innerHTML = "<tr><td colspan='7'>Chargement...</td></tr>";
  try {
    const paiements = await fetchJson(`${API_BASE}/afficherTousLesPaiements`);
    tbody.innerHTML = paiements.map(p => `
      <tr>
        <td>${p.id}</td>
        <td>${safeText(p.montant)}</td>
        <td>${safeText(p.modePaiement)}</td>
        <td>${safeText(p.datePaiement)}</td>
        <td><span class="badge warning">${safeText(p.statutPaiement)}</span></td>
        <td>${safeText(p.reservation?.id)}</td>
        <td>
          <div class="row-actions">
            <button class="btn btn-secondary" onclick='editPaiement("${serializeForAttr(p)}")'>Modifier</button>
            <button class="btn btn-danger" onclick='deletePaiement(${p.id})'>Supprimer</button>
          </div>
        </td>
      </tr>`).join("");
  } catch (error) {
    tbody.innerHTML = `<tr><td colspan='7' class='empty-state'>${toErrorMessage(error)}</td></tr>`;
  }
}

async function savePaiement() {
  const id = document.getElementById("paiementId").value;
  const payload = {
    montant: parseFloat(document.getElementById("montant").value),
    modePaiement: document.getElementById("modePaiement").value.trim(),
    datePaiement: document.getElementById("datePaiement").value.trim(),
    statutPaiement: document.getElementById("statutPaiement").value.trim(),
    reservation: { id: parseInt(document.getElementById("reservationId").value) }
  };

  if (!payload.montant || !payload.modePaiement || !payload.datePaiement || !payload.statutPaiement || !payload.reservation.id) {
    showMessage("paiementMessage", "Veuillez remplir tous les champs avec un ID réservation valide.");
    return;
  }

  try {
    await fetchJson(id ? `${API_BASE}/modifierPaiement/${id}` : `${API_BASE}/ajouterPaiement`, {
      method: id ? "PUT" : "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });
    showMessage("paiementMessage", id ? "Paiement modifié avec succès." : "Paiement ajouté avec succès.", false);
    resetPaiementForm();
    loadPaiements();
  } catch (error) {
    showMessage("paiementMessage", toErrorMessage(error));
  }
}

async function deletePaiement(id) {
  if (!confirm(`Supprimer le paiement #${id} ?`)) return;
  try {
    await fetchJson(`${API_BASE}/supprimerPaiement/${id}`, { method: "DELETE" });
    loadPaiements();
  } catch (error) {
    alert(toErrorMessage(error));
  }
}

loadPaiements();
