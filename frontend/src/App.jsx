// frontend/src/App.jsx
import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import PortfolioDashboard from './components/PortfolioDashboard';
import WithdrawalForm from './components/WithdrawalForm';
import WithdrawalHistory from './components/WithdrawalHistory';

function App() {
  return (
    <Router>
      <div className="min-h-screen bg-gray-100">
        <Toaster position="top-right" />

        {/* Navigation */}
        <nav className="bg-blue-600 text-white shadow-lg">
          <div className="container mx-auto px-4">
            <div className="flex items-center justify-between h-16">
              <Link to="/" className="text-xl font-bold">
                🌱 ENVIRO365 Investments
              </Link>
              <div className="flex space-x-4">
                <Link to="/" className="hover:bg-blue-700 px-3 py-2 rounded">
                  Dashboard
                </Link>
                <Link to="/withdraw" className="hover:bg-blue-700 px-3 py-2 rounded">
                  Withdraw
                </Link>
                <Link to="/history" className="hover:bg-blue-700 px-3 py-2 rounded">
                  History
                </Link>
              </div>
            </div>
          </div>
        </nav>

        {/* Main Content */}
        <div className="container mx-auto px-4 py-8">
          <Routes>
            <Route path="/" element={<PortfolioDashboard />} />
            <Route path="/withdraw" element={<WithdrawalForm />} />
            <Route path="/history" element={<WithdrawalHistory />} />
          </Routes>
        </div>
      </div>
    </Router>
  );
}

export default App;