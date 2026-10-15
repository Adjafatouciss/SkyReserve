async function loginAdmin() {
  clearMessage("loginMessage");
  const email = document.getElementById("email").value.trim();
  const motDePasse = document.getElementById("password").value.trim();

  if (!email || !motDePasse) {
    showMessage("loginMessage", "Veuillez remplir tous les champs.");
    return;
  }

  try {
    const admin = await fetchJson(`${API_BASE}/loginAdmin`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, motDePasse })
    });
    setAdminSession(admin);
    window.location.href = "dashboard.html";
  } catch (error) {
    showMessage("loginMessage", toErrorMessage(error));
  }
}