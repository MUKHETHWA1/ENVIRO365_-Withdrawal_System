// frontend/src/components/PortfolioDashboard.jsx
import React, { useState, useEffect } from 'react';
import toast from 'react-hot-toast';
import { investorService, portfolioService } from '../services/api';

const PortfolioDashboard = () => {
  const [investors, setInvestors] = useState([]);
  const [portfolios, setPortfolios] = useState([]);
  const [investorId, setInvestorId] = useState(null);
  const [loading, setLoading] = useState(true);

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
        setInvestorId(response.data[0].id); // Select first investor by default
      }
    } catch (error) {
      toast.error('Failed to load investors');
      setLoading(false);
    }
  };

  const loadPortfolios = async () => {
    try {
      setLoading(true);
      const response = await portfolioService.getInvestorPortfolios(investorId);
      setPortfolios(response.data);
    } catch (error) {
      toast.error('Failed to load portfolios');
    } finally {
      setLoading(false);
    }
  };

  const calculateTotal = () =>
    portfolios.reduce((sum, p) => sum + parseFloat(p.balance), 0);

  const currentInvestor = investors.find((i) => i.id === investorId);

  if (!investorId && investors.length === 0) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-800">Portfolio Dashboard</h1>
          {currentInvestor && (
            <p className="text-sm text-gray-500 mt-1">
              {currentInvestor.email} • Age: {currentInvestor.age}
            </p>
          )}
        </div>
        <select
          value={investorId || ''}
          onChange={(e) => setInvestorId(Number(e.target.value))}
          className="px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
        >
          {investors.map((inv) => (
            <option key={inv.id} value={inv.id}>
              {inv.fullName} (Age {inv.age})
            </option>
          ))}
        </select>
      </div>

      {loading ? (
        <div className="flex justify-center items-center h-64">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        </div>
      ) : (
        <>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
            <div className="bg-white rounded-lg shadow p-6">
              <h3 className="text-gray-500 text-sm">Total Portfolios</h3>
              <p className="text-2xl font-bold">{portfolios.length}</p>
            </div>
            <div className="bg-white rounded-lg shadow p-6">
              <h3 className="text-gray-500 text-sm">Total Balance</h3>
              <p className="text-2xl font-bold">
                R{calculateTotal().toLocaleString()}
              </p>
            </div>
            <div className="bg-white rounded-lg shadow p-6">
              <h3 className="text-gray-500 text-sm">Available for Withdrawal</h3>
              <p className="text-2xl font-bold text-green-600">
                R{(calculateTotal() * 0.9).toLocaleString()}
              </p>
              <p className="text-xs text-gray-500 mt-1">Maximum 90% of balance</p>
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {portfolios.map((portfolio) => (
              <div key={portfolio.id} className="bg-white rounded-lg shadow p-6">
                <div className="flex justify-between items-start mb-4">
                  <div>
                    <h3 className="text-lg font-semibold">{portfolio.name}</h3>
                    <span
                      className={`px-2 py-1 text-xs rounded ${
                        portfolio.type === 'RETIREMENT'
                          ? 'bg-purple-100 text-purple-700'
                          : 'bg-blue-100 text-blue-700'
                      }`}
                    >
                      {portfolio.type}
                    </span>
                  </div>
                  <div className="text-right">
                    <p className="text-2xl font-bold">
                      R{parseFloat(portfolio.balance).toLocaleString()}
                    </p>
                    <p className="text-sm text-gray-500">
                      Available: R{parseFloat(portfolio.availableForWithdrawal).toLocaleString()}
                    </p>
                  </div>
                </div>

                <div className="border-t pt-4">
                  <p className="text-sm font-medium text-gray-700 mb-2">Holdings</p>
                  {portfolio.products && portfolio.products.length > 0 ? (
                    portfolio.products.map((product) => (
                      <div key={product.id} className="flex justify-between text-sm py-1">
                        <span>{product.name}</span>
                        <span className="font-medium">
                          R{parseFloat(product.value).toLocaleString()}
                        </span>
                      </div>
                    ))
                  ) : (
                    <p className="text-sm text-gray-400">No holdings</p>
                  )}
                </div>
              </div>
            ))}
          </div>
        </>
      )}
    </div>
  );
};

export default PortfolioDashboard;