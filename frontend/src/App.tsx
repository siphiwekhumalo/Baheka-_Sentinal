import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { Layout } from './components/layout/Layout';
import { Dashboard } from './pages/Dashboard';
import { RiskManagement } from './pages/RiskManagement';
import { AmlMonitoring } from './pages/AmlMonitoring';
import { ComplianceCenter } from './pages/ComplianceCenter';
import { SecurityCenter } from './pages/SecurityCenter';
import { Reports } from './pages/Reports';
import { Settings } from './pages/Settings';
import './App.css';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
});

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <Router>
        <div className="App">
          <Layout>
            <Routes>
              <Route path="/" element={<Navigate to="/dashboard" replace />} />
              <Route path="/dashboard" element={<Dashboard />} />
              <Route path="/risk" element={<RiskManagement />} />
              <Route path="/aml" element={<AmlMonitoring />} />
              <Route path="/compliance" element={<ComplianceCenter />} />
              <Route path="/security" element={<SecurityCenter />} />
              <Route path="/reports" element={<Reports />} />
              <Route path="/settings" element={<Settings />} />
            </Routes>
          </Layout>
        </div>
      </Router>
    </QueryClientProvider>
  );
}

export default App;
