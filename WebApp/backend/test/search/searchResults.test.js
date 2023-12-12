import supertest from 'supertest'
import { expect } from 'chai'
import app from '../../index.js'

const request = supertest(app)

/**
 * Testing my searchResults.js route ( GET /search/:page ) endpoint:
 */
describe('GET /search/:page(\\d+)', () => {
  it('should return search results', async () => {
    const res = await request.get('/search/1?query=iphone%2011')
    expect(res.status).to.equal(200)
    expect(res.body).to.be.an('array')
    expect(res.body.length).to.be.greaterThan(0)
  })

  it('should return 500 error if no query is provided', async () => {
    const res = await request.get('/search/1')
    expect(res.status).to.equal(500)
    expect(res.body).to.be.an('object')
    expect(res.body).to.have.property('error')
  })

  it('should return 500 error if query parameter is not provided or is empty', async () => {
    const res = await request.get('/search/1?query=')
    expect(res.status).to.equal(500)
    expect(res.body).to.be.an('object')
    expect(res.body).to.have.property('error')
  })

  it('should return 404 error if page is not a number', async () => {
    const response = await request.get('/search/one?query=iphone%2012')
    expect(response.status).to.equal(404)
  })

  it('should return 404 error if page is not provided', async () => {
    const res = await request.get('/search/?query=iphone')
    expect(res.status).to.equal(404)
    expect(res.text).to.include('Cannot GET /search/')
  })
})
