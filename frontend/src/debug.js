window.searchAuthor = searchAuthor

async function searchAuthor(author) {
  let response = await fetch(`/debug?author=${author}`)
  let json = await response.json()
  console.log(`Authors found: ${json.length}`)
  for (let {author, books} of json) {
    console.log(author)
    for (let {id, title} of books) {
      console.log(`  ${id}: ${title}`)
    }
  }
}