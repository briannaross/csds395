import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./login/Login";
import Dashboard from "./login/Dashboard";
import Transactions from "./login/Transactions";
import { BudgetProvider } from "./context/BudgetContext";
import { MealPlanProvider } from "./context/MealPlanContext";
import MealPlanPage from "./login/MealPlanPage";
import CaseCash from "./login/CaseCash";   

function App() {
  return (
    <BudgetProvider>
      <MealPlanProvider>
        <Router>
          <Routes>
            <Route path="/" element={<Login />} />
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/transactions" element={<Transactions />} />
            <Route path="/meal-plan" element={<MealPlanPage />} />
            <Route path="/casecash" element={<CaseCash />} />
          </Routes>
        </Router>
      </MealPlanProvider>
    </BudgetProvider>
  );
}

export default App;
