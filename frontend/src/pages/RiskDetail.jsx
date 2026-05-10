import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getRiskById, deleteRisk } from '../services/api';
import AIPanel from '../components/risks/AIPanel';
import StatusBadge from '../components/risks/StatusBadge';
import LoadingSpinner from '../components/common/LoadingSpinner';
import EmptyState from '../components/common/EmptyState';
import { formatDateTime, formatRiskScore } from '../utils/formatters';
import toast from 'react-hot-toast';

const RiskDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [risk, setRisk] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => { fetchRisk(); }, [id]);

  const fetchRisk = async () => {
    try { setRisk(await getRiskById(id)); }
    catch { setError(true); }
    finally { setLoading(false); }
  };

  const handleDelete = async () => {
    if (window.confirm('Are you sure you want to delete this risk scenario?')) {
      try { await deleteRisk(id); toast.success('Risk deleted'); navigate('/risks'); }
      catch { toast.error('Failed to delete risk'); }
    }
  };

  const getScoreColor = (score) => {
    if (score >= 8) return 'text-[#ef4444]';
    if (score >= 6) return 'text-[#f97316]';
    if (score >= 4) return 'text-[#a855f7]';
    return 'text-[#10b981]';
  };

  const getScoreBg = (score) => {
    if (score >= 8) return 'bg-[#ef4444]/10 border-[#ef4444]/20';
    if (score >= 6) return 'bg-[#f97316]/10 border-[#f97316]/20';
    if (score >= 4) return 'bg-[#a855f7]/10 border-[#a855f7]/20';
    return 'bg-[#10b981]/10 border-[#10b981]/20';
  };

  if (loading) return <div className="flex justify-center py-20"><LoadingSpinner size="lg" /></div>;
  if (error || !risk) return <EmptyState title="Risk not found" message="The requested risk scenario does not exist" />;

  const formatCost = (val) => {
    if (!val) return 'N/A';
    if (val >= 10000000) return `₹${(val / 10000000).toFixed(1)}Cr`;
    if (val >= 100000) return `₹${(val / 100000).toFixed(1)}L`;
    return `₹${(val / 1000).toFixed(0)}K`;
  };

  return (
    <div className="space-y-6">
      {/* Back + Header */}
      <div className="opacity-0 animate-fade-in-up">
        <button onClick={() => navigate('/risks')} className="flex items-center gap-2 text-sm text-slate-200 hover:text-white mb-3 transition-colors">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polyline points="15 18 9 12 15 6" /></svg>
          <span className="section-title">Back to Scenarios</span>
        </button>
        <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-4">
          <div>
            <h1 className="text-2xl font-bold text-white">{risk.title}</h1>
            <div className="flex flex-wrap items-center gap-3 mt-2">
              <StatusBadge status={risk.status} />
              <span className={`text-sm font-bold ${getScoreColor(risk.riskScore)}`}>Score: {formatRiskScore(risk.riskScore)}</span>
              <span className="text-xs text-slate-200">ID: #{risk.id}</span>
            </div>
          </div>
          <div className="flex gap-3">
            <button onClick={() => navigate(`/risks/${id}/edit`)} className="btn-outline">Edit Scenario</button>
            <button onClick={handleDelete} className="btn-danger">Delete</button>
          </div>
        </div>
      </div>

      {/* Content Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 space-y-4">
          {/* Risk Score Card */}
          <div className={`rounded-xl p-5 border ${getScoreBg(risk.riskScore)} opacity-0 animate-fade-in-up stagger-1`}>
            <div className="flex items-center justify-between">
              <div>
                <p className="text-2xs text-slate-300 font-semibold uppercase tracking-wider mb-1">Risk Assessment Score</p>
                <p className={`text-3xl font-bold ${getScoreColor(risk.riskScore)}`}>{risk.riskScore}/10</p>
              </div>
              <div className="text-right">
                <p className="text-2xs text-slate-300 font-semibold uppercase tracking-wider mb-1">Projected Exposure</p>
                <p className="text-2xl font-bold text-white">{formatCost(risk.projectedCost)}</p>
              </div>
            </div>
          </div>

          {/* Description */}
          <div className="card p-6 opacity-0 animate-fade-in-up stagger-2">
            <h3 className="section-title mb-3">Description</h3>
            <p className="text-sm text-white/90 whitespace-pre-wrap leading-relaxed">{risk.description}</p>
          </div>

          {/* Risk Parameters */}
          <div className="card p-6 opacity-0 animate-fade-in-up stagger-3">
            <h3 className="section-title mb-4">Risk Parameters</h3>
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
              {[
                { label: 'Category', value: risk.category },
                { label: 'Impact', value: risk.impact || 'N/A' },
                { label: 'Likelihood', value: risk.likelihood || 'N/A' },
                { label: 'Created', value: formatDateTime(risk.createdAt) },
                { label: 'Last Updated', value: formatDateTime(risk.updatedAt) },
                { label: 'Status', value: risk.status?.replace('_', ' ') || 'N/A' },
              ].map((item, i) => (
                <div key={i} className="bg-[#162d47] rounded-lg p-3">
                  <p className="text-2xs text-slate-300 font-semibold uppercase tracking-wider mb-1">{item.label}</p>
                  <p className="text-sm font-semibold text-white">{item.value}</p>
                </div>
              ))}
            </div>
          </div>

          {/* Mitigation Plan */}
          {risk.mitigationPlan && (
            <div className="card p-6 opacity-0 animate-fade-in-up stagger-4">
              <h3 className="section-title mb-3">Mitigation Plan</h3>
              <p className="text-sm text-white/90 whitespace-pre-wrap leading-relaxed">{risk.mitigationPlan}</p>
            </div>
          )}

          {/* AI Auto-Analysis (populated async on create) */}
          {(risk.aiDescription || risk.aiRecommendations) && (
            <div className="rounded-xl p-5 opacity-0 animate-fade-in-up stagger-4" style={{ background: 'linear-gradient(135deg, rgba(168,85,247,0.06), rgba(99,102,241,0.06))', border: '1px solid rgba(168,85,247,0.2)' }}>
              <div className="flex items-center gap-2 mb-4">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#a855f7" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M12 2a4 4 0 0 1 4 4c0 1.95-1.4 3.58-3.25 3.93"/><path d="M8.56 13.44A4 4 0 0 0 12 22a4 4 0 0 0 3.44-6.06"/><circle cx="12" cy="12" r="2"/>
                </svg>
                <h3 className="text-xs font-bold uppercase tracking-widest text-purple-400">AI Auto-Analysis</h3>
                <span className="px-2 py-0.5 rounded text-[9px] font-bold bg-purple-500/10 text-purple-400 border border-purple-500/20">Generated on Create</span>
              </div>

              {risk.aiDescription && (() => {
                try {
                  const parsed = JSON.parse(risk.aiDescription);
                  return (
                    <div className="space-y-3 mb-4">
                      {parsed.description && (
                        <div className="bg-white/5 rounded-lg p-3">
                          <p className="text-[10px] text-slate-500 font-bold uppercase tracking-wider mb-1">AI Description</p>
                          <p className="text-xs text-slate-300 leading-relaxed">{parsed.description}</p>
                        </div>
                      )}
                      {(parsed.impact || parsed.likelihood) && (
                        <div className="grid grid-cols-2 gap-2">
                          {parsed.impact && (
                            <div className="bg-white/5 rounded-lg p-2.5">
                              <p className="text-[10px] text-slate-500 font-bold uppercase tracking-wider mb-0.5">Impact</p>
                              <p className="text-xs text-white">{parsed.impact}</p>
                            </div>
                          )}
                          {parsed.likelihood && (
                            <div className="bg-white/5 rounded-lg p-2.5">
                              <p className="text-[10px] text-slate-500 font-bold uppercase tracking-wider mb-0.5">Likelihood</p>
                              <p className="text-xs text-white">{parsed.likelihood}</p>
                            </div>
                          )}
                        </div>
                      )}
                    </div>
                  );
                } catch {
                  return (
                    <div className="bg-white/5 rounded-lg p-3 mb-4">
                      <p className="text-[10px] text-slate-500 font-bold uppercase tracking-wider mb-1">AI Description</p>
                      <p className="text-xs text-slate-300 leading-relaxed">{risk.aiDescription}</p>
                    </div>
                  );
                }
              })()}

              {risk.aiRecommendations && (() => {
                try {
                  const parsed = JSON.parse(risk.aiRecommendations);
                  return (
                    <div className="space-y-2">
                      <p className="text-[10px] text-slate-500 font-bold uppercase tracking-wider mb-2">AI Recommendations</p>
                      {(parsed.recommendations || []).map((rec, i) => (
                        <div key={i} className="bg-white/5 rounded-lg p-2.5 flex items-start gap-2">
                          <span className="w-4 h-4 rounded-full bg-purple-500/20 text-purple-400 text-[9px] font-bold flex items-center justify-center flex-shrink-0 mt-0.5">{i+1}</span>
                          <div>
                            <p className="text-xs text-slate-300 leading-relaxed">{rec.description}</p>
                            {rec.priority && <span className="text-[9px] text-purple-400 font-bold uppercase">{rec.action_type} · {rec.priority}</span>}
                          </div>
                        </div>
                      ))}
                    </div>
                  );
                } catch {
                  return (
                    <div className="bg-white/5 rounded-lg p-3">
                      <p className="text-[10px] text-slate-500 font-bold uppercase tracking-wider mb-1">AI Recommendations</p>
                      <p className="text-xs text-slate-300 leading-relaxed">{risk.aiRecommendations}</p>
                    </div>
                  );
                }
              })()}
            </div>
          )}
        </div>

        {/* AI Panel */}
        <div className="lg:col-span-1 opacity-0 animate-fade-in-up stagger-3">
          <AIPanel riskId={risk.id} riskTitle={risk.title} />
        </div>
      </div>
    </div>
  );
};

export default RiskDetail;