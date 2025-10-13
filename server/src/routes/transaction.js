/**
 * @module routes/transaction
 * @description Routes related to the transaction page
 */ 

const express = require('express');
const db = require('../db'); // Import database connection
require('dotenv').config();
const { v4: uuidv4 } = require("uuid");

const router = express.Router();

/**
 * Gets transaction history for a user.
 * @route GET /:userId
 * @access Private
 */
router.get("/:userId", (req, res) => {
    const { userId } = req.params;

    db.query(
        `SELECT transactions.category, transactions.type, transactions.amount
     FROM transactions 
     WHERE transactions.userID = ? 
     ORDER BY transactions.date DESC`,
        [userId],
        (err, results) => {
            if (err) return res.status(500).json({ message: "Failed to fetch past transactions", error: err });
            res.json(results);
        }
    );
});