import pool from '../../utils/db.js'
import { ITEMS_PER_PAGE } from '../../utils/const.js'

/**
 *
 * @desc    Get cases variants for comparison
 * @route   GET /api/case/:page
 * @access  Public
 * @param {*} req
 * @param {*} res
 * @return  {Object} { rows }
 * @return  {Object} { error }
 */
export const getCases = async (req, res) => {
  try {
    let query = ''
    if (req.query.limited) {
      const limit = parseInt(req.query.limited)
      query = `
        SELECT c.phone_model, cv.*, co.name, co.price
        FROM cases_variants AS cv
        JOIN \`case\` AS c ON cv.case_id = c.id
        JOIN comparison AS co ON cv.id = co.case_variant_id
        LIMIT ${limit}
      `
    } else {
      const page = parseInt(req.params.page) || 1

      // Query to get the total number of cases
      const totalCountQuery = 'SELECT COUNT(*) AS total FROM cases_variants'
      const [totalCountResult] = await pool.query(totalCountQuery)
      const totalCount = totalCountResult[0].total

      if (isNaN(page) || page < 1 || page > Math.ceil(totalCount / ITEMS_PER_PAGE)) {
        return res.status(400).json({ error: 'Invalid page parameter' })
      }

      const offset = (page - 1) * ITEMS_PER_PAGE

      // get all cases variants
      query = `
        SELECT c.phone_model, cv.*, co.name, co.price
        FROM cases_variants AS cv
        JOIN \`case\` AS c ON cv.case_id = c.id
        JOIN comparison AS co ON cv.id = co.case_variant_id
        LIMIT ${ITEMS_PER_PAGE} OFFSET ${offset}
      `
    }

    const [rows] = await pool.query(query)
    res.json(rows)
  } catch (error) {
    console.error('Error fetching case variants:', error.message)

    // Handle specific errors and send appropriate response
    if (error instanceof SyntaxError || error instanceof TypeError) {
      res.status(400).json({ error: 'Bad request syntax or unsupported query parameter' })
    } else {
      res.status(500).json({ error: 'Internal Server Error' })
    }
  }
}
