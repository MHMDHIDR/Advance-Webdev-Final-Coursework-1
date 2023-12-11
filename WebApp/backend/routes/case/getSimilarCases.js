import pool from '../../utils/db.js'

/**
 * Get similar cases
 * @param {import('express').Request} req
 * @param {import('express').Response} res
 * @returns {Promise<void>}
 */
export const getSimilarCases = async (req, res) => {
  const { id: caseId } = req.params

  try {
    // Validate caseId is a positive integer
    if (!/^\d+$/.test(caseId) || parseInt(caseId) < 1) {
      return res.status(400).json({ error: 'Invalid case ID' })
    }

    const similarCasesQuery = `
      SELECT cv.*, c.phone_model, co.name, co.price, co.url, co.website
      FROM cases_variants AS cv
        JOIN \`case\` AS c ON cv.case_id = c.id
        JOIN comparison AS co ON cv.id = co.case_variant_id
      WHERE c.id != ? 
        AND cv.id != ? 
        AND cv.case_id = (SELECT case_id FROM cases_variants WHERE id = ? LIMIT 1)
        AND co.website NOT IN (SELECT website FROM comparison WHERE case_variant_id = ?)
      GROUP BY co.website, cv.case_id
      ORDER BY RAND()
      LIMIT 4;
    `

    const [similarCasesRows] = await pool.query(similarCasesQuery, [
      caseId, // current case_id
      caseId, // current case_variant_id
      caseId, // current case_variant_id for the color subquery
      caseId, // current case_variant_id for the website subquery
      caseId // current case_id for the additional condition
    ])

    // If no similar cases are found, return an empty array
    if (similarCasesRows.length === 0) {
      return res.status(200).json([])
    } else {
      return res.status(200).json(similarCasesRows)
    }
  } catch (error) {
    console.error('Error fetching similar cases:', error.message)
    res.status(500).json({ error: 'Internal Server Error' })
  }
}
