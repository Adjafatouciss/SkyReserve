async function loginUtilisateur() {
  const email = document.getElementById("email").value.trim();
  const motDePasse = document.getElementById("password").value.trim();
  const msg = document.getElementById("loginMessage");

  msg.textContent = "";

  if (!email || !motDePasse) {
    msg.textContent = "Veuillez remplir tous les champs.";
    return;
  }

  try {
    const user = await fetchJson(`${API_BASE}/loginUtilisateur`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, motDePasse })
    });

    setUserSession(user);
    window.location.href = "dashboardUtilisateur.html";
  } catch (error) {
    msg.textContent = error.message || "Erreur de connexion.";
  }
}
