/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        pageBg: '#f5f3ff',
        brandPurple: '#8b5cf6',
        brandPurpleHover: '#7c3aed',
        brandPurpleLight: '#ede9fe',
        // Stat & Card Colors
        statVioletBg: '#ede9fe',
        statVioletText: '#4c1d95',
        statRoseBg: '#ffe4e6',
        statRoseText: '#9f1239',
        statMintBg: '#d1fae5',
        statMintText: '#065f46',
        // Status Colors
        statusEmailBg: '#ffe4e6',
        statusEmailBadge: '#fb7185',
        statusBorderlineBg: '#fef3c7',
        statusBorderlineBadge: '#fbbf24',
        statusNecessaryBg: '#d1fae5',
        statusNecessaryBadge: '#34d399',
      },
      borderRadius: {
        'card': '12px',
      }
    },
  },
  plugins: [],
}
