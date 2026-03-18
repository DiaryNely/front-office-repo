const FRONT_API_BASE_URL = "";

export const ReservationsPage = {
  data() {
    return {
      dateDebut: "",
      dateFin: "",
      reservations: [],
      loading: false,
      errorMessage: ""
    };
  },
  mounted() {
    this.loadReservations();
  },
  methods: {
    async loadReservations() {
      this.loading = true;
      this.errorMessage = "";

      try {
        const query = new URLSearchParams();
        if (this.dateDebut) query.append("dateDebut", this.dateDebut);
        if (this.dateFin) query.append("dateFin", this.dateFin);

        const suffix = query.toString() ? `?${query.toString()}` : "";
        const response = await fetch(`${FRONT_API_BASE_URL}/api/reservations${suffix}`, {
          method: "GET",
          headers: { Accept: "application/json" }
        });

        if (!response.ok) {
          throw new Error(`API error ${response.status}`);
        }

        this.reservations = await response.json();
      } catch (error) {
        this.errorMessage = "Impossible de charger les réservations.";
        this.reservations = [];
      } finally {
        this.loading = false;
      }
    }
  },
  template: `
    <main style="font-family: Arial, sans-serif; margin: 0; padding: 24px; background: #f7f8fb; min-height: 100vh;">
      <section style="max-width: 1100px; margin: 0 auto; background: #fff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.08); padding: 20px;">
        <h1 style="margin-top: 0;">Liste des réservations</h1>

        <div style="display: grid; grid-template-columns: repeat(3, minmax(160px, 1fr)); gap: 12px; margin-bottom: 16px;">
          <div>
            <label for="dateDebut" style="display:block;font-size:13px;margin-bottom:4px;color:#555;">Date début</label>
            <input id="dateDebut" type="date" v-model="dateDebut" style="width:100%;box-sizing:border-box;padding:8px;" />
          </div>

          <div>
            <label for="dateFin" style="display:block;font-size:13px;margin-bottom:4px;color:#555;">Date fin</label>
            <input id="dateFin" type="date" v-model="dateFin" style="width:100%;box-sizing:border-box;padding:8px;" />
          </div>

          <div>
            <button @click="loadReservations" style="width:100%;box-sizing:border-box;padding:8px;border:none;background:#1f6feb;color:#fff;border-radius:4px;cursor:pointer;margin-top:20px;">Filtrer</button>
          </div>
        </div>

        <p v-if="loading" style="color:#1f6feb;">Chargement...</p>
        <p v-if="errorMessage" style="color:#b00020;">{{ errorMessage }}</p>

        <table v-if="reservations.length > 0" style="width:100%;border-collapse:collapse;margin-top:10px;">
          <thead>
            <tr>
              <th style="border:1px solid #e5e7eb;padding:8px;text-align:left;background:#f2f4f8;">ID</th>
              <th style="border:1px solid #e5e7eb;padding:8px;text-align:left;background:#f2f4f8;">Client</th>
              <th style="border:1px solid #e5e7eb;padding:8px;text-align:left;background:#f2f4f8;">Passagers</th>
              <th style="border:1px solid #e5e7eb;padding:8px;text-align:left;background:#f2f4f8;">Date/Heure arrivée</th>
              <th style="border:1px solid #e5e7eb;padding:8px;text-align:left;background:#f2f4f8;">Hôtel</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="reservation in reservations" :key="reservation.id">
              <td style="border:1px solid #e5e7eb;padding:8px;">{{ reservation.id }}</td>
              <td style="border:1px solid #e5e7eb;padding:8px;">{{ reservation.clientId }}</td>
              <td style="border:1px solid #e5e7eb;padding:8px;">{{ reservation.nombrePassager }}</td>
              <td style="border:1px solid #e5e7eb;padding:8px;">{{ reservation.dateHeureArrivee }}</td>
              <td style="border:1px solid #e5e7eb;padding:8px;">{{ reservation.idHotel }}</td>
            </tr>
          </tbody>
        </table>

        <p v-else-if="!loading">Aucune réservation trouvée.</p>
      </section>
    </main>
  `
};
