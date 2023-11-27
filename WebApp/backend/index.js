import express from 'express';
import cors from 'cors';
import caseRoutes from './routes/case/index.js'
import searchRoutes from './routes/search/index.js'

const app = express();
const port = 3001;

// Enable CORS to allow requests from the frontend
app.use(cors());

// For getting cases
app.use('/case', caseRoutes);
app.use('/search', searchRoutes);

// Start the server
app.listen(port, () => console.log(`Server is running on http://localhost:${port}`));
