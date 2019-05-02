$(document).ready(function () {
    $(".alert").hide();

    $("button").click(function(event){
        url = $(event.target).attr("href");
        getAjaxMessage(url);
    });

    $("ul.tree a").click(function(event){
        path = $(event.target).attr("data-path");
        loadImages(path);
    });

    $(document).ajaxError(function( event, jqxhr, settings, thrownError ) {
        $(".alert-danger").text("Request can not complete: " + jqxhr.status + " " + jqxhr.statusText);
        $(".alert-danger").show();
        $(".alert-danger").fadeOut(2000);
    });

    function getAjaxMessage(url) {
        $.getJSON({
            url: url,
            data: null,
            success: function (result) {
                $(".alert-primary").text(result.message);
                $(".alert-primary").show();
                $(".alert-primary").fadeOut(2000);
            },
        });
    }

    function loadImages(path) {
        $.getJSON({
            url: "/api/load-images",
            data: {dir: path},
            success: function (result) {
                $("div.thumbnails").empty();
                result.forEach(function(element) {
                    $("div.thumbnails").append("<img class=\"img-thumbnail\" src=\"/f/"+ element.source + "\"/>")
                })
            },
        });
    }

});
