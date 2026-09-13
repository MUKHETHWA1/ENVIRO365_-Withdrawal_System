// frontend/src/components/WithdrawalHistory.jsx
import React, { useState, useEffect } from 'react';
import toast from 'react-hot-toast';
import { format } from 'date-fns';
import { investorService, withdrawalService, reportService } from '../services/api';

const WithdrawalHistory = () => {
  const [investors, setInvestors] = useState([]);
  const [withdrawals, setWithdrawals] = useState([]);
  const [investorId, setInvestorId] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadInvestors();
  }, []);

  useEffect(() => {
    if (investorId) {
      loadWithdrawals();
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
      setLoading(false);
    }
  };

  const loadWithdrawals = async () => {
    try {
      setLoading(true);
      const response = await withdrawalService.getInvestorWithdrawals(investorId);
      setWithdrawals(response.data);
    } catch (error) {
      toast.error('Failed to load withdrawal history');
    } finally {
      setLoading(false);
    }
  };

  const handleExportCSV = async () => {
    try {
      const response = await reportService.exportCSV(investorId);
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute(
        'download',
        `withdrawals_${investorId}_${format(new Date(), 'yyyy-MM-dd')}.csv`
      );
      document.body.appendChild(link);
      link.click();
      link.remove();
      toast.success('CSV downloaded');
    } catch (error) {
      toast.error('Failed to download CSV');
    }
  };

  const statusColor = (status) => {
    switch (status) {
      case 'PENDING': return 'bg-yellow-100 text-yellow-700';
      case 'APPROVED': return 'bg-green-100 text-green-700';
      case 'REJECTED': return 'bg-red-100 text-red-700';
      default: return 'bg-gray-100 text-gray-700';
    }
  };

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Withdrawal History</h1>
        <div className="flex space-x-4">
          <select
            value={investorId || ''}
            onChange={(e) => setInvestorId(Number(e.target.value))}
            className="px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            {investors.map((inv) => (
              <option key={inv.id} value={inv.id}>
                {inv.fullName}
              </option>
            ))}
          </select>
          <button
            onClick={handleExportCSV}
            className="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 transition flex items-center gap-2"
          >
            ⬇️ Export CSV
          </button>
        </div>
      </div>

      {loading ? (
        <div className="flex justify-center items-center h-64">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        </div>
      ) : withdrawals.length === 0 ? (
        <div className="bg-white rounded-lg shadow p-8 text-center">
          <p className="text-gray-500">No withdrawal history available</p>
        </div>
      ) : (
        <div className="bg-white rounded-lg shadow overflow-hidden">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Amount</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Remaining Balance</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Notes</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {withdrawals.map((w) => (
                <tr key={w.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 text-sm">
                    {format(new Date(w.withdrawalDate), 'MMM dd, yyyy HH:mm')}
                  </td>
                  <td className="px-6 py-4 text-sm font-medium">
                    R{parseFloat(w.amount).toLocaleString()}
                  </td>
                  <td className="px-6 py-4">
                    <span className={`px-2 py-1 text-xs rounded ${statusColor(w.status)}`}>
                      {w.status}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-sm">
                    R{parseFloat(w.remainingBalance).toLocaleString()}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-500">
                    {w.rejectionReason || '—'}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default WithdrawalHistory;