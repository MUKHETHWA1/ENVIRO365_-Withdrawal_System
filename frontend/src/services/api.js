// frontend/src/services/api.js
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

//  Investor service
export const investorService = {
  getAllInvestors: () => api.get('/investors'),
  getInvestorById: (id) => api.get(`/investors/${id}`),
};

export const portfolioService = {
  getInvestorPortfolios: (investorId) =>
    api.get(`/portfolios/investor/${investorId}`),
  getPortfolioById: (portfolioId) =>
    api.get(`/portfolios/${portfolioId}`),
};

export const withdrawalService = {
  createWithdrawal: (data) =>
    api.post('/withdrawals/create', data),
  getInvestorWithdrawals: (investorId) =>
    api.get(`/withdrawals/investor/${investorId}`),
};

export const reportService = {
  exportCSV: (investorId) =>
    api.get(`/reports/withdrawals/csv?investorId=${investorId}`, {
      responseType: 'blob',
    }),
};

export default api;