const url = 'http://localhost:8080';

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

    /*$("#btn-game-connect").click(function () {
        let gameUUID = document.getElementById("input_connect_uuid").value;

        console.log("input: " + gameUUID);

        $.ajax({
            type: 'POST',
            url:url + "/game/connect",
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify({
                "gameUUID": gameUUID,
                //"newPlayerLogin": "Alice",
                "playerRole": "GUESSER"
            }),
            success: function (data) {
                console.log(data);
                window.location.href = "/game?uuid="+gameUUID;
            },
            error: function (error) {
                console.log(error);
                window.location.href = "/login";
            }
        });
        });*/
});


function openForm(formName) {
    document.getElementById(formName).style.display = "block";
}

function closeForm(formName) {
    document.getElementById(formName).style.display = "none";
}




