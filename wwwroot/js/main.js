$('#search').keypress(function (e) {
 var key = e.which;
 if(key == 13)  // the enter key code
  {
    $("#gosearch").trigger("click");
    return false;  
  }
});   

var addResult = function(head, foot) {   
  $(function() {
      $("<div>").attr("style", "margin-top: 20px").html(
        $("<div>").attr("class", "panel panel-default").html(
          [
            $("<div>").attr({
                "class" : "panel-body",
                "style" : "font-weight: bold"
            }).text(head),
            $("<div>").attr({
                "class" : "panel-footer" 
            }).text(foot)
          ]
      )).appendTo("#results");
  });
}

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
 //<div class="alert alert-danger" role="alert">...</div>
  removeResults()
  $(function() {
    $("<div>").attr("class", "alert alert-danger").html(
      [
        $("<b>").text(title + " "),
        $("<span>").text(text)
      ]
    ).appendTo("#results");
  });
}

var removeResults = function() {
  $("#results").empty();
  /*var childr = $("#results").children()
  $.each(childr, function (idx, val) {
    $(this).slideDown(250, function() {
      $(this).remove();
    });
  });*/
}

$("#gosearch").click(function(){
  var searchText = $("#search").val()
  if (/^\s*$/.test(searchText)) {
    addError("Tom sökning.", "prova att skriva lite mer i sökrutan!")
  } else {
    removeResults()
    showSpinner()
    //$.getJSON("http://localhost:8081/?q="+$("#search").val(), function( data ) {
    $.getJSON("/query/?q="+$("#search").val(), function( data ) {
      if (data.length == 0) { 
          addError("Inga träffar.", "Prova med en annan fråga?");
        } else {
          removeResults()
          $.each(data, function(i, item) {
            addResult(item.title, item.text)
          }); 
      }
    }).error(
      function() {
        addError("Kommunikationsfel.", "Ett fel har uppstått och servern kan inte nås.")
      }
    );
  }
  $("#search").val('');
}); 



