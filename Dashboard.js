import React, { useContext } from "react";
import { useNavigate } from "react-router-dom";
import { BudgetContext } from "../context/BudgetContext";
import "./dashboard.css";

function Dashboard() {
  const navigate = useNavigate();
  const { transactions, balances } = useContext(BudgetContext);

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
            CaseCash: <strong>${balances.caseCash.toFixed(2)}</strong>
          </div>
          <div className="balance-card personal">
            Personal Funds: <strong>${balances.personalFunds.toFixed(2)}</strong>
          </div>
        </div>

        <div className="main-panels">

          <div className="chart-panel">
            <h2>Weekly Spending Trend</h2>
            <div className="chart-placeholder">
              [Will connect/create once SQL code is done]
            </div>
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
