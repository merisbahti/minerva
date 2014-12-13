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
  $("#resultsP").css("display","");
  $("#resultsTA").css("display","none");
  $("#resultsRTA").css("display","none");
}
$( "#tabResultsP" ).click(function(event) {
  event.preventDefault();
  switchToP();
});

var switchToTA = function() {
  $("#tabResultsP").attr("class", "");
  $("#tabResultsTA").attr("class", "active");
  $("#tabResultsRTA").attr("class", "");
  $("#resultsP").css("display","none");
  $("#resultsTA").css("display","");
  $("#resultsRTA").css("display","none");
}
$( "#tabResultsTA" ).click(function(event) {
  event.preventDefault();
  switchToTA();
});

var switchToRTA = function() {
  $("#tabResultsP").attr("class", "");
  $("#tabResultsTA").attr("class", "");
  $("#tabResultsRTA").attr("class", "active");
  $("#resultsP").css("display","none");
  $("#resultsTA").css("display","none");
  $("#resultsRTA").css("display","");
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
    ).appendTo("#spinner")
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
  $("#spinner").empty();
}

$("#search").click(function(){
  $("#search").val('');
});

var trunc = function(dub, decs) {
  return Math.floor(dub * Math.pow(10, decs)) / Math.pow(10, decs);
}

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
      if (data.length == 0 || (data["topAnswers"].length == 0 && data["rankedTopAnswers"].length == 0 && data["paragraphs"].length == 0)) { 
          addError("Inga träffar.", "Prova med en annan fråga?");
        } else {
          removeResults()
          $("ul").show()
          $("#tabResultsRTAAmount").text(data["rankedTopAnswers"].length);
          $("#tabResultsTAAmount").text(data["topAnswers"].length);
          $("#tabResultsPAmount").text(data["paragraphs"].length);
          if (data["paragraphs"].length == 0) {
            addResult("No paragraphs found, try another query?", "" ,"#resultsP")
          } else {
            $.each(data["paragraphs"], function(i, item) {
              addResult(item.title, item.text, "#resultsP")
            }); 
          }
          $.each(data, function(i, item) { console.log(item.length) })
          if (data["rankedTopAnswers"].length == 0) {
            addResult("The reranker couldn't find any candidates, try another query?","", "#resultsRTA")
          } else {
            $.each(data["rankedTopAnswers"], function(i, item) {
              addResult(item.word, "Score: " + trunc(item.score, 2), "#resultsRTA")
            }); 
          }
          if (data["topAnswers"].length == 0) {
            addResult("The POS-tagger couldn't find any candidates, try another query?", "", "#resultsTA")
          } else {
            $.each(data["topAnswers"], function(i, item) {
              addResult(item.word, "Score: " + trunc(item.score, 2), "#resultsTA")
            }); 
          }
      }
    }).error(
      function() {
        addError("Kommunikationsfel.", "Ett fel har uppstått och servern kan inte nås.")
      }
    );
  }
}); 



