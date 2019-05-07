$(document).ready(function () {
    $(".alert").hide();

    // toolbar buttons
    $(".button-href").click(function(event){
        url = $(event.target).attr("href");
        buttonAction(url);
    });

    // directories tree view toggle
    $(".toggle-tree").click(function(){
        $("div.tree").toggle();
    });

    // directory checkbox handler
    $("div.tree input[type='checkbox']").change(function(){
        toggleDirectory(this);
    });

    // directory on click loads images preview
    $("ul.tree a").click(function(event){
        path = $(event.target).attr("data-path");
        loadImages(path);
    });

    // general errors xhr requests handler
    $(document).ajaxError(function( event, jqxhr, settings, thrownError ) {
        $(".alert-danger").text("Request can not complete: " + jqxhr.status + " " + jqxhr.statusText);
        $(".alert-danger").show();
        $(".alert-danger").fadeOut(2000);
    });

    // toolbar button's action requesting
    function buttonAction(url) {
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

    // loads images preview of a directory
    function loadImages(path) {
        $.getJSON({
            url: "/api/list-previews",
            data: {dir: path},
            success: function (result) {
                $("div.thumbnails").empty();
                result.forEach(function(element) {
                    n = $( "<a href=\"/f/" + element.source +"\" target=\"blank\"><img class=\"img-thumbnail\" src=\"/preview?image=" + element.source + "\"/></a>"
                    );
                    $("div.thumbnails").append(n);
                })
            },
        });
    }

    // directory checkbox action
    function toggleDirectory(checkbox) {
        $.getJSON({
            url: "/api/watch-directory",
            data: {dir: $(checkbox).attr("data-path")},
            success: function (result) {
                checkbox.prop("checked", !checkBoxes.prop("checked"));
            },
        });
    }
});
