import chai from 'chai'
import supertest from 'supertest'
import app from '../../index.js'

const expect = chai.expect
const request = supertest(app)

describe('GET /api/search/:page', () => {
  it('should handle missing search query parameter and return 404 status', async () => {
    const response = await request.get('/api/search/')
    expect(response.status).to.equal(404)
  })
})
