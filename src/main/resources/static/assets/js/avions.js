requireAuth();

function resetAvionForm() {
  ["avionId", "modele", "capacite", "immatriculation", "compagnieId"].forEach(id => document.getElementById(id).value = "");
  clearMessage("avionMessage");
}

function editAvion(encoded) {
  const avion = JSON.parse(encoded.replaceAll("&apos;", "'"));
  document.getElementById("avionId").value = avion.id;
  document.getElementById("modele").value = avion.modele;
  document.getElementById("capacite").value = avion.capacite;
  document.getElementById("immatriculation").value = avion.immatriculation;
  document.getElementById("compagnieId").value = avion.compagnieAerienne?.id || "";
  showMessage("avionMessage", `Mode modification activé pour l'avion #${avion.id}`, false);
}

async function loadAvions() {
  const tbody = document.getElementById("avionsTableBody");
  tbody.innerHTML = "<tr><td colspan='6'>Chargement...</td></tr>";
  try {
    const avions = await fetchJson(`${API_BASE}/afficherTousLesAvions`);
    tbody.innerHTML = avions.map(a => `
      <tr>
        <td>${a.id}</td>
        <td>${safeText(a.modele)}</td>
        <td>${safeText(a.capacite)}</td>
        <td>${safeText(a.immatriculation)}</td>
        <td>${safeText(a.compagnieAerienne?.nomCompagnie)}</td>
        <td>
          <div class="row-actions">
            <button class="btn btn-secondary" onclick='editAvion("${serializeForAttr(a)}")'>Modifier</button>
            <button class="btn btn-danger" onclick='deleteAvion(${a.id})'>Supprimer</button>
          </div>
        </td>
      </tr>`).join("");
  } catch (error) {
    tbody.innerHTML = `<tr><td colspan='6' class='empty-state'>${toErrorMessage(error)}</td></tr>`;
  }
}

async function saveAvion() {
  const id = document.getElementById("avionId").value;
  const payload = {
    modele: document.getElementById("modele").value.trim(),
    capacite: parseInt(document.getElementById("capacite").value),
    immatriculation: document.getElementById("immatriculation").value.trim(),
    compagnieAerienne: { id: parseInt(document.getElementById("compagnieId").value) }
  };

  if (!payload.modele || !payload.capacite || !payload.immatriculation || !payload.compagnieAerienne.id) {
    showMessage("avionMessage", "Veuillez remplir tous les champs et fournir un ID compagnie valide.");
    return;
  }

  try {
    await fetchJson(id ? `${API_BASE}/modifierAvion/${id}` : `${API_BASE}/ajouterAvion`, {
      method: id ? "PUT" : "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });
    showMessage("avionMessage", id ? "Avion modifié avec succès." : "Avion ajouté avec succès.", false);
    resetAvionForm();
    loadAvions();
  } catch (error) {
    showMessage("avionMessage", toErrorMessage(error));
  }
}

async function deleteAvion(id) {
  if (!confirm(`Supprimer l'avion #${id} ?`)) return;
  try {
    await fetchJson(`${API_BASE}/supprimerAvion/${id}`, { method: "DELETE" });
    loadAvions();
  } catch (error) {
    alert(toErrorMessage(error));
  }
}

loadAvions();
