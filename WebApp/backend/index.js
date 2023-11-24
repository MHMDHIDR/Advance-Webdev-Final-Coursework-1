import express from 'express';
import cors from 'cors';
import pool from './utils/db.js'

const app = express();
const port = 3001;

// Enable CORS to allow requests from the frontend
app.use(cors());

// Define API endpoints
app.get('/case', async (req, res) => {
  try {
    const [rows, fields] = await pool.query('SELECT * FROM `cases_variants`');
    res.json(rows);
  } catch (error) {
    console.error('Error fetching case:', error.message); // Log the error message
    res.status(500).json({ error: 'Internal Server Error' });
  }
});

// Start the server
app.listen(port, () => {
  console.log(`Server is running on http://localhost:${port}`);
});
