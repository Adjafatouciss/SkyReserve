requireAuth();

function resetAeroportForm() {
  ["aeroportId", "nomAeroport", "ville", "pays", "codeAeroport"].forEach(id => document.getElementById(id).value = "");
  clearMessage("aeroportMessage");
}

function editAeroport(encoded) {
  const aeroport = JSON.parse(encoded.replaceAll("&apos;", "'"));
  document.getElementById("aeroportId").value = aeroport.id;
  document.getElementById("nomAeroport").value = aeroport.nomAeroport;
  document.getElementById("ville").value = aeroport.ville;
  document.getElementById("pays").value = aeroport.pays;
  document.getElementById("codeAeroport").value = aeroport.codeAeroport;
  showMessage("aeroportMessage", `Mode modification activé pour l'aéroport #${aeroport.id}`, false);
}

async function loadAeroports() {
  const tbody = document.getElementById("aeroportsTableBody");
  tbody.innerHTML = "<tr><td colspan='6'>Chargement...</td></tr>";
  try {
    const aeroports = await fetchJson(`${API_BASE}/afficherTousLesAeroports`);
    tbody.innerHTML = aeroports.map(a => `
      <tr>
        <td>${a.id}</td>
        <td>${safeText(a.nomAeroport)}</td>
        <td>${safeText(a.ville)}</td>
        <td>${safeText(a.pays)}</td>
        <td>${safeText(a.codeAeroport)}</td>
        <td>
          <div class="row-actions">
            <button class="btn btn-secondary" onclick='editAeroport("${serializeForAttr(a)}")'>Modifier</button>
            <button class="btn btn-danger" onclick='deleteAeroport(${a.id})'>Supprimer</button>
          </div>
        </td>
      </tr>`).join("");
  } catch (error) {
    tbody.innerHTML = `<tr><td colspan='6' class='empty-state'>${toErrorMessage(error)}</td></tr>`;
  }
}

async function saveAeroport() {
  const id = document.getElementById("aeroportId").value;
  const payload = {
    nomAeroport: document.getElementById("nomAeroport").value.trim(),
    ville: document.getElementById("ville").value.trim(),
    pays: document.getElementById("pays").value.trim(),
    codeAeroport: document.getElementById("codeAeroport").value.trim()
  };

  if (!payload.nomAeroport || !payload.ville || !payload.pays || !payload.codeAeroport) {
    showMessage("aeroportMessage", "Veuillez remplir tous les champs.");
    return;
  }

  try {
    await fetchJson(id ? `${API_BASE}/modifierAeroport/${id}` : `${API_BASE}/ajouterAeroport`, {
      method: id ? "PUT" : "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });
    showMessage("aeroportMessage", id ? "Aéroport modifié avec succès." : "Aéroport ajouté avec succès.", false);
    resetAeroportForm();
    loadAeroports();
  } catch (error) {
    showMessage("aeroportMessage", toErrorMessage(error));
  }
}

async function deleteAeroport(id) {
  if (!confirm(`Supprimer l'aéroport #${id} ?`)) return;
  try {
    await fetchJson(`${API_BASE}/supprimerAeroport/${id}`, { method: "DELETE" });
    loadAeroports();
  } catch (error) {
    alert(toErrorMessage(error));
  }
}

loadAeroports();
