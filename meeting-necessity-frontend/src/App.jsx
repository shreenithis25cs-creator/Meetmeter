import React, { useState } from 'react';
import LoginScreen from './components/LoginScreen';
import DashboardScreen from './components/DashboardScreen';

export default function App() {
  const [currentScreen, setCurrentScreen] = useState('login');

  return (
    <div className="min-h-screen bg-[#f5f3ff] antialiased">
      {/* Floating screen switcher for demonstration & quick preview */}
      <div className="fixed top-3 left-1/2 -translate-x-1/2 z-50 flex items-center gap-1.5 bg-white border border-[#ddd6fe] p-1.5 rounded-[12px]">
        <button
          onClick={() => setCurrentScreen('login')}
          className={`px-2.5 py-1 text-xs font-semibold rounded-[8px] transition-colors ${
            currentScreen === 'login'
              ? 'bg-[#8b5cf6] text-white'
              : 'text-slate-600 hover:bg-slate-100'
          }`}
        >
          Screen 1: Login
        </button>
        <button
          onClick={() => setCurrentScreen('dashboard')}
          className={`px-2.5 py-1 text-xs font-semibold rounded-[8px] transition-colors ${
            currentScreen === 'dashboard'
              ? 'bg-[#8b5cf6] text-white'
              : 'text-slate-600 hover:bg-slate-100'
          }`}
        >
          Screen 2: Dashboard
        </button>
      </div>

      {currentScreen === 'login' ? (
        <LoginScreen onLogin={() => setCurrentScreen('dashboard')} />
      ) : (
        <DashboardScreen onLogout={() => setCurrentScreen('login')} />
      )}
    </div>
  );
}
