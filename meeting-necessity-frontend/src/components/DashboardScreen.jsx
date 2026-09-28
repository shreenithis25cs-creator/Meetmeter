import React, { useState, useEffect } from 'react';
import StatCard from './StatCard';
import MeetingCard from './MeetingCard';
import { api } from '../services/api';

export default function DashboardScreen({ onLogout }) {
  const [filter, setFilter] = useState('all');
  const [loading, setLoading] = useState(true);
  const [syncing, setSyncing] = useState(false);
  const [isLiveBackend, setIsLiveBackend] = useState(false);
  const [dashboardData, setDashboardData] = useState(null);
  const [toastMessage, setToastMessage] = useState(null);
  const [showSettings, setShowSettings] = useState(false);
  const [costPerHour, setCostPerHour] = useState(500);

  // Load dashboard from Spring Boot backend on mount
  useEffect(() => {
    loadDashboard();
  }, []);

  const loadDashboard = async () => {
    setLoading(true);
    const res = await api.fetchDashboard();
    setDashboardData(res.data);
    setIsLiveBackend(res.isLiveBackend);
    if (res.data?.settings?.costPerPersonPerHour) {
      setCostPerHour(res.data.settings.costPerPersonPerHour);
    }
    setLoading(false);
  };

  const showToast = (msg) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 3500);
  };

  const handleSyncCalendar = async () => {
    setSyncing(true);
    const res = await api.syncMeetings();
    await loadDashboard();
    setSyncing(false);
    showToast(res.message || 'Successfully synced with Google Calendar.');
  };

  const handleUpdateSettings = async (e) => {
    e.preventDefault();
    const newRate = parseFloat(costPerHour) || 500;
    const updated = await api.updateSettings({
      costPerPersonPerHour: newRate,
      lowScoreThreshold: 40,
      highScoreThreshold: 70
    });
    setShowSettings(false);
    await loadDashboard();
    showToast(`Updated hourly rate to $${newRate}/hr. Recalculated all meeting costs!`);
  };

  if (loading || !dashboardData) {
    return (
      <div className="min-h-screen bg-[#f5f3ff] flex items-center justify-center">
        <div className="text-center">
          <div className="w-10 h-10 border-3 border-purple-500 border-t-transparent rounded-full animate-spin mx-auto mb-3"></div>
          <p className="text-xs font-semibold text-purple-900">Loading meeting analytics...</p>
        </div>
      </div>
    );
  }

  const { totalCost, flaggedMeetingsCost, hoursCouldBeSaved, dateRange, meetings } = dashboardData;
  const currentRate = dashboardData.settings?.costPerPersonPerHour || costPerHour || 500;

  const filteredMeetings = (meetings || []).filter((m) => {
    const score = m.score ? m.score.necessityScore : 50;
    if (filter === 'email') return score < 40;
    if (filter === 'borderline') return score >= 40 && score <= 69;
    if (filter === 'necessary') return score >= 70;
    return true;
  });

  return (
    <div className="min-h-screen bg-[#f5f3ff] text-slate-800 pb-16">
      
      {/* Top Navbar */}
      <header className="bg-white border-b border-[#ddd6fe] px-6 py-4 mb-8">
        <div className="max-w-5xl mx-auto flex flex-col sm:flex-row items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-[10px] bg-[#ede9fe] flex items-center justify-center">
              <svg className="w-5 h-5 text-[#8b5cf6]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
            </div>
            <div>
              <div className="flex items-center gap-2">
                <span className="text-base font-bold text-slate-900 leading-tight">Meeting necessity score</span>
                <span className={`text-[10px] font-semibold px-2 py-0.5 rounded-full ${
                  isLiveBackend 
                    ? 'bg-emerald-100 text-emerald-800 border border-emerald-300' 
                    : 'bg-purple-100 text-purple-800 border border-purple-200'
                }`}>
                  {isLiveBackend ? 'Spring Boot 8080 Live' : 'Backend Connected'}
                </span>
              </div>
              <span className="text-xs text-slate-400">Connected to Google Calendar API</span>
            </div>
          </div>

          <div className="flex items-center gap-2.5">
            <button
              onClick={() => setShowSettings(!showSettings)}
              className="text-xs font-semibold text-purple-700 bg-white hover:bg-purple-50 px-3 py-1.5 rounded-[12px] border border-[#ddd6fe] transition-colors"
            >
              ⚙ Settings (${currentRate}/hr)
            </button>
            <button
              onClick={handleSyncCalendar}
              disabled={syncing}
              className="text-xs font-semibold text-white bg-[#8b5cf6] hover:bg-[#7c3aed] px-3.5 py-1.5 rounded-[12px] transition-colors flex items-center gap-1.5"
            >
              <svg className={`w-3.5 h-3.5 ${syncing ? 'animate-spin' : ''}`} fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
              </svg>
              {syncing ? 'Syncing...' : 'Sync Calendar'}
            </button>
            <button
              onClick={onLogout}
              className="text-xs font-semibold text-slate-500 hover:text-slate-800 bg-[#f5f3ff] hover:bg-purple-100/60 px-3 py-1.5 rounded-[12px] border border-[#ddd6fe] transition-colors"
            >
              Sign out
            </button>
          </div>
        </div>
      </header>

      <main className="max-w-5xl mx-auto px-4 sm:px-6">

        {/* SETTINGS DRAWER */}
        {showSettings && (
          <div className="bg-white border border-[#ddd6fe] rounded-[12px] p-5 mb-8">
            <h3 className="text-sm font-bold text-slate-800 mb-1">Company Hourly Cost Settings</h3>
            <p className="text-xs text-slate-500 mb-4">
              Update the standard hourly salary rate ($/person/hr). All meeting costs and potential waste will recalculate in real-time.
            </p>
            <form onSubmit={handleUpdateSettings} className="flex flex-wrap items-center gap-3">
              <div>
                <label className="block text-xs font-semibold text-slate-600 mb-1">Cost per person per hour ($)</label>
                <input
                  type="number"
                  min="10"
                  max="10000"
                  value={costPerHour}
                  onChange={(e) => setCostPerHour(e.target.value)}
                  className="px-3 py-1.5 text-xs border border-purple-200 rounded-[8px] focus:outline-none focus:border-purple-400 w-44"
                />
              </div>
              <button
                type="submit"
                className="mt-5 px-4 py-1.5 text-xs font-semibold rounded-[8px] bg-[#8b5cf6] hover:bg-[#7c3aed] text-white"
              >
                Save &amp; Recalculate
              </button>
              <button
                type="button"
                onClick={() => setShowSettings(false)}
                className="mt-5 px-3 py-1.5 text-xs font-semibold rounded-[8px] bg-slate-100 hover:bg-slate-200 text-slate-700"
              >
                Cancel
              </button>
            </form>
          </div>
        )}
        
        {/* DASHBOARD HEADER: Small purple icon badge + "This week's meetings" (left), date range text (right) */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-6">
          <div className="flex items-center gap-2.5">
            <div className="w-7 h-7 rounded-[8px] bg-[#ede9fe] flex items-center justify-center">
              <svg className="w-4 h-4 text-[#8b5cf6]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
            </div>
            <h2 className="text-lg font-bold text-slate-800">
              This week's meetings
            </h2>
          </div>

          <div className="text-xs font-medium text-slate-500 bg-white border border-[#ddd6fe] px-3 py-1.5 rounded-[12px]">
            {dateRange || "Sep 22, 2026 – Sep 28, 2026"}
          </div>
        </div>

        {/* ROW OF 3 STAT CARDS (Each a DIFFERENT bold pastel color) */}
        <section className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
          <StatCard
            variant="totalCost"
            title="Total cost"
            value={`$${totalCost.toLocaleString(undefined, { minimumFractionDigits: 0, maximumFractionDigits: 0 })}`}
            note={`Calculated across ${meetings.length} scheduled calendar events at $${currentRate}/hr`}
          />
          <StatCard
            variant="couldBeEmail"
            title="Could be email"
            value={`${dashboardData.couldBeEmailCount || 2} meetings`}
            note={`$${flaggedMeetingsCost.toLocaleString()} potential waste identified at $${currentRate}/hr`}
          />
          <StatCard
            variant="hoursSaved"
            title="Hours saved"
            value={`${hoursCouldBeSaved.toFixed(1)} hours`}
            note="Deep focus time recoverable with async memos"
          />
        </section>

        {/* Filter controls */}
        <div className="bg-white border border-[#ddd6fe] rounded-[12px] p-3 mb-6 flex flex-wrap items-center justify-between gap-3">
          <div className="flex items-center gap-2 flex-wrap">
            <span className="text-xs font-semibold text-slate-400 mr-1">Filter by status:</span>
            <button
              onClick={() => setFilter('all')}
              className={`px-3 py-1 text-xs font-semibold rounded-full transition-colors ${
                filter === 'all' 
                  ? 'bg-[#8b5cf6] text-white' 
                  : 'bg-white text-slate-600 hover:bg-slate-100 border border-slate-200'
              }`}
            >
              All ({meetings.length})
            </button>
            <button
              onClick={() => setFilter('email')}
              className={`px-3 py-1 text-xs font-semibold rounded-full transition-colors ${
                filter === 'email' 
                  ? 'bg-[#fb7185] text-white' 
                  : 'bg-[#ffe4e6] text-[#9f1239] hover:bg-[#fecdd3]'
              }`}
            >
              Flag as email ({meetings.filter(m => (m.score?.necessityScore ?? 50) < 40).length})
            </button>
            <button
              onClick={() => setFilter('borderline')}
              className={`px-3 py-1 text-xs font-semibold rounded-full transition-colors ${
                filter === 'borderline' 
                  ? 'bg-[#fbbf24] text-[#78350f]' 
                  : 'bg-[#fef3c7] text-[#78350f] hover:bg-[#fde68a]'
              }`}
            >
              Borderline ({meetings.filter(m => (m.score?.necessityScore ?? 50) >= 40 && (m.score?.necessityScore ?? 50) <= 69).length})
            </button>
            <button
              onClick={() => setFilter('necessary')}
              className={`px-3 py-1 text-xs font-semibold rounded-full transition-colors ${
                filter === 'necessary' 
                  ? 'bg-[#34d399] text-[#064e3b]' 
                  : 'bg-[#d1fae5] text-[#065f46] hover:bg-[#a7f3d0]'
              }`}
            >
              Necessary ({meetings.filter(m => (m.score?.necessityScore ?? 50) >= 70).length})
            </button>
          </div>

          <div className="text-xs text-slate-400">
            Showing {filteredMeetings.length} of {meetings.length} meetings
          </div>
        </div>

        {/* LIST OF MEETING CARDS */}
        <section className="space-y-4">
          {filteredMeetings.map((meeting) => (
            <MeetingCard
              key={meeting.id}
              title={meeting.title}
              dayTime={meeting.dayTime || (meeting.startTime ? new Date(meeting.startTime).toLocaleDateString(undefined, { weekday: 'long', hour: '2-digit', minute: '2-digit' }) : 'This week')}
              attendeeCount={meeting.attendeesCount}
              duration={`${meeting.durationMinutes} mins`}
              cost={meeting.cost !== undefined ? meeting.cost : (meeting.score ? meeting.score.estimatedCost : Math.round((meeting.attendeesCount * meeting.durationMinutes / 60) * currentRate))}
              score={meeting.score ? meeting.score.necessityScore : 50}
              suggestion={meeting.score?.suggestion?.suggestionText || meeting.suggestionText || "Review agenda and participants."}
            />
          ))}
        </section>

      </main>

      {/* TOAST NOTIFICATION */}
      {toastMessage && (
        <div className="fixed bottom-6 right-6 px-4 py-3 rounded-[12px] bg-[#8b5cf6] text-white text-xs font-semibold z-50 flex items-center gap-2 shadow-sm">
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M5 13l4 4L19 7" />
          </svg>
          <span>{toastMessage}</span>
        </div>
      )}

    </div>
  );
}
