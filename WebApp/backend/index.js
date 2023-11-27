import express from 'express';
import cors from 'cors';
import caseRoutes from './routes/caseRoutes/index.js'

const app = express();
const port = 3001;

// Enable CORS to allow requests from the frontend
app.use(cors());

// For getting cases
app.use('/case', caseRoutes);

// Start the server
app.listen(port, () => console.log(`Server is running on http://localhost:${port}`));
