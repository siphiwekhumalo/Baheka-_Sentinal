import React from 'react';

export const AmlMonitoring: React.FC = () => {
  return (
    <div className="max-w-7xl mx-auto">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">AML Monitoring</h1>
        <p className="mt-2 text-gray-600">
          Real-time transaction monitoring, sanctions screening, and suspicious activity detection
        </p>
      </div>
      
      <div className="bg-white shadow rounded-lg p-6">
        <h3 className="text-lg font-medium text-gray-900 mb-4">Active Alerts</h3>
        <p className="text-gray-600">AML alerts and monitoring dashboard will be displayed here</p>
      </div>
    </div>
  );
};
