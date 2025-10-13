import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./login/Login";
import Dashboard from "./login/Dashboard";
import Transactions from "./login/Transactions";
import { BudgetProvider } from "./context/BudgetContext";

function App() {
  return (
    <BudgetProvider>
      <Router>
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/transactions" element={<Transactions />} />
        </Routes>
      </Router>
    </BudgetProvider>
  );
}

export default App;
