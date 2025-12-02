import React, { useState, useContext } from "react";
import { Link } from "react-router-dom";
import "./casecash.css";
import { BudgetContext } from "../context/BudgetContext";

function CaseCash() {
  const {
    caseCashEntries = [], 
    addCaseCashEntry,
    editCaseCashEntry,
    deleteCaseCashEntry,
  } = useContext(BudgetContext);

  const [form, setForm] = useState({
    date: "",
    place: "",
    amount: "",
  });

  const [editIndex, setEditIndex] = useState(null);
  const [editMode, setEditMode] = useState(false);

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  function handleSubmit(e) {
    e.preventDefault();
    if (!form.date || !form.place || !form.amount) return;

    if (editIndex !== null) {
      editCaseCashEntry(editIndex, form);
      setEditIndex(null);
      setEditMode(false);
    } else {
      addCaseCashEntry(form);
    }

    setForm({ date: "", place: "", amount: "" });
  }

  function beginEdit(index) {
    if (!editMode) return;
    setEditIndex(index);
    setForm({ ...caseCashEntries[index] }); 
  }

  function handleDelete() {
    if (editIndex === null) return;
    deleteCaseCashEntry(editIndex);
    setEditIndex(null);
    setForm({ date: "", place: "", amount: "" });
    setEditMode(false);
  }

  return (
    <div className="transactions-page">
      <header className="transactions-header">
        <h1>CaseCash Tracking</h1>
        <Link to="/dashboard">
          <button className="back-btn">← Back to Dashboard</button>
        </Link>
      </header>

      <main className="transactions-main">
        {/* FORM */}
        <div className="transactions-form-section">
          <h2>{editIndex !== null ? "Edit Entry" : "Add CaseCash Entry"}</h2>

          <form className="transactions-form" onSubmit={handleSubmit}>
            <div className="form-group">
              <label>Date</label>
              <input type="date" name="date" value={form.date} onChange={handleChange} />
            </div>

            <div className="form-group">
              <label>Where did you spend?</label>
              <input
                type="text"
                name="place"
                value={form.place}
                placeholder="e.g. Starbucks, Grab-It"
                onChange={handleChange}
              />
            </div>

            <div className="form-group">
              <label>Amount ($)</label>
              <input
                type="number"
                step="0.01"
                name="amount"
                value={form.amount}
                onChange={handleChange}
              />
            </div>

            <button className="add-btn" type="submit">
              {editIndex !== null ? "Save Changes" : "Add Entry"}
            </button>

            {editIndex !== null && (
              <button type="button" className="delete-inside-btn" onClick={handleDelete}>
                Delete Entry
              </button>
            )}
          </form>
        </div>

        {/* TABLE */}
        <div className="transactions-table-section">
          <div className="table-header-row">
            <h2>CaseCash History</h2>

            <button
              className="edit-mode-btn"
              onClick={() => {
                setEditMode(!editMode);
                setEditIndex(null);
                setForm({ date: "", place: "", amount: "" });
              }}
            >
              {editMode ? "Done Editing" : "Edit"}
            </button>
          </div>

          {caseCashEntries.length === 0 ? (
            <p className="no-data">No CaseCash entries yet.</p>
          ) : (
            <table className="transactions-table">
              <thead>
                <tr>
                  <th>Date</th>
                  <th>Location</th>
                  <th>Amount ($)</th>
                </tr>
              </thead>
              <tbody>
                {caseCashEntries.map((entry, index) => (
                  <tr
                    key={index}
                    className={editMode ? "row-editable" : ""}
                    onClick={() => beginEdit(index)}
                  >
                    <td>{entry.date}</td>
                    <td>{entry.place}</td>
                    <td className="negative">${parseFloat(entry.amount).toFixed(2)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </main>

      <footer className="transactions-footer">
        © SpartanSpend | 2025 CSDS 395 Senior Project
      </footer>
    </div>
  );
}

export default CaseCash;
