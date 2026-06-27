import { useState, useEffect } from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, ReferenceLine } from 'recharts';

export default function App() {
  const [chartData, setChartData] = useState([]);
  const [indicators, setIndicators] = useState({ currentPrice: 0, rsi: 50, macd: 0 });
  const [portfolio, setPortfolio] = useState({ cashBalance: 100000, holdings: {} });
  const [aiSignal, setAiSignal] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const interval = setInterval(() => {
      fetch('http://localhost:8080/api/indicators/AAPL')
        .then(res => { if (res.status === 204) return null; return res.json(); })
        .then(data => {
          if (data) {
            setIndicators(data);
            setChartData(prev => {
              const newData = [...prev, { time: new Date().toLocaleTimeString([], {hour12: false}), price: data.currentPrice }];
              return newData.slice(-30); // Keep 30 ticks for a smoother line
            });
          }
        }).catch(err => console.error(err));
    }, 1000);
    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    const interval = setInterval(() => {
      fetch('http://localhost:8080/api/portfolio')
        .then(res => res.json())
        .then(data => setPortfolio(data))
        .catch(err => console.error(err));
    }, 2000);
    return () => clearInterval(interval);
  }, []);

  const handleTriggerAI = () => {
    setLoading(true);
    fetch('http://localhost:8080/api/signal/AAPL', { method: 'POST' })
      .then(res => res.json())
      .then(data => { setAiSignal(data); setLoading(false); })
      .catch(err => { console.error(err); setLoading(false); });
  };

  // --- FUTURISTIC ENTERPRISE STYLING ---
  const theme = {
    bg: '#0a0b10', // Deepest space dark
    panelBg: 'rgba(20, 22, 37, 0.65)', // Glassmorphism base
    border: '1px solid rgba(255, 255, 255, 0.08)',
    accent: '#00e5ff', // Cyber cyan
    textMain: '#e2e8f0',
    textMuted: '#94a3b8',
    glow: '0 0 15px rgba(0, 229, 255, 0.3)'
  };

  const styles = {
    container: { backgroundColor: theme.bg, color: theme.textMain, minHeight: '100vh', padding: '30px', fontFamily: '"Inter", "Segoe UI", sans-serif' },
    header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '30px' },
    title: { margin: 0, fontSize: '28px', fontWeight: '800', letterSpacing: '1px', textTransform: 'uppercase', color: theme.textMain },
    titleAccent: { color: theme.accent, textShadow: theme.glow },
    grid: { display: 'flex', gap: '24px', flexWrap: 'wrap' },
    glassPanel: { 
      backgroundColor: theme.panelBg, 
      backdropFilter: 'blur(16px)', 
      WebkitBackdropFilter: 'blur(16px)', 
      border: theme.border, 
      borderRadius: '16px', 
      padding: '24px', 
      flex: 1,
      boxShadow: '0 8px 32px 0 rgba(0, 0, 0, 0.3)' // Soft Neumorphic depth
    },
    metricBox: { textAlign: 'center', padding: '15px', backgroundColor: 'rgba(0,0,0,0.2)', borderRadius: '12px', border: theme.border },
    metricLabel: { fontSize: '12px', color: theme.textMuted, textTransform: 'uppercase', letterSpacing: '1px', marginBottom: '8px' },
    metricValue: { fontSize: '24px', fontWeight: '700', color: theme.accent },
    button: { 
      backgroundColor: 'transparent', color: theme.accent, padding: '16px', fontSize: '14px', fontWeight: '700', 
      border: `2px solid ${theme.accent}`, borderRadius: '8px', cursor: 'pointer', width: '100%', 
      textTransform: 'uppercase', letterSpacing: '1px', transition: 'all 0.3s ease',
      boxShadow: loading ? 'none' : theme.glow
    },
    signalBox: { 
      marginTop: '20px', padding: '20px', borderRadius: '12px', 
      backgroundColor: 'rgba(0, 0, 0, 0.3)', borderLeft: `4px solid ${theme.accent}` 
    }
  };

  // Dynamic color for RSI (Red for >70, Green for <30, Cyan for neutral)
  const getRsiColor = (val) => val > 70 ? '#ff4d4d' : val < 30 ? '#00e5ff' : theme.textMain;

  return (
    <div style={styles.container}>
      <header style={styles.header}>
        <h1 style={styles.title}>Neural<span style={styles.titleAccent}>Quant</span> // Terminal</h1>
        <div style={{ color: theme.textMuted, fontSize: '14px', fontWeight: '600' }}>SYS.STATUS: <span style={{ color: '#00ff41' }}>ONLINE</span></div>
      </header>

      <div style={styles.grid}>
        {/* LEFT COLUMN: Data & Chart */}
        <div style={{ flex: 2, display: 'flex', flexDirection: 'column', gap: '24px', minWidth: '600px' }}>
          
          {/* Top Math Metrics */}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '20px' }}>
            <div style={styles.glassPanel}>
              <div style={styles.metricLabel}>AAPL Live Price</div>
              <div style={styles.metricValue}>${indicators.currentPrice.toFixed(2)}</div>
            </div>
            <div style={styles.glassPanel}>
              <div style={styles.metricLabel}>RSI (14) Momentum</div>
              <div style={{ ...styles.metricValue, color: getRsiColor(indicators.rsi) }}>
                {indicators.rsi.toFixed(2)}
              </div>
            </div>
            <div style={styles.glassPanel}>
              <div style={styles.metricLabel}>MACD Divergence</div>
              <div style={{ ...styles.metricValue, color: indicators.macd > 0 ? '#00e5ff' : '#ff4d4d' }}>
                {indicators.macd.toFixed(3)}
              </div>
            </div>
          </div>

          {/* Chart Panel */}
          <div style={{ ...styles.glassPanel, height: '400px' }}>
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={chartData} margin={{ top: 10, right: 10, left: 0, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.05)" vertical={false} />
                <XAxis dataKey="time" stroke={theme.textMuted} fontSize={12} tickLine={false} axisLine={false} />
                <YAxis domain={['auto', 'auto']} stroke={theme.textMuted} fontSize={12} tickLine={false} axisLine={false} width={60} />
                <Tooltip 
                  contentStyle={{ backgroundColor: 'rgba(10, 11, 16, 0.9)', border: theme.border, borderRadius: '8px', backdropFilter: 'blur(8px)' }}
                  itemStyle={{ color: theme.accent, fontWeight: 'bold' }}
                />
                <Line 
                  type="monotone" 
                  dataKey="price" 
                  stroke={theme.accent} 
                  strokeWidth={3} 
                  dot={false} 
                  isAnimationActive={false} 
                  activeDot={{ r: 6, fill: theme.accent, stroke: '#000', strokeWidth: 2 }}
                />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* RIGHT COLUMN: AI Agent & Execution */}
        <div style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: '24px', minWidth: '350px' }}>
          
          <div style={styles.glassPanel}>
            <h3 style={{ color: theme.textMuted, fontSize: '14px', textTransform: 'uppercase', marginTop: 0 }}>Autonomous Agent</h3>
            <button 
              style={styles.button} 
              onClick={handleTriggerAI} 
              disabled={loading}
              onMouseOver={(e) => e.target.style.backgroundColor = 'rgba(0, 229, 255, 0.1)'}
              onMouseOut={(e) => e.target.style.backgroundColor = 'transparent'}
            >
              {loading ? "EXECUTING REASONING..." : "INITIALIZE SIGNAL"}
            </button>
            
            {aiSignal && (
              <div style={{ ...styles.signalBox, 
                borderLeftColor: aiSignal.action === 'BUY' ? theme.accent : aiSignal.action === 'SELL' ? '#ff4d4d' : theme.textMuted 
              }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
                  <h4 style={{ margin: 0, fontSize: '18px', color: theme.textMain }}>
                    ACTION: <span style={{ color: aiSignal.action === 'BUY' ? theme.accent : aiSignal.action === 'SELL' ? '#ff4d4d' : theme.textMuted }}>{aiSignal.action}</span>
                  </h4>
                  <span style={{ fontSize: '12px', color: theme.accent, backgroundColor: 'rgba(0, 229, 255, 0.1)', padding: '4px 8px', borderRadius: '4px' }}>
                    {(aiSignal.confidence * 100).toFixed(0)}% CONF
                  </span>
                </div>
                <p style={{ fontSize: '13px', lineHeight: '1.6', color: theme.textMuted, margin: 0 }}>{aiSignal.reasoning}</p>
              </div>
            )}
          </div>

          <div style={styles.glassPanel}>
            <h3 style={{ color: theme.textMuted, fontSize: '14px', textTransform: 'uppercase', marginTop: 0 }}>Portfolio State</h3>
            <div style={{ fontSize: '32px', fontWeight: '800', color: theme.textMain, marginBottom: '20px' }}>
              ${portfolio.cashBalance.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
            </div>
            
            <div style={{ borderTop: theme.border, paddingTop: '15px' }}>
              <div style={{ fontSize: '12px', color: theme.textMuted, textTransform: 'uppercase', marginBottom: '10px' }}>Active Positions</div>
              {Object.keys(portfolio.holdings).length === 0 ? (
                <div style={{ color: '#555', fontStyle: 'italic', fontSize: '13px' }}>No active allocations.</div>
              ) : (
                Object.entries(portfolio.holdings).map(([sym, pos]) => (
                  <div key={sym} style={{ display: 'flex', justifyContent: 'space-between', padding: '10px', backgroundColor: 'rgba(0,0,0,0.2)', borderRadius: '6px', marginBottom: '8px' }}>
                    <strong style={{ color: theme.accent }}>{sym}</strong>
                    <span>{pos.quantity} units</span>
                  </div>
                ))
              )}
            </div>
          </div>
        </div>

      </div>
    </div>
  );
}