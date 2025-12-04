import React, { useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { BudgetContext } from "../context/BudgetContext";
import "./transactions.css";

function Transactions() {
  const navigate = useNavigate();
  const { transactions, addTransaction, editTransaction, deleteTransaction } = useContext(BudgetContext);

  const [form, setForm] = useState({
    date: "",
    category: "",
    type: "Expense",
    amount: "",
    notes: "",
  });

  const [editIndex, setEditIndex] = useState(null);
  const [editMode, setEditMode] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!form.amount || !form.category) return;

    if (editIndex !== null) {
      editTransaction(editIndex, form);
      setEditIndex(null);
      setEditMode(false);
    } else {
      addTransaction(form);
    }

    setForm({ date: "", category: "", type: "Expense", amount: "", notes: "" });
  };

  const beginEdit = (index) => {
    if (!editMode) return;
    setEditIndex(index);
    setForm({ ...transactions[index] });
  };

  const handleDelete = () => {
    if (editIndex === null) return;
    deleteTransaction(editIndex);
    setEditIndex(null);
    setForm({ date: "", category: "", type: "Expense", amount: "", notes: "" });
    setEditMode(false);
  };

  return (
    <div className="transactions-page">
      <header className="transactions-header">
        <h1>Transactions</h1>
        <button className="back-btn" onClick={() => navigate("/dashboard")}>
          ← Back to Dashboard
        </button>
      </header>

      <main className="transactions-main">
        <section className="transactions-form-section">
          <h2>{editIndex !== null ? "Edit Transaction" : "Add a New Transaction"}</h2>
          <form className="transactions-form" onSubmit={handleSubmit}>
            <div className="form-group">
              <label>Date</label>
              <input
                type="date"
                name="date"
                value={form.date}
                onChange={handleChange}
                required
              />
            </div>
            <div className="form-group">
              <label>Category</label>
              <input
                type="text"
                name="category"
                placeholder="e.g. Groceries, Books"
                value={form.category}
                onChange={handleChange}
                required
              />
            </div>
            <div className="form-group">
              <label>Type</label>
              <select name="type" value={form.type} onChange={handleChange}>
                <option value="Expense">Expense</option>
                <option value="Income">Income</option>
              </select>
            </div>
            <div className="form-group">
              <label>Amount ($)</label>
              <input
                type="number"
                name="amount"
                placeholder="Enter amount"
                value={form.amount}
                onChange={handleChange}
                required
              />
            </div>
            <div className="form-group">
              <label>Notes</label>
              <textarea
                name="notes"
                placeholder="Optional notes"
                value={form.notes}
                onChange={handleChange}
              />
            </div>
            <button type="submit" className="add-btn">
              {editIndex !== null ? "Save Changes" : "Add Transaction"}
            </button>

            {editIndex !== null && (
              <button type="button" className="delete-inside-btn" onClick={handleDelete}>
                Delete Transaction
              </button>
            )}
          </form>
        </section>

        <section className="transactions-table-section">
          <div className="table-header-row">
            <h2>Transaction History</h2>
            <button
              className="edit-mode-btn"
              onClick={() => {
                setEditMode(!editMode);
                setEditIndex(null);
                setForm({ date: "", category: "", type: "Expense", amount: "", notes: "" });
              }}
            >
              {editMode ? "Done Editing" : "Edit"}
            </button>
          </div>

          {transactions.length === 0 ? (
            <p className="no-data">No transactions recorded yet.</p>
          ) : (
            <table className="transactions-table">
              <thead>
                <tr>
                  <th>Date</th>
                  <th>Category</th>
                  <th>Type</th>
                  <th>Amount ($)</th>
                  <th>Notes</th>
                </tr>
              </thead>
              <tbody>
                {transactions.map((t, index) => (
                  <tr
                    key={index}
                    className={editMode ? "row-editable" : ""}
                    onClick={() => beginEdit(index)}
                  >
                    <td>{t.date}</td>
                    <td>{t.category}</td>
                    <td>{t.type}</td>
                    <td className={t.type === "Income" ? "positive" : "negative"}>
                      {parseFloat(t.amount).toFixed(2)}
                    </td>
                    <td>{t.notes}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </section>
      </main>

      <footer className="transactions-footer">
        © SpartanSpend | 2025 CSDS 395 Senior Project
      </footer>
    </div>
  );
}

export default Transactions;
