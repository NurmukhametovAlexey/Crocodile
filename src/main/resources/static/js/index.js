$( document ).ready(function() {

    $("#btn-game-create").click(function () {
        if(!currentUser) {
            alert("Please, login!");
        }
        else if (activeGameUUID) {
            alert("Please, finish your last match!");
        } else {
            openForm("form-game-create");
        }
    });

    $("#form-game-create-close").click(function () {
        closeForm("form-game-create");
    });

    $("#btn-game-connect").click(function () {
        if(!currentUser) {
            alert("Please, login!");
        }
        else if(activeGameUUID) {
            console.log(activeGameUUID);
            openForm("form-game-reconnect");
        } else {
            console.log("gameUUID is null");
            openForm("form-game-connect");
        }

    });

    $("#form-game-connect-close").click(function () {
        closeForm("form-game-connect");
    });

    $("#form-game-reconnect-close").click(function () {
        closeForm("form-game-reconnect");
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




