requireAuth();

function resetUserForm() {
  ["userId", "nom", "prenom", "email", "motDePasse", "telephone"].forEach(id => document.getElementById(id).value = "");
  clearMessage("userMessage");
}

function editUser(encoded) {
  const user = JSON.parse(encoded.replaceAll("&apos;", "'"));
  document.getElementById("userId").value = user.id;
  document.getElementById("nom").value = user.nom;
  document.getElementById("prenom").value = user.prenom;
  document.getElementById("email").value = user.email;
  document.getElementById("motDePasse").value = user.motDePasse;
  document.getElementById("telephone").value = user.telephone || "";
  showMessage("userMessage", `Mode modification activé pour l'utilisateur #${user.id}`, false);
}

async function loadUsers() {
  const tbody = document.getElementById("usersTableBody");
  tbody.innerHTML = "<tr><td colspan='5'>Chargement...</td></tr>";
  try {
    const users = await fetchJson(`${API_BASE}/afficherTousLesUtilisateurs`);
    tbody.innerHTML = users.map(user => `
      <tr>
        <td>${user.id}</td>
        <td>${safeText(user.nom)} ${safeText(user.prenom, "")}</td>
        <td>${safeText(user.email)}</td>
        <td>${safeText(user.telephone)}</td>
        <td>
          <div class="row-actions">
            <button class="btn btn-secondary" onclick='editUser("${serializeForAttr(user)}")'>Modifier</button>
            <button class="btn btn-danger" onclick='deleteUser(${user.id})'>Supprimer</button>
          </div>
        </td>
      </tr>`).join("");
  } catch (error) {
    tbody.innerHTML = `<tr><td colspan='5' class='empty-state'>${toErrorMessage(error)}</td></tr>`;
  }
}

async function saveUser() {
  const id = document.getElementById("userId").value;
  const payload = {
    nom: document.getElementById("nom").value.trim(),
    prenom: document.getElementById("prenom").value.trim(),
    email: document.getElementById("email").value.trim(),
    motDePasse: document.getElementById("motDePasse").value.trim(),
    telephone: document.getElementById("telephone").value.trim()
  };

  if (!payload.nom || !payload.prenom || !payload.email || !payload.motDePasse) {
    showMessage("userMessage", "Veuillez remplir les champs obligatoires.");
    return;
  }

  try {
    await fetchJson(id ? `${API_BASE}/modifierUtilisateur/${id}` : `${API_BASE}/ajouterUtilisateur`, {
      method: id ? "PUT" : "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });
    showMessage("userMessage", id ? "Utilisateur modifié avec succès." : "Utilisateur ajouté avec succès.", false);
    resetUserForm();
    loadUsers();
  } catch (error) {
    showMessage("userMessage", toErrorMessage(error));
  }
}

async function deleteUser(id) {
  if (!confirm(`Supprimer l'utilisateur #${id} ?`)) return;
  try {
    await fetchJson(`${API_BASE}/supprimerUtilisateur/${id}`, { method: "DELETE" });
    loadUsers();
  } catch (error) {
    alert(toErrorMessage(error));
  }
}

loadUsers();
