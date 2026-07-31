import { describe, it, expect, vi } from "vitest";
import { TIER_LIMITS, getMonthKey } from "../middlewares/quota.js";

describe("Quota Middleware & Tier Limits", () => {
  it("should have correct limits for all subscription tiers", () => {
    expect(TIER_LIMITS.free).toEqual({ songs: 5, podcasts: 2, realtimeMinutes: 10 });
    expect(TIER_LIMITS.premium_basic).toEqual({ songs: 30, podcasts: 10, realtimeMinutes: 60 });
    expect(TIER_LIMITS.premium_pro).toEqual({ songs: 100, podcasts: 30, realtimeMinutes: 150 });
    expect(TIER_LIMITS.premium_ultra).toEqual({ songs: Infinity, podcasts: Infinity, realtimeMinutes: Infinity });
  });

  it("should generate a valid YYYY-MM month key", () => {
    const monthKey = getMonthKey();
    expect(monthKey).toMatch(/^\d{4}-\d{2}$/);
  });
});
