/**
 * @module db
 * @description Initializes and exports a MySQL database connection.
 */
const mysql = require('mysql2');
require('dotenv').config();

/**
 * MySQL database connection.
 * @type {mysql.Connection}
 */
const db = mysql.createConnection({
    host: '127.0.0.1',
    user: 'root',
    password: 'Group7',
    database: 'budget'
});

// Connect to MySQL
db.connect((err) => {
    if (err) {
        console.error("Database connection failed:", err);
    } else {
        console.log("Connected to MySQL database!");
    }
});

// Export connection for use in other files
module.exports = db;