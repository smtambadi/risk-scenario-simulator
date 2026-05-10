import React, { useState } from 'react';
import { getAIDescription, getAIRecommendations, getAICategorise, askAIQuery } from '../../services/api';
import LoadingSpinner from '../common/LoadingSpinner';
import toast from 'react-hot-toast';

const AIPanel = ({ riskId, riskTitle }) => {
  const [description, setDescription] = useState(null);
  const [loadingDescribe, setLoadingDescribe] = useState(false);
  const [recommendations, setRecommendations] = useState(null);
  const [loadingRecommend, setLoadingRecommend] = useState(false);
  const [category, setCategory] = useState(null);
  const [loadingCategorise, setLoadingCategorise] = useState(false);
  const [question, setQuestion] = useState('');
  const [aiResponse, setAiResponse] = useState(null);
  const [loadingQuery, setLoadingQuery] = useState(false);
  const [activeTab, setActiveTab] = useState('describe');

  const handleDescribe = async () => {
    setLoadingDescribe(true);
    try {
      const data = await getAIDescription(riskId);
      setDescription(data);
      toast.success('AI analysis generated');
    } catch (error) {
      toast.error('Failed to get AI analysis');
    } finally {
      setLoadingDescribe(false);
    }
  };

  const handleGetRecommendations = async () => {
    setLoadingRecommend(true);
    try {
      const data = await getAIRecommendations(riskId);
      setRecommendations(data);
      toast.success('AI recommendations generated');
    } catch (error) {
      toast.error('Failed to get recommendations');
    } finally {
      setLoadingRecommend(false);
    }
  };

  const handleCategorise = async () => {
    setLoadingCategorise(true);
    try {
      const data = await getAICategorise(riskId);
      setCategory(data);
      toast.success('AI categorisation complete');
    } catch (error) {
      toast.error('Failed to categorise');
    } finally {
      setLoadingCategorise(false);
    }
  };

  const handleAskQuestion = async () => {
    if (!question.trim()) {
      toast.error('Please enter a question');
      return;
    }
    setLoadingQuery(true);
    try {
      const data = await askAIQuery(riskId, question);
      setAiResponse(data);
    } catch (error) {
      toast.error('Failed to get AI response');
    } finally {
      setLoadingQuery(false);
    }
  };

  const tabs = [
    { id: 'describe', label: 'Describe' },
    { id: 'recommend', label: 'Recommend' },
    { id: 'categorise', label: 'Categorise' },
    { id: 'query', label: 'Ask AI' },
  ];

  const MetaInfo = ({ meta }) => {
    if (!meta) return null;
    return (
      <div className="flex flex-wrap gap-2 mt-3">
        {meta.confidence != null && (
          <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
            {(meta.confidence * 100).toFixed(0)}% confidence
          </span>
        )}
        {meta.model_used && (
          <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-purple-500/10 text-purple-400 border border-purple-500/20">
            {meta.model_used}
          </span>
        )}
        {meta.response_time_ms != null && (
          <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-cyan-500/10 text-cyan-400 border border-cyan-500/20">
            {meta.response_time_ms}ms
          </span>
        )}
        {meta.tokens_used != null && meta.tokens_used > 0 && (
          <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-amber-500/10 text-amber-400 border border-amber-500/20">
            {meta.tokens_used} tokens
          </span>
        )}
        {meta.cached && (
          <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-blue-500/10 text-blue-400 border border-blue-500/20">
            ⚡ Cached
          </span>
        )}
      </div>
    );
  };

  return (
    <div className="space-y-4">
      {/* Header Card */}
      <div className="bg-gradient-to-br from-[#162d47] to-[#1e3a5f] rounded-xl p-5 relative overflow-hidden" style={{ border: '1px solid rgba(168, 85, 247, 0.2)' }}>
        <div className="absolute -top-8 -right-8 w-24 h-24 rounded-full opacity-[0.07]" style={{ background: 'radial-gradient(circle, #a855f7, transparent)' }}></div>
        <div className="flex items-center gap-2 mb-2">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#a855f7" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <path d="M12 2a4 4 0 0 1 4 4c0 1.95-1.4 3.58-3.25 3.93" />
            <path d="M8.56 13.44A4 4 0 0 0 12 22a4 4 0 0 0 3.44-6.06" />
            <path d="M19.44 8.56A4 4 0 0 0 12 6a4 4 0 0 0-3.06 8.44" />
            <circle cx="12" cy="12" r="2" />
          </svg>
          <h3 className="text-xs font-bold uppercase tracking-wider text-purple-400">Neural Advisor</h3>
        </div>
        <p className="text-xs text-slate-400 leading-relaxed">
          AI-powered risk analysis using LLaMA 3.3 70B via Groq. Get insights, recommendations, and categorisation.
        </p>
      </div>

      {/* Tab Navigation */}
      <div className="flex gap-1 p-1 rounded-lg" style={{ background: 'rgba(255,255,255,0.03)', border: '1px solid rgba(255,255,255,0.06)' }}>
        {tabs.map(tab => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={`flex-1 py-2 rounded-md text-[10px] font-bold uppercase tracking-wider transition-all ${
              activeTab === tab.id
                ? 'bg-gradient-to-r from-[#a855f7] to-[#6366f1] text-white shadow-lg'
                : 'text-slate-500 hover:text-white hover:bg-white/5'
            }`}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {/* Describe Tab */}
      {activeTab === 'describe' && (
        <div className="card p-5">
          <button
            onClick={handleDescribe}
            disabled={loadingDescribe}
            className="w-full bg-gradient-to-r from-[#a855f7] to-[#6366f1] text-white py-2.5 rounded-lg text-xs font-bold uppercase tracking-wider hover:shadow-lg hover:shadow-purple-500/20 transition-all disabled:opacity-50 active:scale-[0.97]"
          >
            {loadingDescribe ? <LoadingSpinner size="sm" /> : 'Analyze Risk'}
          </button>

          {description && (
            <div className="mt-4 space-y-3">
              {description.title && (
                <div className="bg-purple-500/5 rounded-lg p-3 border border-purple-500/10">
                  <p className="text-[10px] text-purple-400 font-bold uppercase tracking-wider mb-1">AI Title</p>
                  <p className="text-sm text-white font-medium">{description.title}</p>
                </div>
              )}
              {description.description && (
                <div className="bg-slate-500/10 rounded-lg p-3 border border-slate-500/10">
                  <p className="text-[10px] text-slate-400 font-bold uppercase tracking-wider mb-1">Analysis</p>
                  <p className="text-xs text-slate-300 leading-relaxed">{description.description}</p>
                </div>
              )}
              <div className="grid grid-cols-2 gap-2">
                {description.impact && (
                  <div className="bg-red-500/5 rounded-lg p-2.5 border border-red-500/10">
                    <p className="text-[10px] text-red-400 font-bold uppercase tracking-wider mb-0.5">Impact</p>
                    <p className="text-xs text-slate-300">{description.impact}</p>
                  </div>
                )}
                {description.likelihood && (
                  <div className="bg-amber-500/5 rounded-lg p-2.5 border border-amber-500/10">
                    <p className="text-[10px] text-amber-400 font-bold uppercase tracking-wider mb-0.5">Likelihood</p>
                    <p className="text-xs text-slate-300">{description.likelihood}</p>
                  </div>
                )}
              </div>
              {description.raw_response && (
                <div className="bg-slate-500/10 rounded-lg p-3 border border-slate-500/10">
                  <p className="text-xs text-slate-300 leading-relaxed whitespace-pre-wrap">{description.raw_response}</p>
                </div>
              )}
              <MetaInfo meta={description.meta} />
            </div>
          )}
        </div>
      )}

      {/* Recommend Tab */}
      {activeTab === 'recommend' && (
        <div className="card p-5">
          <button
            onClick={handleGetRecommendations}
            disabled={loadingRecommend}
            className="w-full bg-gradient-to-r from-[#10b981] to-[#06b6d4] text-white py-2.5 rounded-lg text-xs font-bold uppercase tracking-wider hover:shadow-lg hover:shadow-emerald-500/20 transition-all disabled:opacity-50 active:scale-[0.97]"
          >
            {loadingRecommend ? <LoadingSpinner size="sm" /> : 'Get Recommendations'}
          </button>

          {recommendations && (
            <div className="mt-4">
              <div className="space-y-2">
                {(recommendations.recommendations || []).map((rec, idx) => (
                  <div key={idx} className="bg-slate-500/10 rounded-lg p-3 border border-slate-500/20">
                    <div className="flex items-start gap-2">
                      <span className="flex-shrink-0 w-5 h-5 rounded-full bg-emerald-500/10 text-emerald-400 text-2xs font-bold flex items-center justify-center mt-0.5">
                        {idx + 1}
                      </span>
                      <div className="flex-1">
                        <div className="flex items-center gap-2 mb-1">
                          {rec.action_type && (
                            <span className="px-1.5 py-0.5 rounded text-[9px] font-bold uppercase bg-cyan-500/10 text-cyan-400 border border-cyan-500/20">
                              {rec.action_type}
                            </span>
                          )}
                          {rec.priority && (
                            <span className={`px-1.5 py-0.5 rounded text-[9px] font-bold uppercase ${
                              rec.priority === 'High' ? 'bg-red-500/10 text-red-400 border border-red-500/20' :
                              rec.priority === 'Medium' ? 'bg-amber-500/10 text-amber-400 border border-amber-500/20' :
                              'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20'
                            }`}>
                              {rec.priority}
                            </span>
                          )}
                        </div>
                        <p className="text-xs text-slate-300 leading-relaxed">{rec.description}</p>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
              {recommendations.raw_response && (
                <div className="mt-3 bg-slate-500/10 rounded-lg p-3 border border-slate-500/10">
                  <p className="text-xs text-slate-300 leading-relaxed whitespace-pre-wrap">{recommendations.raw_response}</p>
                </div>
              )}
              <MetaInfo meta={recommendations.meta} />
            </div>
          )}
        </div>
      )}

      {/* Categorise Tab */}
      {activeTab === 'categorise' && (
        <div className="card p-5">
          <button
            onClick={handleCategorise}
            disabled={loadingCategorise}
            className="w-full bg-gradient-to-r from-[#f59e0b] to-[#f97316] text-[#050a15] py-2.5 rounded-lg text-xs font-bold uppercase tracking-wider hover:shadow-lg hover:shadow-amber-500/20 transition-all disabled:opacity-50 active:scale-[0.97]"
          >
            {loadingCategorise ? <LoadingSpinner size="sm" /> : 'AI Categorise'}
          </button>

          {category && (
            <div className="mt-4 space-y-3">
              <div className="text-center py-4">
                <span className="inline-block px-4 py-2 rounded-xl text-lg font-bold bg-amber-500/15 text-amber-400 border border-amber-500/25">
                  {category.category || 'Unknown'}
                </span>
              </div>
              {category.confidence != null && (
                <div className="bg-slate-500/10 rounded-lg p-3 border border-slate-500/10">
                  <div className="flex items-center justify-between mb-2">
                    <p className="text-[10px] text-slate-400 font-bold uppercase tracking-wider">Confidence</p>
                    <p className="text-sm font-bold text-white">{(category.confidence * 100).toFixed(0)}%</p>
                  </div>
                  <div className="w-full h-2 bg-slate-700 rounded-full overflow-hidden">
                    <div
                      className="h-full rounded-full transition-all duration-500"
                      style={{
                        width: `${category.confidence * 100}%`,
                        background: `linear-gradient(to right, ${category.confidence > 0.7 ? '#10b981' : category.confidence > 0.4 ? '#f59e0b' : '#ef4444'}, ${category.confidence > 0.7 ? '#06b6d4' : category.confidence > 0.4 ? '#f97316' : '#f43f5e'})`
                      }}
                    ></div>
                  </div>
                </div>
              )}
              {category.reasoning && (
                <div className="bg-slate-500/10 rounded-lg p-3 border border-slate-500/10">
                  <p className="text-[10px] text-slate-400 font-bold uppercase tracking-wider mb-1">Reasoning</p>
                  <p className="text-xs text-slate-300 leading-relaxed">{category.reasoning}</p>
                </div>
              )}
              <MetaInfo meta={category.meta} />
            </div>
          )}
        </div>
      )}

      {/* Ask AI Tab */}
      {activeTab === 'query' && (
        <div className="card p-5">
          <div className="flex gap-2">
            <input
              type="text"
              value={question}
              onChange={(e) => setQuestion(e.target.value)}
              placeholder="e.g., What are the main mitigation strategies?"
              className="input text-xs"
              onKeyDown={(e) => e.key === 'Enter' && handleAskQuestion()}
            />
            <button
              onClick={handleAskQuestion}
              disabled={loadingQuery}
              className="px-4 bg-gradient-to-r from-[#a855f7] to-[#6366f1] text-white rounded-lg text-xs font-bold hover:shadow-lg transition-all disabled:opacity-50 flex-shrink-0 active:scale-[0.97]"
            >
              {loadingQuery ? <LoadingSpinner size="sm" color="white" /> : 'Ask'}
            </button>
          </div>
          {aiResponse && (
            <div className="mt-3 space-y-2">
              <div className="p-3 bg-slate-500/10 rounded-lg border border-slate-500/20">
                <p className="text-xs text-slate-300 leading-relaxed whitespace-pre-wrap">{aiResponse.answer}</p>
              </div>
              {aiResponse.sources && aiResponse.sources.length > 0 && (
                <div className="bg-blue-500/5 rounded-lg p-3 border border-blue-500/10">
                  <p className="text-[10px] text-blue-400 font-bold uppercase tracking-wider mb-1.5">
                    Knowledge Sources ({aiResponse.total_sources || aiResponse.sources.length})
                  </p>
                  {aiResponse.sources.map((src, i) => (
                    <p key={i} className="text-[10px] text-slate-400 leading-relaxed mb-1 pl-2" style={{ borderLeft: '2px solid rgba(59, 130, 246, 0.3)' }}>
                      {src.length > 150 ? src.substring(0, 150) + '...' : src}
                    </p>
                  ))}
                </div>
              )}
              <MetaInfo meta={aiResponse.meta} />
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default AIPanel;