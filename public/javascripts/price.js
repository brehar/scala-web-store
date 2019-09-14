function loadPrice(doc) {
  $.get("http://localhost:9000/rnd/rxbat", function(response) {
    doc.getElementById("price").value = parseFloat(response)
  }).fail(function(e) {
    alert('Oops! Was not able to call http://localhost:9000/rnd/rxbat. Error: ' + e.statusText);
  });
}
