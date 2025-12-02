import React, { createContext, useState } from "react";

export const BudgetContext = createContext();

export const BudgetProvider = ({ children }) => {
  const [transactions, setTransactions] = useState([]);
  const [caseCashEntries, setCaseCashEntries] = useState([]);

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

  const editTransaction = (index, updatedTransaction) => {
    setTransactions((prev) => {
      const newTransactions = [...prev];
      const oldTransaction = prev[index];

      // Adjust balance by reversing old transaction and applying new one
      setBalances((prevBalances) => {
        let newPersonalFunds = prevBalances.personalFunds;

        // Reverse old transaction
        if (oldTransaction.type === "Income") {
          newPersonalFunds -= parseFloat(oldTransaction.amount);
        } else {
          newPersonalFunds += parseFloat(oldTransaction.amount);
        }

        if (updatedTransaction.type === "Income") {
          newPersonalFunds += parseFloat(updatedTransaction.amount);
        } else {
          newPersonalFunds -= parseFloat(updatedTransaction.amount);
        }

        return {
          ...prevBalances,
          personalFunds: newPersonalFunds,
        };
      });

      newTransactions[index] = updatedTransaction;
      return newTransactions;
    });
  };

  const deleteTransaction = (index) => {
    setTransactions((prev) => {
      const transaction = prev[index];

      setBalances((prevBalances) => {
        if (transaction.type === "Income") {
          return {
            ...prevBalances,
            personalFunds: prevBalances.personalFunds - parseFloat(transaction.amount),
          };
        } else {
          return {
            ...prevBalances,
            personalFunds: prevBalances.personalFunds + parseFloat(transaction.amount),
          };
        }
      });

      return prev.filter((_, i) => i !== index);
    });
  };

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
        editTransaction,
        deleteTransaction,
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
