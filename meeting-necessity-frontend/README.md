# Meeting Necessity Score — Frontend

A modern React + Tailwind CSS dashboard that analyzes scheduled meetings, predicts necessity scores, flags low-value meetings as asynchronous email memos, and recalculates real-time financial waste based on company hourly salary settings.

## 🎨 Design & Aesthetic
- **Page Background**: Light Lavender (`#f5f3ff`)
- **Card Styling**: Clean white background, subtle light-purple borders (`#ddd6fe`), `rounded-[12px]`, zero gradients, and zero drop shadows.
- **Brand Purple**: `#8b5cf6`
- **Pastel Badge Statuses**:
  - 🔴 **Flag as email** (Score < 40): Soft Rose (`#ffe4e6`) & Coral Badge (`#fb7185`)
  - 🟡 **Borderline** (Score 40–69): Warm Amber (`#fef3c7`) & Amber Badge (`#fbbf24`)
  - 🟢 **Necessary** (Score 70+): Mint Green (`#d1fae5`) & Green Badge (`#34d399`)

## 🚀 Quick Start

### 1. Install Dependencies
```bash
npm install
```

### 2. Run Development Server
```bash
npm run dev
```
Open [http://localhost:5173](http://localhost:5173) in your browser.

### 3. Build for Production
```bash
npm run build
```

## ⚙ Key Features
- **Two Screen Flows**:
  - **Screen 1**: Google Calendar OAuth Login Screen.
  - **Screen 2**: Full Audit Dashboard with filters and sync.
- **Dynamic Cost Recalculator**: Instant recalculation across all meeting cards when the hourly rate is modified in Settings.
- **Dual-Mode Backend Resilience**: Seamlessly connects to Spring Boot on `http://localhost:8080`, with local dynamic recalculation fallback when offline.
