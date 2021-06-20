const url = 'http://localhost:8080';

$( document ).ready(function() {

    console.log("hello " + currentUser);


    $("#btn-game-start").click(function () {
        alert("btn-game-start");

        $.ajax({
            type: 'POST',
            url: url + "/game/start",
            dataType: "json",
            contentType: "application/json",
            //headers: "X-CSRF-Token: ",
            data: JSON.stringify({
                //"creatorLogin": "Bob",
                "difficulty": 2
            }),
            success: function (data) {
                console.log(data);
                let gameUUID = data.gameUUID;
                window.location.href = "/game?uuid="+gameUUID;
            },
            error: function () {
                window.location.href = "/login";
            }
        });
    });

    $("#btn-game-connect").click(function () {
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
        });
});




