requireAuth();

function resetReservationForm() {
  ["reservationId","dateReservation","statutReservation","utilisateurId","volId"].forEach(id => document.getElementById(id).value = "");
  clearMessage("reservationMessage");
}

function editReservation(encoded) {
  const reservation = JSON.parse(encoded.replaceAll("&apos;", "'"));
  document.getElementById("reservationId").value = reservation.id;
  document.getElementById("dateReservation").value = reservation.dateReservation;
  document.getElementById("statutReservation").value = reservation.statutReservation;
  document.getElementById("utilisateurId").value = reservation.utilisateur?.id || "";
  document.getElementById("volId").value = reservation.vol?.id || "";
  showMessage("reservationMessage", `Mode modification activé pour la réservation #${reservation.id}`, false);
}

async function loadReservations() {
  const tbody = document.getElementById("reservationsTableBody");
  tbody.innerHTML = "<tr><td colspan='6'>Chargement...</td></tr>";
  try {
    const reservations = await fetchJson(`${API_BASE}/afficherToutesLesReservations`);
    tbody.innerHTML = reservations.map(r => `
      <tr>
        <td>${r.id}</td>
        <td>${safeText(r.dateReservation)}</td>
        <td><span class="badge info">${safeText(r.statutReservation)}</span></td>
        <td>${safeText(r.utilisateur?.nom)} ${safeText(r.utilisateur?.prenom, "")}</td>
        <td>${safeText(r.vol?.numeroVol)}</td>
        <td>
          <div class="row-actions">
            <button class="btn btn-secondary" onclick='editReservation("${serializeForAttr(r)}")'>Modifier</button>
            <button class="btn btn-danger" onclick='deleteReservation(${r.id})'>Supprimer</button>
          </div>
        </td>
      </tr>`).join("");
  } catch (error) {
    tbody.innerHTML = `<tr><td colspan='6' class='empty-state'>${toErrorMessage(error)}</td></tr>`;
  }
}

async function saveReservation() {
  const id = document.getElementById("reservationId").value;
  const payload = {
    dateReservation: document.getElementById("dateReservation").value.trim(),
    statutReservation: document.getElementById("statutReservation").value.trim(),
    utilisateur: { id: parseInt(document.getElementById("utilisateurId").value) },
    vol: { id: parseInt(document.getElementById("volId").value) }
  };

  if (!payload.dateReservation || !payload.statutReservation || !payload.utilisateur.id || !payload.vol.id) {
    showMessage("reservationMessage", "Veuillez remplir tous les champs avec des IDs valides.");
    return;
  }

  try {
    await fetchJson(id ? `${API_BASE}/modifierReservation/${id}` : `${API_BASE}/ajouterReservation`, {
      method: id ? "PUT" : "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });
    showMessage("reservationMessage", id ? "Réservation modifiée avec succès." : "Réservation ajoutée avec succès.", false);
    resetReservationForm();
    loadReservations();
  } catch (error) {
    showMessage("reservationMessage", toErrorMessage(error));
  }
}

async function deleteReservation(id) {
  if (!confirm(`Supprimer la réservation #${id} ?`)) return;
  try {
    await fetchJson(`${API_BASE}/supprimerReservation/${id}`, { method: "DELETE" });
    loadReservations();
  } catch (error) {
    alert(toErrorMessage(error));
  }
}

loadReservations();
