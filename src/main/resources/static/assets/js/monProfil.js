if (!requireUserAuth()) {}

const profileUser = getUserSession();

function renderProfileSidebar() {
  const zone = document.getElementById("sidebarContainer");
  if (zone) zone.innerHTML = createSidebar("profil");
}

async function chargerProfil() {
  renderProfileSidebar();

  try {
    const user = await fetchJson(`${API_BASE}/utilisateur/${profileUser.id}`);
    setUserSession(user);

    document.getElementById('nom').value = user.nom || '';
    document.getElementById('prenom').value = user.prenom || '';
    document.getElementById('email').value = user.email || '';
    document.getElementById('telephone').value = user.telephone || '';
    document.getElementById('profilNomComplet').textContent = getUserFullName(user);
    document.getElementById('profilEmail').textContent = user.email || '';
  } catch (error) {
    showMessage('profilMessage', toErrorMessage(error));
  }
}

async function modifierProfil() {
  const msg = document.getElementById('profilMessage');

  try {
    const actuel = await fetchJson(`${API_BASE}/utilisateur/${profileUser.id}`);
    const nouveauMotDePasse = document.getElementById('motDePasse').value.trim();

    const user = await fetchJson(`${API_BASE}/modifierUtilisateur/${profileUser.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        nom: document.getElementById('nom').value.trim(),
        prenom: document.getElementById('prenom').value.trim(),
        email: document.getElementById('email').value.trim(),
        telephone: document.getElementById('telephone').value.trim(),
        motDePasse: nouveauMotDePasse || actuel.motDePasse
      })
    });

    setUserSession(user);
    document.getElementById('motDePasse').value = '';
    document.getElementById('profilNomComplet').textContent = getUserFullName(user);
    document.getElementById('profilEmail').textContent = user.email || '';
    showMessage('profilMessage', 'Profil modifié avec succès.', false);
  } catch (error) {
    showMessage('profilMessage', toErrorMessage(error));
  }
}

chargerProfil();
