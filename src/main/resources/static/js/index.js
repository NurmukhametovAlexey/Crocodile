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
}

function closeForm(formName) {
    document.getElementById(formName).style.display = "none";
}




