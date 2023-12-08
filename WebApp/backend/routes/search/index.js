import express from 'express'
import { searchResults } from './searchResults.js'

const router = express.Router()

// Use the route handlers with router.get
router.get('/:page?', searchResults)

export default router
