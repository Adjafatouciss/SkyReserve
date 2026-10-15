requireAuth();

function resetVolForm() {
  ["volId","numeroVol","dateDepart","heureDepart","dateArrivee","heureArrivee","prix","compagnieId","avionId","aeroportDepartId","aeroportArriveeId"]
    .forEach(id => document.getElementById(id).value = "");
  clearMessage("volMessage");
}

function editVol(encoded) {
  const vol = JSON.parse(encoded.replaceAll("&apos;", "'"));
  document.getElementById("volId").value = vol.id;
  document.getElementById("numeroVol").value = vol.numeroVol;
  document.getElementById("dateDepart").value = vol.dateDepart;
  document.getElementById("heureDepart").value = vol.heureDepart;
  document.getElementById("dateArrivee").value = vol.dateArrivee;
  document.getElementById("heureArrivee").value = vol.heureArrivee;
  document.getElementById("prix").value = vol.prix;
  document.getElementById("compagnieId").value = vol.compagnieAerienne?.id || "";
  document.getElementById("avionId").value = vol.avion?.id || "";
  document.getElementById("aeroportDepartId").value = vol.aeroportDepart?.id || "";
  document.getElementById("aeroportArriveeId").value = vol.aeroportArrivee?.id || "";
  showMessage("volMessage", `Mode modification activé pour le vol #${vol.id}`, false);
}

async function loadVols() {
  const tbody = document.getElementById("volsTableBody");
  tbody.innerHTML = "<tr><td colspan='5'>Chargement...</td></tr>";
  try {
    const vols = await fetchJson(`${API_BASE}/afficherTousLesVols`);
    tbody.innerHTML = vols.map(vol => `
      <tr>
        <td>${vol.id}</td>
        <td><strong>${safeText(vol.numeroVol)}</strong><br><small>${safeText(vol.aeroportDepart?.nomAeroport)} → ${safeText(vol.aeroportArrivee?.nomAeroport)}</small></td>
        <td>Départ : ${safeText(vol.dateDepart)} ${safeText(vol.heureDepart)}<br>Arrivée : ${safeText(vol.dateArrivee)} ${safeText(vol.heureArrivee)}</td>
        <td>${safeText(vol.prix)}</td>
        <td>
          <div class="row-actions">
            <button class="btn btn-secondary" onclick='editVol("${serializeForAttr(vol)}")'>Modifier</button>
            <button class="btn btn-danger" onclick='deleteVol(${vol.id})'>Supprimer</button>
          </div>
        </td>
      </tr>`).join("");
  } catch (error) {
    tbody.innerHTML = `<tr><td colspan='5' class='empty-state'>${toErrorMessage(error)}</td></tr>`;
  }
}

async function saveVol() {
  const id = document.getElementById("volId").value;
  const payload = {
    numeroVol: document.getElementById("numeroVol").value.trim(),
    dateDepart: document.getElementById("dateDepart").value.trim(),
    heureDepart: document.getElementById("heureDepart").value.trim(),
    dateArrivee: document.getElementById("dateArrivee").value.trim(),
    heureArrivee: document.getElementById("heureArrivee").value.trim(),
    prix: parseFloat(document.getElementById("prix").value),
    compagnieAerienne: { id: parseInt(document.getElementById("compagnieId").value) },
    avion: { id: parseInt(document.getElementById("avionId").value) },
    aeroportDepart: { id: parseInt(document.getElementById("aeroportDepartId").value) },
    aeroportArrivee: { id: parseInt(document.getElementById("aeroportArriveeId").value) }
  };

  if (!payload.numeroVol || !payload.dateDepart || !payload.heureDepart || !payload.dateArrivee || !payload.heureArrivee || !payload.prix ||
      !payload.compagnieAerienne.id || !payload.avion.id || !payload.aeroportDepart.id || !payload.aeroportArrivee.id) {
    showMessage("volMessage", "Veuillez remplir tous les champs avec des IDs valides.");
    return;
  }

  try {
    await fetchJson(id ? `${API_BASE}/modifierVol/${id}` : `${API_BASE}/ajouterVol`, {
      method: id ? "PUT" : "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });
    showMessage("volMessage", id ? "Vol modifié avec succès." : "Vol ajouté avec succès.", false);
    resetVolForm();
    loadVols();
  } catch (error) {
    showMessage("volMessage", toErrorMessage(error));
  }
}

async function deleteVol(id) {
  if (!confirm(`Supprimer le vol #${id} ?`)) return;
  try {
    await fetchJson(`${API_BASE}/supprimerVol/${id}`, { method: "DELETE" });
    loadVols();
  } catch (error) {
    alert(toErrorMessage(error));
  }
}

loadVols();
