import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getStats, getRisks, getAIHealth } from '../services/api';
import KPICard from '../components/dashboard/KPICard';
import { TrendLineChart, StatusPieChart } from '../components/dashboard/Charts';
import RiskHeatMap from '../components/dashboard/RiskHeatMap';
import RiskComparison from '../components/dashboard/RiskComparison';
import LoadingSpinner from '../components/common/LoadingSpinner';
import EmptyState from '../components/common/EmptyState';

const Dashboard = () => {
  const [stats, setStats] = useState(null);
  const [risks, setRisks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [aiHealth, setAiHealth] = useState(null);
  const navigate = useNavigate();

  useEffect(() => { fetchStats(); }, []);

  const fetchStats = async () => {
    try {
      const [statsData, risksData] = await Promise.all([
        getStats(),
        getRisks({ page: 0, size: 100 })
      ]);
      setStats(statsData);
      setRisks(risksData.content || []);

      // Fetch AI health (non-blocking)
      try {
        const health = await getAIHealth();
        setAiHealth(health);
      } catch {
        setAiHealth({ status: 'unavailable' });
      }
    } catch (err) {
      console.error('Failed to fetch data:', err);
      setError(true);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div className="flex justify-center items-center py-20"><LoadingSpinner size="lg" /></div>;
  if (error || !stats) return <EmptyState title="Unable to load dashboard" message="Please try again later" />;

  const formatCurrency = (val) => {
    if (val >= 10000000) return `₹${(val / 10000000).toFixed(1)}Cr`;
    if (val >= 100000) return `₹${(val / 100000).toFixed(1)}L`;
    if (val >= 1000) return `₹${(val / 1000).toFixed(0)}K`;
    return `₹${val}`;
  };

  const kpis = [
    { title: 'Total Scenarios', value: stats.totalRisks || 0, icon: (<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="10" /><line x1="12" y1="8" x2="12" y2="16" /><line x1="8" y1="12" x2="16" y2="12" /></svg>), color: 'blue', trend: { positive: true, value: 12 } },
    { title: 'Active Risks', value: stats.openRisks || 0, icon: (<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12" /></svg>), color: 'green', trend: { positive: false, value: 8 } },
    { title: 'Critical Alerts', value: stats.criticalRisks || 0, icon: (<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2" /></svg>), color: 'red', trend: { positive: false, value: 5 } },
    { title: 'Total Exposure', value: formatCurrency(stats.totalExposure || 0), icon: (<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="2" y="3" width="20" height="14" rx="2" ry="2" /><line x1="8" y1="21" x2="16" y2="21" /><line x1="12" y1="17" x2="12" y2="21" /></svg>), color: 'purple', trend: { positive: false, value: 18 } },
  ];

  const trendData = stats.monthlyTrend || [];
  const aiOnline = aiHealth && aiHealth.status === 'ok';

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-4 opacity-0 animate-fade-in-up">
        <div>
          <h1 className="text-2xl font-bold text-white">Dashboard</h1>
          <p className="text-sm text-slate-200 mt-1">Real-time insights and monitoring for risk scenarios across all sectors.</p>
        </div>
        <div className="flex gap-3">
          <button onClick={() => navigate('/reports')} className="btn-outline">Download Report</button>
          <button onClick={() => navigate('/risks/new')} className="btn-primary">New Scenario</button>
        </div>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {kpis.map((kpi, idx) => (
          <div key={idx} className={`opacity-0 animate-fade-in-up stagger-${idx + 1}`}>
            <KPICard {...kpi} />
          </div>
        ))}
      </div>

      {/* Charts Row */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
        <div className="lg:col-span-2 card p-5 opacity-0 animate-fade-in-up stagger-5">
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center gap-2">
              <span className="w-2 h-2 rounded-full bg-[#10b981]"></span>
              <h3 className="section-title">Risk Evolution Trend</h3>
            </div>
          </div>
          {trendData.length > 0 ? (
            <TrendLineChart data={trendData} />
          ) : (
            <div className="flex items-center justify-center h-52 text-slate-400 text-sm">
              <p>No trend data available.</p>
            </div>
          )}
        </div>

        <div className="space-y-4">
          <div className="card p-5 opacity-0 animate-fade-in-up stagger-6">
            <h3 className="section-title mb-3">Risk Distribution</h3>
            {stats.statusBreakdown && stats.statusBreakdown.length > 0 ? (
              <StatusPieChart data={stats.statusBreakdown} />
            ) : (
              <div className="h-32 flex items-center justify-center text-slate-400 text-sm">No distribution data</div>
            )}
          </div>

          {/* AI Health Status Card */}
          <div
            className="rounded-xl p-5 relative overflow-hidden shadow-lg opacity-0 animate-fade-in-up stagger-6"
            style={{
              background: 'linear-gradient(135deg, #162d47, #1e3a5f)',
              border: `1px solid ${aiOnline ? 'rgba(16, 185, 129, 0.2)' : 'rgba(239, 68, 68, 0.2)'}`,
            }}
          >
            <div className="absolute top-3 right-3 opacity-[0.06]">
              <svg width="52" height="52" viewBox="0 0 24 24" fill="#ffffff">
                <path d="M12 2a4 4 0 0 1 4 4c0 1.95-1.4 3.58-3.25 3.93M8.56 13.44A4 4 0 0 0 12 22a4 4 0 0 0 3.44-6.06M19.44 8.56A4 4 0 0 0 12 6a4 4 0 0 0-3.06 8.44" />
              </svg>
            </div>

            <div className="flex items-center gap-2 mb-3">
              <span className={`w-2 h-2 rounded-full ${aiOnline ? 'bg-emerald-500 animate-pulse' : 'bg-red-500'}`}></span>
              <h3 className="text-sm font-bold uppercase tracking-widest text-white">Neural Advisor</h3>
              <span className={`px-2 py-0.5 rounded text-[9px] font-bold uppercase ${aiOnline ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20' : 'bg-red-500/10 text-red-400 border border-red-500/20'}`}>
                {aiOnline ? 'Online' : 'Offline'}
              </span>
            </div>

            {aiOnline && aiHealth ? (
              <div className="space-y-2">
                <div className="grid grid-cols-2 gap-2">
                  <div className="bg-white/5 rounded-lg p-2">
                    <p className="text-[9px] text-slate-500 font-bold uppercase tracking-wider">Model</p>
                    <p className="text-xs text-white font-medium truncate">{aiHealth.model || 'LLaMA 3.3'}</p>
                  </div>
                  <div className="bg-white/5 rounded-lg p-2">
                    <p className="text-[9px] text-slate-500 font-bold uppercase tracking-wider">Provider</p>
                    <p className="text-xs text-white font-medium">{aiHealth.provider || 'Groq'}</p>
                  </div>
                  <div className="bg-white/5 rounded-lg p-2">
                    <p className="text-[9px] text-slate-500 font-bold uppercase tracking-wider">KB Docs</p>
                    <p className="text-xs text-white font-medium">{aiHealth.chromadb_documents || 0}</p>
                  </div>
                  <div className="bg-white/5 rounded-lg p-2">
                    <p className="text-[9px] text-slate-500 font-bold uppercase tracking-wider">Uptime</p>
                    <p className="text-xs text-white font-medium">{aiHealth.uptime_formatted || 'N/A'}</p>
                  </div>
                </div>
                {aiHealth.cache_stats && (
                  <div className="bg-white/5 rounded-lg p-2">
                    <p className="text-[9px] text-slate-500 font-bold uppercase tracking-wider mb-1">Cache</p>
                    <div className="flex items-center gap-3">
                      <span className="text-[10px] text-emerald-400 font-bold">{aiHealth.cache_stats.hits || 0} hits</span>
                      <span className="text-[10px] text-slate-400">|</span>
                      <span className="text-[10px] text-amber-400 font-bold">{aiHealth.cache_stats.misses || 0} misses</span>
                      <span className="text-[10px] text-slate-400">|</span>
                      <span className="text-[10px] text-cyan-400 font-bold">{aiHealth.cache_stats.hit_rate || 0}% rate</span>
                    </div>
                  </div>
                )}
              </div>
            ) : (
              <p className="text-xs text-slate-400 leading-relaxed mb-4">
                AI analysis suggests {stats.criticalRisks || 0} critical scenarios require immediate attention. Avg risk score: {stats.avgRiskScore || 'N/A'}/10.
              </p>
            )}

            <button onClick={() => navigate('/risks')} className="w-full mt-3 bg-gradient-to-r from-[#10b981] to-[#06b6d4] text-white py-2.5 rounded-lg text-xs font-bold uppercase tracking-widest hover:shadow-lg transition-all active:scale-[0.97]">
              View All Scenarios
            </button>
          </div>
        </div>
      </div>

      {/* Risk Heat Map and Comparison */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        <div className="opacity-0 animate-fade-in-up stagger-7">
          <RiskHeatMap risks={risks} />
        </div>
        <div className="opacity-0 animate-fade-in-up stagger-7">
          <RiskComparison risks={risks} />
        </div>
      </div>
    </div>
  );
};

export default Dashboard;