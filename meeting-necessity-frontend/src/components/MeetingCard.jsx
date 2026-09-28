import React from 'react';

export default function MeetingCard({ 
  title, 
  dayTime, 
  attendeeCount, 
  duration, 
  cost,
  score, 
  suggestion 
}) {
  // Determine status and style configuration based on score (0-100)
  let cardBg = '';
  let borderColor = '';
  let badgeBg = '';
  let badgeTextColor = '';
  let statusLabel = '';
  let textColor = '';
  let detailTextColor = '';

  if (score < 40) {
    // Flag as email: rose pink background #ffe4e6, coral badge #fb7185
    cardBg = 'bg-[#ffe4e6]';
    borderColor = 'border-[#fecdd3]';
    badgeBg = 'bg-[#fb7185]';
    badgeTextColor = 'text-white';
    statusLabel = 'Flag as email';
    textColor = 'text-[#881337]';
    detailTextColor = 'text-[#9f1239]';
  } else if (score <= 69) {
    // Borderline: amber background #fef3c7, amber badge #fbbf24
    cardBg = 'bg-[#fef3c7]';
    borderColor = 'border-[#fde68a]';
    badgeBg = 'bg-[#fbbf24]';
    badgeTextColor = 'text-[#78350f]';
    statusLabel = 'Borderline';
    textColor = 'text-[#78350f]';
    detailTextColor = 'text-[#92400e]';
  } else {
    // Necessary: mint green background #d1fae5, green badge #34d399
    cardBg = 'bg-[#d1fae5]';
    borderColor = 'border-[#a7f3d0]';
    badgeBg = 'bg-[#34d399]';
    badgeTextColor = 'text-[#064e3b]';
    statusLabel = 'Necessary';
    textColor = 'text-[#064e3b]';
    detailTextColor = 'text-[#065f46]';
  }

  return (
    <div className={`p-5 rounded-[12px] border ${cardBg} ${borderColor} transition-all`}>
      <div className="flex flex-col sm:flex-row sm:items-start justify-between gap-3 mb-3">
        <div>
          <h3 className={`text-base font-bold mb-1 ${textColor}`}>
            {title}
          </h3>
          <div className={`flex flex-wrap items-center gap-3 text-xs ${detailTextColor}`}>
            <span className="flex items-center gap-1.5 font-medium">
              <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              {dayTime}
            </span>
            <span className="opacity-40">•</span>
            <span className="flex items-center gap-1.5 font-medium">
              <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
              </svg>
              {attendeeCount} attendees
            </span>
            <span className="opacity-40">•</span>
            <span className="font-medium">
              {duration}
            </span>
            {cost !== undefined && cost !== null && (
              <>
                <span className="opacity-40">•</span>
                <span className="font-bold underline">
                  ${cost.toLocaleString()} cost
                </span>
              </>
            )}
          </div>
        </div>

        {/* Score badge (0-100): pill shape, bold number */}
        <div className="flex items-center sm:self-start gap-2">
          <div className={`px-3 py-1 rounded-full ${badgeBg} ${badgeTextColor} text-xs font-semibold flex items-center gap-1.5 whitespace-nowrap`}>
            <span className="text-sm font-extrabold">{score}</span>
            <span className="text-[11px] opacity-90">/ 100</span>
          </div>
        </div>
      </div>

      {/* One-line suggestion in sentence case */}
      <div className="pt-3 border-t border-black/5 flex items-center justify-between text-xs">
        <p className={`font-medium ${textColor}`}>
          <span className="font-bold">Suggestion: </span>
          {suggestion}
        </p>
        <span className={`text-[11px] font-semibold px-2 py-0.5 rounded-full bg-white/60 ${textColor}`}>
          {statusLabel}
        </span>
      </div>
    </div>
  );
}
