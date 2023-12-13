import { expect } from 'chai'
import pool from '../../utils/db.js'

describe('Database Connection Pool', () => {
  it('should successfully create a connection pool', () => {
    expect(pool).to.exist
  })

  it('should successfully acquire a connection from the pool', async () => {
    const connection = await pool.getConnection()
    expect(connection).to.exist
    connection.release()
  })

  it('should successfully execute a query using the connection pool', async () => {
    const [rows, fields] = await pool.execute('SELECT 1 + 1 as sum')
    expect(rows).to.exist
    expect(fields).to.exist
    expect(rows[0].sum).to.equal(2)
  })

  it('should successfully release a connection back to the pool', async () => {
    const connection = await pool.getConnection()
    connection.release()
  })

  it('should handle errors when selecting from non_existent_table', async () => {
    try {
      await pool.execute('SELECT * FROM non_existent_table')
    } catch (error) {
      // Ensure that an error is caught
      expect(error).to.exist
    }
  })
})
