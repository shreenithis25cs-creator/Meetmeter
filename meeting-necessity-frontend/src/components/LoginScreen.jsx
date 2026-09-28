import React from 'react';

export default function LoginScreen({ onLogin }) {
  return (
    <div className="min-h-screen flex bg-[#f5f3ff]">

      {/* LEFT PANEL - Login card */}
      <div className="w-full max-w-md flex flex-col justify-center px-12 py-10 bg-white border-r border-[#ddd6fe]">

        {/* App icon */}
        <div className="w-16 h-16 rounded-[12px] bg-[#ede9fe] flex items-center justify-center mb-6">
          <svg
            className="w-8 h-8 text-[#8b5cf6]"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
            aria-hidden="true"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth="2"
              d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
            />
          </svg>
        </div>

        {/* App name + tagline */}
        <h1 className="text-2xl font-bold text-slate-800 mb-2">
          Meeting necessity score
        </h1>
        <p className="text-sm text-slate-500 mb-8">
          Audit your schedule and reclaim hours lost to meetings that could be emails.
        </p>

        {/* Google login button */}
        <button
          onClick={onLogin}
          className="w-full flex items-center justify-center gap-3 py-3 px-5 rounded-[12px] bg-[#8b5cf6] hover:bg-[#7c3aed] text-white text-sm font-semibold transition-colors duration-150"
        >
          <svg className="w-4 h-4" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
            <path
              fill="#fff"
              d="M12.24 10.285V14.4h6.806c-.275 1.765-2.056 5.174-6.806 5.174-4.095 0-7.439-3.389-7.439-7.574s3.344-7.574 7.439-7.574c2.33 0 3.891.989 4.785 1.849l3.254-3.138C18.189 1.186 15.479 0 12.24 0c-6.635 0-12 5.365-12 12s5.365 12 12 12c6.926 0 11.52-4.869 11.52-11.726 0-.788-.085-1.39-.189-1.989H12.24z"
            />
          </svg>
          <span>Connect Google Calendar</span>
        </button>

        <p className="text-xs text-slate-400 mt-4">
          Read-only access. We never edit your calendar.
        </p>
      </div>

      {/* RIGHT PANEL - Decorative */}
      <div className="hidden md:flex flex-1 flex-col items-center justify-center bg-[#ede9fe] p-12">
        <div className="max-w-sm text-center">
          <div className="w-20 h-20 rounded-[20px] bg-[#8b5cf6] flex items-center justify-center mx-auto mb-6">
            <svg className="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2"
                d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
            </svg>
          </div>
          <h2 className="text-xl font-bold text-[#4c1d95] mb-3">Reclaim your focus time</h2>
          <p className="text-sm text-[#6d28d9] leading-relaxed">
            Analyze every meeting on your calendar, score its necessity, and find out how much time and money your team can save.
          </p>

          {/* Mini stat pills */}
          <div className="flex flex-col gap-3 mt-8">
            <div className="bg-white/60 rounded-[10px] px-4 py-3 text-left">
              <p className="text-xs font-semibold text-[#4c1d95]">💸 Avg meeting waste</p>
              <p className="text-lg font-bold text-[#7c3aed]">$4,200 / week</p>
            </div>
            <div className="bg-white/60 rounded-[10px] px-4 py-3 text-left">
              <p className="text-xs font-semibold text-[#4c1d95]">⏱ Hours recoverable</p>
              <p className="text-lg font-bold text-[#7c3aed]">6.5 hours / week</p>
            </div>
          </div>
        </div>
      </div>

    </div>
  );
}
