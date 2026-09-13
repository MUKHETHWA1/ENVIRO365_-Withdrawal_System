// frontend/src/components/WithdrawalForm.jsx
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { investorService, portfolioService, withdrawalService } from '../services/api';

const WithdrawalForm = () => {
  const navigate = useNavigate();
  const [investors, setInvestors] = useState([]);
  const [investorId, setInvestorId] = useState(null);
  const [portfolios, setPortfolios] = useState([]);
  const [selectedPortfolio, setSelectedPortfolio] = useState('');
  const [amount, setAmount] = useState('');
  const [loading, setLoading] = useState(false);
  const [portfolioLoading, setPortfolioLoading] = useState(false);

  // Load investors on mount
  useEffect(() => {
    loadInvestors();
  }, []);

  // Load portfolios when investor changes
  useEffect(() => {
    if (investorId) {
      loadPortfolios();
    }
  }, [investorId]);

  const loadInvestors = async () => {
    try {
      const response = await investorService.getAllInvestors();
      setInvestors(response.data);
      if (response.data.length > 0) {
        setInvestorId(response.data[0].id);
      }
    } catch (error) {
      toast.error('Failed to load investors');
    }
  };

  const loadPortfolios = async () => {
    try {
      setPortfolioLoading(true);
      const response = await portfolioService.getInvestorPortfolios(investorId);
      setPortfolios(response.data);
      setSelectedPortfolio('');
    } catch (error) {
      toast.error('Failed to load portfolios');
    } finally {
      setPortfolioLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!selectedPortfolio) {
      toast.error('Please select a portfolio');
      return;
    }
    if (!amount || parseFloat(amount) <= 0) {
      toast.error('Please enter a valid amount');
      return;
    }

    const portfolio = portfolios.find((p) => p.id === parseInt(selectedPortfolio));
    const withdrawalAmount = parseFloat(amount);
    const maxWithdrawal = parseFloat(portfolio.availableForWithdrawal);

    if (withdrawalAmount > maxWithdrawal) {
      toast.error(`Maximum withdrawal is $${maxWithdrawal.toLocaleString()}`);
      return;
    }

    try {
      setLoading(true);
      await withdrawalService.createWithdrawal({
        investorId,
        portfolioId: parseInt(selectedPortfolio),
        amount: withdrawalAmount,
      });
      toast.success('Withdrawal submitted successfully!');
      navigate('/history');
    } catch (error) {
      const msg = error.response?.data?.message || 'Failed to process withdrawal';
      toast.error(msg);
    } finally {
      setLoading(false);
    }
  };

  const selected = portfolios.find((p) => p.id === parseInt(selectedPortfolio));
  const currentInvestor = investors.find((i) => i.id === investorId);

  return (
    <div className="max-w-2xl mx-auto">
      <h1 className="text-2xl font-bold text-gray-800 mb-6">Request Withdrawal</h1>

      <div className="bg-white rounded-lg shadow p-6">
        <form onSubmit={handleSubmit}>
          {/* Investor Selection */}
          <div className="mb-6">
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Investor
            </label>
            <select
              value={investorId || ''}
              onChange={(e) => setInvestorId(Number(e.target.value))}
              className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              {investors.map((inv) => (
                <option key={inv.id} value={inv.id}>
                  {inv.fullName} (Age {inv.age})
                </option>
              ))}
            </select>
          </div>

          {/* Portfolio Selection */}
          <div className="mb-6">
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Select Portfolio
            </label>
            <select
              value={selectedPortfolio}
              onChange={(e) => setSelectedPortfolio(e.target.value)}
              className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              disabled={portfolioLoading}
            >
              <option value="">Select a portfolio...</option>
              {portfolios.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.name} — R{parseFloat(p.balance).toLocaleString()}
                </option>
              ))}
            </select>
          </div>

          {/* Portfolio Info */}
          {selected && (
            <div className="bg-gray-50 p-4 rounded-lg mb-6">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <p className="text-sm text-gray-600">Current Balance</p>
                  <p className="text-lg font-semibold">
                    R{parseFloat(selected.balance).toLocaleString()}
                  </p>
                </div>
                <div>
                  <p className="text-sm text-gray-600">Max Withdrawal (90%)</p>
                  <p className="text-lg font-semibold text-green-600">
                    R{parseFloat(selected.availableForWithdrawal).toLocaleString()}
                  </p>
                </div>
                <div className="col-span-2">
                  <p className="text-sm text-gray-600">Portfolio Type</p>
                  <p className="font-medium">
                    {selected.type}
                    {selected.type === 'RETIREMENT' && (
                      <span className="ml-2 text-xs text-purple-600 bg-purple-100 px-2 py-1 rounded">
                        Age &gt; 65 required
                      </span>
                    )}
                  </p>
                </div>
              </div>
            </div>
          )}

          {/* Amount Input */}
          <div className="mb-6">
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Withdrawal Amount (R)
            </label>
            <input
              type="number"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              placeholder="Enter amount..."
              className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              step="0.01"
              min="0.01"
            />
            {selected && (
              <p className="text-xs text-gray-500 mt-1">
                Max: R{parseFloat(selected.availableForWithdrawal).toLocaleString()}
              </p>
            )}
          </div>

          <button
            type="submit"
            disabled={loading || !selectedPortfolio || !amount}
            className="w-full bg-blue-600 text-white py-3 rounded-lg font-medium hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition"
          >
            {loading ? 'Processing...' : 'Submit Withdrawal Request'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default WithdrawalForm;