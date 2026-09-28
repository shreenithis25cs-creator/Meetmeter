/**
 * API Client connecting React frontend to Spring Boot 3 Backend
 * Backend base URL: http://localhost:8080
 */

const API_BASE_URL = 'http://localhost:8080';

// Current active settings state (persisted in memory / localStorage)
let currentSettings = {
  costPerPersonPerHour: 500.0,
  lowScoreThreshold: 40,
  highScoreThreshold: 70
};

// Base meeting definitions
const RAW_MEETINGS = [
  {
    id: 1,
    googleEventId: "g_event_1",
    title: "Weekly company status broadcast",
    startTime: "2026-09-22T10:00:00",
    endTime: "2026-09-22T10:45:00",
    dayTime: "Monday, 10:00 am",
    attendeesCount: 28,
    durationMinutes: 45,
    agendaText: "General status updates across all units.",
    isRecurring: true,
    scoreValue: 22,
    suggestionText: "Convert to a 3-minute asynchronous email memo or Loom summary.",
    suggestionSource: "RULE_BASED"
  },
  {
    id: 2,
    googleEventId: "g_event_2",
    title: "Design system architectural review",
    startTime: "2026-09-23T14:30:00",
    endTime: "2026-09-23T15:00:00",
    dayTime: "Tuesday, 2:30 pm",
    attendeesCount: 4,
    durationMinutes: 30,
    agendaText: "Detailed technical breakdown of tokens and Tailwind hierarchy.",
    isRecurring: false,
    scoreValue: 88,
    suggestionText: "High collaborative alignment with key technical decisions made.",
    suggestionSource: "AI"
  },
  {
    id: 3,
    googleEventId: "g_event_3",
    title: "Marketing asset pipeline check-in",
    startTime: "2026-09-24T11:00:00",
    endTime: "2026-09-24T12:00:00",
    dayTime: "Wednesday, 11:00 am",
    attendeesCount: 11,
    durationMinutes: 60,
    agendaText: "Quick sync.",
    isRecurring: true,
    scoreValue: 34,
    suggestionText: "Replace with a shared Figma comment thread and automated status digest.",
    suggestionSource: "RULE_BASED"
  },
  {
    id: 4,
    googleEventId: "g_event_4",
    title: "Quarterly product roadmap grooming",
    startTime: "2026-09-25T13:00:00",
    endTime: "2026-09-25T13:45:00",
    dayTime: "Thursday, 1:00 pm",
    attendeesCount: 8,
    durationMinutes: 45,
    agendaText: "Feature prioritizing, scope evaluation, backlog grooming.",
    isRecurring: true,
    scoreValue: 58,
    suggestionText: "Trim non-essential attendees from 8 to 4 contributors to boost focus.",
    suggestionSource: "AI"
  },
  {
    id: 5,
    googleEventId: "g_event_5",
    title: "Infrastructure incident post-mortem",
    startTime: "2026-09-25T16:00:00",
    endTime: "2026-09-25T16:50:00",
    dayTime: "Thursday, 4:00 pm",
    attendeesCount: 5,
    durationMinutes: 50,
    agendaText: "Comprehensive root cause analysis of database replication lag.",
    isRecurring: false,
    scoreValue: 92,
    suggestionText: "Essential retrospective solving root cause and active mitigation.",
    suggestionSource: "AI"
  },
  {
    id: 6,
    googleEventId: "g_event_6",
    title: "Tool vendor contract evaluation",
    startTime: "2026-09-26T09:30:00",
    endTime: "2026-09-26T10:10:00",
    dayTime: "Friday, 9:30 am",
    attendeesCount: 9,
    durationMinutes: 40,
    agendaText: "Review SaaS tool pricing sheet and negotiate renewal terms.",
    isRecurring: false,
    scoreValue: 48,
    suggestionText: "Shorten duration to 15 minutes and circulate pricing spreadsheet beforehand.",
    suggestionSource: "RULE_BASED"
  }
];

function buildDynamicDashboard(rate) {
  let totalCost = 0;
  let flaggedCost = 0;
  let hoursSaved = 0;
  let couldBeEmailCount = 0;

  const meetings = RAW_MEETINGS.map(m => {
    const personHours = (m.attendeesCount * m.durationMinutes) / 60.0;
    const cost = personHours * rate;
    totalCost += cost;

    let status = "NECESSARY";
    if (m.scoreValue < currentSettings.lowScoreThreshold) {
      status = "UNNECESSARY";
      flaggedCost += cost;
      couldBeEmailCount++;
      hoursSaved += (m.durationMinutes / 60.0);
    } else if (m.scoreValue < currentSettings.highScoreThreshold) {
      status = "BORDERLINE";
      hoursSaved += (m.durationMinutes / 60.0) * 0.4;
    }

    return {
      ...m,
      cost: Math.round(cost),
      score: {
        id: 100 + m.id,
        necessityScore: m.scoreValue,
        estimatedCost: Math.round(cost),
        status: status,
        suggestion: {
          id: 200 + m.id,
          suggestionText: m.suggestionText,
          source: m.suggestionSource
        }
      }
    };
  });

  return {
    totalCost: Math.round(totalCost),
    flaggedMeetingsCost: Math.round(flaggedCost),
    hoursCouldBeSaved: Math.round(hoursSaved * 10) / 10,
    totalMeetingsCount: meetings.length,
    couldBeEmailCount: couldBeEmailCount,
    dateRange: "Sep 22, 2026 – Sep 28, 2026",
    settings: { ...currentSettings, costPerPersonPerHour: rate },
    meetings: meetings
  };
}

export const api = {
  /**
   * Fetches full dashboard data from Spring Boot GET /api/dashboard with dynamic fallback recalculation
   */
  async fetchDashboard() {
    try {
      const response = await fetch(`${API_BASE_URL}/api/dashboard`, {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' }
      });
      if (response.ok) {
        const data = await response.json();
        return { data, isLiveBackend: true };
      }
    } catch (e) {
      console.warn('Backend server offline. Dynamically calculating costs locally at $' + currentSettings.costPerPersonPerHour + '/hr.');
    }
    return { data: buildDynamicDashboard(currentSettings.costPerPersonPerHour), isLiveBackend: false };
  },

  /**
   * Triggers Google Calendar Sync: GET /api/meetings/sync
   */
  async syncMeetings() {
    try {
      const response = await fetch(`${API_BASE_URL}/api/meetings/sync`, {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' }
      });
      if (response.ok) {
        return await response.json();
      }
    } catch (e) {
      console.warn('Backend sync error:', e.message);
    }
    return { status: 'mock_success', message: 'Synced 6 events from Google Calendar.' };
  },

  /**
   * Recalculates meeting score with Claude AI & rules: POST /api/meetings/{id}/score
   */
  async scoreMeeting(meetingId) {
    try {
      const response = await fetch(`${API_BASE_URL}/api/meetings/${meetingId}/score`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' }
      });
      if (response.ok) {
        return await response.json();
      }
    } catch (e) {
      console.warn('Backend score API error:', e.message);
    }
    return null;
  },

  /**
   * Gets user settings: GET /api/settings
   */
  async getSettings() {
    try {
      const response = await fetch(`${API_BASE_URL}/api/settings`);
      if (response.ok) {
        const data = await response.json();
        currentSettings = { ...currentSettings, ...data };
        return data;
      }
    } catch (e) {
      console.warn('Backend getSettings error:', e.message);
    }
    return currentSettings;
  },

  /**
   * Updates user settings: PUT /api/settings
   */
  async updateSettings(settings) {
    currentSettings = {
      ...currentSettings,
      costPerPersonPerHour: Number(settings.costPerPersonPerHour) || currentSettings.costPerPersonPerHour
    };

    try {
      const response = await fetch(`${API_BASE_URL}/api/settings`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(currentSettings)
      });
      if (response.ok) {
        const data = await response.json();
        currentSettings = { ...currentSettings, ...data };
        return data;
      }
    } catch (e) {
      console.warn('Backend updateSettings offline. Updated local rate to $' + currentSettings.costPerPersonPerHour + '/hr.');
    }
    return currentSettings;
  },

  /**
   * URL for Google OAuth Login redirect
   */
  getGoogleLoginUrl() {
    return `${API_BASE_URL}/auth/google/login`;
  }
};
