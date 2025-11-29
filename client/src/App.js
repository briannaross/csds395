import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./login/Login";
import Dashboard from "./login/Dashboard";
import Transactions from "./login/Transactions";
import { BudgetProvider } from "./context/BudgetContext";
import MealPlanPage from "./login/MealPlanPage";

function App() {
  return (
    <BudgetProvider>
      <Router>
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/transactions" element={<Transactions />} />
          <Route path="/meal-plan" element={<MealPlanPage />} />
        </Routes>
      </Router>
    </BudgetProvider>
  );
}

export default App;
