requireAuth();

function resetAdminForm() {
  ["adminId", "nom", "prenom", "email", "motDePasse"].forEach(id => document.getElementById(id).value = "");
  clearMessage("adminMessage");
}

function editAdmin(encoded) {
  const admin = JSON.parse(encoded.replaceAll("&apos;", "'"));
  document.getElementById("adminId").value = admin.id;
  document.getElementById("nom").value = admin.nom;
  document.getElementById("prenom").value = admin.prenom;
  document.getElementById("email").value = admin.email;
  document.getElementById("motDePasse").value = admin.motDePasse;
  showMessage("adminMessage", `Mode modification activé pour l'admin #${admin.id}`, false);
}

async function loadAdmins() {
  const tbody = document.getElementById("adminsTableBody");
  tbody.innerHTML = "<tr><td colspan='4'>Chargement...</td></tr>";
  try {
    const admins = await fetchJson(`${API_BASE}/afficherTousLesAdmins`);
    tbody.innerHTML = admins.map(admin => `
      <tr>
        <td>${admin.id}</td>
        <td>${safeText(admin.nom)} ${safeText(admin.prenom, "")}</td>
        <td>${safeText(admin.email)}</td>
        <td>
          <div class="row-actions">
            <button class="btn btn-secondary" onclick='editAdmin("${serializeForAttr(admin)}")'>Modifier</button>
            <button class="btn btn-danger" onclick='deleteAdmin(${admin.id})'>Supprimer</button>
          </div>
        </td>
      </tr>`).join("");
  } catch (error) {
    tbody.innerHTML = `<tr><td colspan='4' class='empty-state'>${toErrorMessage(error)}</td></tr>`;
  }
}

async function saveAdmin() {
  const id = document.getElementById("adminId").value;
  const payload = {
    nom: document.getElementById("nom").value.trim(),
    prenom: document.getElementById("prenom").value.trim(),
    email: document.getElementById("email").value.trim(),
    motDePasse: document.getElementById("motDePasse").value.trim()
  };

  if (!payload.nom || !payload.prenom || !payload.email || !payload.motDePasse) {
    showMessage("adminMessage", "Veuillez remplir tous les champs.");
    return;
  }

  try {
    await fetchJson(id ? `${API_BASE}/modifierAdmin/${id}` : `${API_BASE}/ajouterAdmin`, {
      method: id ? "PUT" : "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });
    showMessage("adminMessage", id ? "Admin modifié avec succès." : "Admin ajouté avec succès.", false);
    resetAdminForm();
    loadAdmins();
  } catch (error) {
    showMessage("adminMessage", toErrorMessage(error));
  }
}

async function deleteAdmin(id) {
  if (!confirm(`Supprimer l'admin #${id} ?`)) return;
  try {
    await fetchJson(`${API_BASE}/supprimerAdmin/${id}`, { method: "DELETE" });
    loadAdmins();
  } catch (error) {
    alert(toErrorMessage(error));
  }
}

loadAdmins();
