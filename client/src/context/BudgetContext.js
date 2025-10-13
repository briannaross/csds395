import React, { createContext, useState } from "react";

export const BudgetContext = createContext();

export const BudgetProvider = ({ children }) => {
  const [transactions, setTransactions] = useState([]);

  const [balances, setBalances] = useState({
    caseCash: 0,
    personalFunds: 0,
  });

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

  return (
    <BudgetContext.Provider
      value={{ transactions, addTransaction, balances }}
    >
      {children}
    </BudgetContext.Provider>
  );
};
