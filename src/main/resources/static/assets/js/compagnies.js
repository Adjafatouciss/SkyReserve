requireAuth();

function resetCompagnieForm() {
  ["compagnieId", "nomCompagnie", "pays", "codeCompagnie"].forEach(id => document.getElementById(id).value = "");
  clearMessage("compagnieMessage");
}

function editCompagnie(encoded) {
  const compagnie = JSON.parse(encoded.replaceAll("&apos;", "'"));
  document.getElementById("compagnieId").value = compagnie.id;
  document.getElementById("nomCompagnie").value = compagnie.nomCompagnie;
  document.getElementById("pays").value = compagnie.pays;
  document.getElementById("codeCompagnie").value = compagnie.codeCompagnie;
  showMessage("compagnieMessage", `Mode modification activé pour la compagnie #${compagnie.id}`, false);
}

async function loadCompagnies() {
  const tbody = document.getElementById("compagniesTableBody");
  tbody.innerHTML = "<tr><td colspan='5'>Chargement...</td></tr>";
  try {
    const compagnies = await fetchJson(`${API_BASE}/afficherToutesLesCompagnies`);
    tbody.innerHTML = compagnies.map(c => `
      <tr>
        <td>${c.id}</td>
        <td>${safeText(c.nomCompagnie)}</td>
        <td>${safeText(c.pays)}</td>
        <td>${safeText(c.codeCompagnie)}</td>
        <td>
          <div class="row-actions">
            <button class="btn btn-secondary" onclick='editCompagnie("${serializeForAttr(c)}")'>Modifier</button>
            <button class="btn btn-danger" onclick='deleteCompagnie(${c.id})'>Supprimer</button>
          </div>
        </td>
      </tr>`).join("");
  } catch (error) {
    tbody.innerHTML = `<tr><td colspan='5' class='empty-state'>${toErrorMessage(error)}</td></tr>`;
  }
}

async function saveCompagnie() {
  const id = document.getElementById("compagnieId").value;
  const payload = {
    nomCompagnie: document.getElementById("nomCompagnie").value.trim(),
    pays: document.getElementById("pays").value.trim(),
    codeCompagnie: document.getElementById("codeCompagnie").value.trim()
  };

  if (!payload.nomCompagnie || !payload.pays || !payload.codeCompagnie) {
    showMessage("compagnieMessage", "Veuillez remplir tous les champs.");
    return;
  }

  try {
    await fetchJson(id ? `${API_BASE}/modifierCompagnie/${id}` : `${API_BASE}/ajouterCompagnie`, {
      method: id ? "PUT" : "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });
    showMessage("compagnieMessage", id ? "Compagnie modifiée avec succès." : "Compagnie ajoutée avec succès.", false);
    resetCompagnieForm();
    loadCompagnies();
  } catch (error) {
    showMessage("compagnieMessage", toErrorMessage(error));
  }
}

async function deleteCompagnie(id) {
  if (!confirm(`Supprimer la compagnie #${id} ?`)) return;
  try {
    await fetchJson(`${API_BASE}/supprimerCompagnie/${id}`, { method: "DELETE" });
    loadCompagnies();
  } catch (error) {
    alert(toErrorMessage(error));
  }
}

loadCompagnies();
