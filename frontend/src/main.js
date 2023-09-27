import "fomantic-ui/dist/semantic.min"
import "fomantic-ui/dist/semantic.min.css"

window.search = search

async function search() {
  let author = document.getElementById("author-input").value
  let words = document.getElementById("words-input").value
  let response = await fetch(`search?author=${author}&words=${words}`)
  let json = await response.json()
  console.log(json)
}