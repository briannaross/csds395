import React, { useContext } from "react";
import { useNavigate } from "react-router-dom";
import { BudgetContext } from "../context/BudgetContext";
import { MealPlanContext } from "../context/MealPlanContext";
import AssistantChat from "./AssistantChat";
import "./dashboard.css";
import { Line } from "react-chartjs-2";
import {
  Chart as ChartJS,
  LineElement,
  CategoryScale,
  LinearScale,
  PointElement,
  Tooltip,
  Legend,
} from "chart.js";

ChartJS.register(LineElement, CategoryScale, LinearScale, PointElement, Tooltip, Legend);

function Dashboard() {
  const navigate = useNavigate();
  const { transactions, balances, caseCashEntries = [] } = useContext(BudgetContext);
  const { swipesUsed, weeklyLimit } = useContext(MealPlanContext);

  // Calculate total CaseCash spent
  const totalCaseCashSpent = caseCashEntries.reduce((sum, entry) => {
    return sum + parseFloat(entry.amount || 0);
  }, 0);

  return (
    <div className="dashboard-page" style={{ height: '100vh', overflow: 'hidden' }}>
      <header className="dashboard-header" style={{ padding: '10px 20px', minHeight: 'auto' }}>
        <h1 className="header-title" style={{ fontSize: '1.5rem', margin: 0 }}>Dashboard</h1>
        <button className="logout-btn" onClick={() => navigate("/")}>
          Logout
        </button>
      </header>

      <main className="dashboard-content" style={{ padding: '15px', maxHeight: 'calc(100vh - 120px)', overflow: 'auto' }}>
        {/* Balance Cards at Top - Compact */}
        <div className="balances-row centered-balances" style={{ marginBottom: '15px', gap: '10px' }}>
          <div className="balance-card casecash" style={{ padding: '10px 15px', fontSize: '0.9rem' }}>
            CaseCash: <strong>${totalCaseCashSpent.toFixed(2)}</strong>
          </div>
          <div className="balance-card personal" style={{ padding: '10px 15px', fontSize: '0.9rem' }}>
            Personal Funds: <strong>${balances.personalFunds.toFixed(2)}</strong>
          </div>
          <div className="balance-card meal-swipes" style={{ padding: '10px 15px', fontSize: '0.9rem' }}>
            Meal Swipes: <strong>{swipesUsed} / {weeklyLimit}</strong>
          </div>
        </div>

        {/* Chart and Chatbot Side by Side - Compact */}
        <div className="main-panels" style={{ gap: '15px', marginBottom: '15px' }}>
          <div className="chart-panel" style={{ flex: '0 0 48%', padding: '15px' }}>
            <h2 style={{ fontSize: '1.1rem', marginBottom: '10px' }}>Weekly Spending Trend</h2>
            {transactions.length === 0 ? (
              <p className="no-data">No data yet.</p>
            ) : (
              <div style={{ height: '250px' }}>
                <Line
                  data={{
                    labels: transactions.slice(-7).map((t) => t.date),
                    datasets: [
                      {
                        label: "Spending ($)",
                        data: transactions.slice(-7).map((t) =>
                          t.type === "Expense" ? t.amount : 0
                        ),
                        borderColor: "#244b9b",
                        backgroundColor: "rgba(36, 75, 155, 0.2)",
                        fill: true,
                      },
                    ],
                  }}
                  options={{
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: { y: { beginAtZero: true } },
                    plugins: {
                      legend: { display: false }
                    }
                  }}
                />
              </div>
            )}
          </div>

          <div style={{ flex: '0 0 48%' }}>
            <AssistantChat />
          </div>
        </div>

        {/* Transaction History - Compact */}
        <div className="table-panel" style={{ padding: '15px' }}>
          <h2 style={{ fontSize: '1.1rem', marginBottom: '10px' }}>Transaction History</h2>

          {transactions.length === 0 ? (
            <p className="no-data">No transactions yet.</p>
          ) : (
            <table className="dashboard-transactions-table" style={{ fontSize: '0.85rem' }}>
              <thead>
                <tr>
                  <th>Date</th>
                  <th>Category</th>
                  <th>Type</th>
                  <th>Amount ($)</th>
                </tr>
              </thead>
              <tbody>
                {transactions
                  .slice(-2)
                  .reverse()
                  .map((t, index) => (
                    <tr key={index}>
                      <td>{t.date || "-"}</td>
                      <td>{t.category}</td>
                      <td>{t.type}</td>
                      <td
                        className={
                          t.type === "Income" ? "positive" : "negative"
                        }
                      >
                        {parseFloat(t.amount).toFixed(2)}
                      </td>
                    </tr>
                  ))}
              </tbody>
            </table>
          )}

          <div style={{ display: 'flex', gap: '10px', marginTop: '10px', flexWrap: 'wrap' }}>
            <button
              className="view-more-btn"
              onClick={() => navigate("/transactions")}
              style={{ padding: '8px 16px', fontSize: '0.85rem' }}
            >
              View Full Transactions
            </button>
            <button
              className="view-more-btn"
              onClick={() => navigate("/casecash")}
              style={{ padding: '8px 16px', fontSize: '0.85rem' }}
            >
              View CaseCash History
            </button>
            <button
              className="view-more-btn"
              onClick={() => navigate("/meal-plan")}
              style={{ padding: '8px 16px', fontSize: '0.85rem' }}
            >
              View Meal Plan
            </button>
          </div>
        </div>
      </main>

      <footer className="dashboard-footer" style={{ padding: '10px', fontSize: '0.8rem' }}>
        © SpartanSpend | 2025 CSDS 395 Senior Project
      </footer>
    </div>
  );
}

export default Dashboard;
