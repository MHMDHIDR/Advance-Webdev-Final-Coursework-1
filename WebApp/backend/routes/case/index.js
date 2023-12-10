import express from 'express'
import { getCases } from './getCases.js'
import { getCaseById } from './getCaseById.js'
import { getSimilarCases } from './getSimilarCases.js'

const router = express.Router()

// Use the route handlers with router.get
router.get('/:page?', getCases)
router.get('/byId/:id', getCaseById)
router.get('/similar/:id', getSimilarCases)

export default router
