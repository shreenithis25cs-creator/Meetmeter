import React from 'react';

export default function StatCard({ title, value, note, variant }) {
  // Styles for each distinct bold pastel color
  const variantStyles = {
    totalCost: {
      bg: 'bg-[#ede9fe]',
      text: 'text-[#4c1d95]',
      border: 'border-[#ddd6fe]',
      subText: 'text-[#6b21a8]',
      icon: (
        <svg className="w-5 h-5 text-[#4c1d95]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
      )
    },
    couldBeEmail: {
      bg: 'bg-[#ffe4e6]',
      text: 'text-[#9f1239]',
      border: 'border-[#fecdd3]',
      subText: 'text-[#be123c]',
      icon: (
        <svg className="w-5 h-5 text-[#9f1239]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
        </svg>
      )
    },
    hoursSaved: {
      bg: 'bg-[#d1fae5]',
      text: 'text-[#065f46]',
      border: 'border-[#a7f3d0]',
      subText: 'text-[#047857]',
      icon: (
        <svg className="w-5 h-5 text-[#065f46]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
      )
    }
  };

  const style = variantStyles[variant] || variantStyles.totalCost;

  return (
    <div className={`p-5 rounded-[12px] border ${style.bg} ${style.border} flex flex-col justify-between`}>
      <div className="flex items-center justify-between mb-3">
        <span className={`text-xs font-semibold ${style.text}`}>
          {title}
        </span>
        <div className="w-8 h-8 rounded-[8px] bg-white/60 flex items-center justify-center">
          {style.icon}
        </div>
      </div>
      <div>
        <div className={`text-3xl font-bold tracking-tight mb-1 ${style.text}`}>
          {value}
        </div>
        <p className={`text-xs font-medium ${style.subText}`}>
          {note}
        </p>
      </div>
    </div>
  );
}
