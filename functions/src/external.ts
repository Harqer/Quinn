import { onCall, HttpsError } from "firebase-functions/v2/https";
import { defineSecret } from "firebase-functions/params";

const RAPID_API_KEY = defineSecret("RAPID_API_KEY");

export const searchConcerts = onCall(
  {
    secrets: [RAPID_API_KEY],
    enforceAppCheck: true,
    cors: true,
  },
  async (request) => {
    if (!request.auth) {
      throw new HttpsError("unauthenticated", "The function must be called while authenticated.");
    }

    const { q, lat, lon, range, page, perPage } = request.data;
    const rapidApiKey = RAPID_API_KEY.value();

    try {
      const queryParams = new URLSearchParams();
      if (q) queryParams.append("q", q);
      if (lat !== undefined) queryParams.append("lat", lat.toString());
      if (lon !== undefined) queryParams.append("lon", lon.toString());
      if (range) queryParams.append("range", range);
      if (page !== undefined) queryParams.append("page", page.toString());
      if (perPage !== undefined) queryParams.append("perPage", perPage.toString());

      const url = `https://seatgeek-com-scraper.p.rapidapi.com/events/search?${queryParams.toString()}`;
      
      const response = await fetch(url, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "x-rapidapi-host": "seatgeek-com-scraper.p.rapidapi.com",
          "x-rapidapi-key": rapidApiKey
        }
      });

      if (!response.ok) {
        throw new Error(`RapidAPI Error: ${response.status} ${response.statusText}`);
      }

      const data = await response.json();
      return data;
    } catch (err: any) {
      throw new HttpsError("internal", `Failed to search concerts: ${err.message || err}`);
    }
  }
);
