$( document ).ready(function() {

    console.log("hello " + currentUser);

    $("#btn-game-start").click(function () {
        openForm("form-game-start");
    });

    $("#form-game-start-close").click(function () {
        closeForm("form-game-start");
    });

    $("#btn-game-connect").click(function () {
        openForm("form-game-connect");
    });

    $("#form-game-connect-close").click(function () {
        closeForm("form-game-connect");
    });

});


function openForm(formName) {
    document.getElementById(formName).style.display = "block";
    document.getElementById("index-page").style.opacity = "0.4";
    document.getElementById("index-page").style.filter = "gray";
    document.getElementById("index-page").style.pointerEvents = "none";
}

function closeForm(formName) {
    document.getElementById(formName).style.display = "none";
    document.getElementById("index-page").style.opacity = "1.0";
    document.getElementById("index-page").style.filter = "none";
    document.getElementById("index-page").style.pointerEvents = "auto";
}




