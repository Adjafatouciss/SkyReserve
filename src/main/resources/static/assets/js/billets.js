requireAuth();

function resetBilletForm() {
  ["billetId","numeroBillet","prixBillet","siege","classeVoyage","reservationId","volId"].forEach(id => document.getElementById(id).value = "");
  clearMessage("billetMessage");
}

function editBillet(encoded) {
  const billet = JSON.parse(encoded.replaceAll("&apos;", "'"));
  document.getElementById("billetId").value = billet.id;
  document.getElementById("numeroBillet").value = billet.numeroBillet;
  document.getElementById("prixBillet").value = billet.prixBillet;
  document.getElementById("siege").value = billet.siege;
  document.getElementById("classeVoyage").value = billet.classeVoyage;
  document.getElementById("reservationId").value = billet.reservation?.id || "";
  document.getElementById("volId").value = billet.vol?.id || "";
  showMessage("billetMessage", `Mode modification activé pour le billet #${billet.id}`, false);
}

async function loadBillets() {
  const tbody = document.getElementById("billetsTableBody");
  tbody.innerHTML = "<tr><td colspan='7'>Chargement...</td></tr>";
  try {
    const billets = await fetchJson(`${API_BASE}/afficherTousLesBillets`);
    tbody.innerHTML = billets.map(b => `
      <tr>
        <td>${b.id}</td>
        <td>${safeText(b.numeroBillet)}</td>
        <td>${safeText(b.prixBillet)}</td>
        <td><span class="badge success">${safeText(b.classeVoyage)}</span></td>
        <td>${safeText(b.siege)}</td>
        <td>${safeText(b.vol?.numeroVol)}</td>
        <td>
          <div class="row-actions">
            <button class="btn btn-secondary" onclick='editBillet("${serializeForAttr(b)}")'>Modifier</button>
            <button class="btn btn-danger" onclick='deleteBillet(${b.id})'>Supprimer</button>
          </div>
        </td>
      </tr>`).join("");
  } catch (error) {
    tbody.innerHTML = `<tr><td colspan='7' class='empty-state'>${toErrorMessage(error)}</td></tr>`;
  }
}

async function saveBillet() {
  const id = document.getElementById("billetId").value;
  const payload = {
    numeroBillet: document.getElementById("numeroBillet").value.trim(),
    prixBillet: parseFloat(document.getElementById("prixBillet").value),
    siege: document.getElementById("siege").value.trim(),
    classeVoyage: document.getElementById("classeVoyage").value.trim(),
    reservation: { id: parseInt(document.getElementById("reservationId").value) },
    vol: { id: parseInt(document.getElementById("volId").value) }
  };

  if (!payload.numeroBillet || !payload.prixBillet || !payload.siege || !payload.classeVoyage || !payload.reservation.id || !payload.vol.id) {
    showMessage("billetMessage", "Veuillez remplir tous les champs avec des IDs valides.");
    return;
  }

  try {
    await fetchJson(id ? `${API_BASE}/modifierBillet/${id}` : `${API_BASE}/ajouterBillet`, {
      method: id ? "PUT" : "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });
    showMessage("billetMessage", id ? "Billet modifié avec succès." : "Billet ajouté avec succès.", false);
    resetBilletForm();
    loadBillets();
  } catch (error) {
    showMessage("billetMessage", toErrorMessage(error));
  }
}

async function deleteBillet(id) {
  if (!confirm(`Supprimer le billet #${id} ?`)) return;
  try {
    await fetchJson(`${API_BASE}/supprimerBillet/${id}`, { method: "DELETE" });
    loadBillets();
  } catch (error) {
    alert(toErrorMessage(error));
  }
}

loadBillets();
