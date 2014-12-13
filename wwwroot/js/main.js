$('#search').keypress(function (e) {
 var key = e.which;
 if(key == 13)  // the enter key code
  {
    $("#gosearch").trigger("click");
    return false;  
  }
});   

var addResult = function(head, foot, id) {   
  $(function() {
      $("<div>").attr("class", "list-group-item").html(
        [
          $("<h4>").attr({
              "class" : "list-group-item-heading"
          }).text(head),
          $("<p>").attr({
              "class" : "list-group-item-text" 
          }).text(foot)
        ]
      ).appendTo(id);
  });
}

var switchToP = function() {
  $("#tabResultsP").attr("class", "active");
  $("#tabResultsTA").attr("class", "");
  $("#tabResultsRTA").attr("class", "");
}
$( "#tabResultsP" ).click(function(event) {
  event.preventDefault();
  switchToP();
});

var switchToTA = function() {
  $("#tabResultsP").attr("class", "");
  $("#tabResultsTA").attr("class", "active");
  $("#tabResultsRTA").attr("class", "");
}
$( "#tabResultsTA" ).click(function(event) {
  event.preventDefault();
  switchToTA();
});

var switchToRTA = function() {
  $("#tabResultsP").attr("class", "");
  $("#tabResultsTA").attr("class", "");
  $("#tabResultsRTA").attr("class", "active");
}

$( "#tabResultsRTA" ).click(function(event) {
  event.preventDefault();
  switchToRTA();
});

$( "#tabResultsRTA" ).click(function() {
  switchToRTA();
});

var showSpinner = function () {
  $(function () {
    $("<div>").attr("class", "spinner").html(
      [
        $("<div>").attr("class", "double-bounce1"),
        $("<div>").attr("class", "double-bounce2")
      ]
    ).appendTo("#results")
  })
}

var addError = function(title, text) {
  $("ul").hide()
  removeResults()
  $(function() {
    $("<div>").attr("class", "alert alert-danger").html(
      [
        $("<b>").text(title + " "),
        $("<span>").text(text)
      ]
    ).appendTo("#error");
  });
}

var removeResults = function() {
  $("#error").empty();
  $("#resultsP").empty();
  $("#resultsTA").empty();
  $("#resultsRTA").empty();
}

$("#search").click(function(){
  $("#search").val('');
});

$("#gosearch").click(function(){
  var searchText = $("#search").val()
  if (/^\s*$/.test(searchText)) {
   addError("Tom sökning.", "Prova att skriva lite mer i sökrutan!")
  } else {
    removeResults()
    $("ul").hide()
    showSpinner()
    //$.getJSON("http://localhost:8081/?q="+$("#search").val(), function( data ) {
    $.getJSON("/query/?q="+$("#search").val(), function( data ) {
      if (data.length == 0) { 
          addError("Inga träffar.", "Prova med en annan fråga?");
        } else {
          removeResults()
          $("ul").show()
          $.each(data, function(i, item) {
            addResult(item.title, item.text, "#resultsP")
          }); 
      }
    }).error(
      function() {
        addError("Kommunikationsfel.", "Ett fel har uppstått och servern kan inte nås.")
      }
    );
  }
}); 



