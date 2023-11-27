import express from 'express';
import { getCases } from './getCases.js';
import { getCaseById } from './getCaseById.js';
import { getSimilarCases } from './getSimilarCases.js';

const router = express.Router();

// Use the route handlers with router.get
router.get('/', getCases);
router.get('/:id', getCaseById);
router.get('/similar/:phone_model', getSimilarCases);

export default router;


