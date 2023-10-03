import "fomantic-ui/dist/semantic.min"
import "fomantic-ui/dist/semantic.min.css"

window.search = search

async function search() {
  let author = document.getElementById("author-input").value
  let words = document.getElementById("words-input").value
  let params = `search?author=${author}&q=${words}`
  let response = await fetch(params)
  let json = await response.json()
  if(json.empty()) {

  }
  console.warn(json)
}