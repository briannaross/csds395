import React, { createContext, useState } from "react";

export const BudgetContext = createContext();

export const BudgetProvider = ({ children }) => {
  const [transactions, setTransactions] = useState([]);

  const [caseCashEntries, setCaseCashEntries] = useState([]);

  const [balances, setBalances] = useState({
    caseCash: 0,
    personalFunds: 0,
  });

  // --------------------------
  // PERSONAL FUNDS TRANSACTIONS
  // --------------------------
  const addTransaction = (transaction) => {
    setTransactions((prev) => [...prev, transaction]);

    setBalances((prev) => {
      if (transaction.type === "Income") {
        return {
          ...prev,
          personalFunds: prev.personalFunds + parseFloat(transaction.amount),
        };
      } else {
        return {
          ...prev,
          personalFunds: prev.personalFunds - parseFloat(transaction.amount),
        };
      }
    });
  };

  // --------------------------
  // CASECASH FUNCTIONS
  // --------------------------
  const addCaseCashEntry = (entry) => {
    setCaseCashEntries((prev) => [...prev, entry]);
  };

  const editCaseCashEntry = (index, updated) => {
    setCaseCashEntries((prev) => {
      const newEntries = [...prev];
      newEntries[index] = updated;
      return newEntries;
    });
  };

  const deleteCaseCashEntry = (index) => {
    setCaseCashEntries((prev) => prev.filter((_, i) => i !== index));
  };

  return (
    <BudgetContext.Provider
      value={{
        transactions,
        addTransaction,
        balances,
        caseCashEntries,
        addCaseCashEntry,
        editCaseCashEntry,
        deleteCaseCashEntry,
      }}
    >
      {children}
    </BudgetContext.Provider>
  );
};
