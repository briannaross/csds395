import React, { useContext } from "react";
import { useNavigate } from "react-router-dom";
import { BudgetContext } from "../context/BudgetContext";
import { MealPlanContext } from "../context/MealPlanContext";
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
    <div className="dashboard-page">
      <header className="dashboard-header">
        <h1 className="header-title">Dashboard</h1>
        <button className="logout-btn" onClick={() => navigate("/")}>
          Logout
        </button>
      </header>

      <main className="dashboard-content">
        <div className="balances-row centered-balances">
          <div className="balance-card casecash">
            CaseCash: <strong>${totalCaseCashSpent.toFixed(2)}</strong>
          </div>
          <div className="balance-card personal">
            Personal Funds: <strong>${balances.personalFunds.toFixed(2)}</strong>
          </div>
          <div className="balance-card meal-swipes">
            Meal Swipes: <strong>{swipesUsed} / {weeklyLimit}</strong>
          </div>
        </div>

        <div className="main-panels">
          <div className="chart-panel">
            <h2>Weekly Spending Trend</h2>
            {transactions.length === 0 ? (
              <p className="no-data">No data yet.</p>
            ) : (
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
                  scales: { y: { beginAtZero: true } },
                }}
              />
            )}
          </div>

          <div className="table-panel">
            <h2>Transaction History</h2>

            {transactions.length === 0 ? (
              <p className="no-data">No transactions yet.</p>
            ) : (
              <table className="dashboard-transactions-table">
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
                    .slice(-3)
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

            <button
              className="view-more-btn"
              onClick={() => navigate("/transactions")}
            >
              View Full Transactions
            </button>
            <button
              className="view-more-btn"
              onClick={() => navigate("/casecash")}
            >
              View CaseCash History
            </button>
            <button
              className="view-more-btn"
              onClick={() => navigate("/meal-plan")}
            >
              View Meal Plan
            </button>
          </div>
        </div>
      </main>

      <footer className="dashboard-footer">
        © SpartanSpend | 2025 CSDS 395 Senior Project
      </footer>
    </div>
  );
}

export default Dashboard;
