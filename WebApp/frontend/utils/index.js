export const getPageNumber = () => {
  //Get page number from query parameter
  const queryParams = new URLSearchParams(window.location.search)
  const page = parseInt(queryParams.get('page')) || 1
  return page
}
